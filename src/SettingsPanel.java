import javax.microedition.lcdui.*;
/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 12.10.14
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class SettingsPanel extends AbstractView{
    public int BG = 0x66aadd, FG = 0x444444, FOCUS = 0xff, FRAME = 0x4488bb, CURSOR = 0x88ccff;
    public int EXIT_KEY = '0';
    TextView textview;
    int Y, W, H, w, h, curpos, wc, hc, dx, dy;
    Font big = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE);
    Font medium = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
    Font small = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    Font[] fonts = {small, medium, big };
    boolean needRepaint;
    //
    int mode = 0, popy;//mode: 0-popup, 1-show, 2-popdown
    long start;
    Image bgImage, popImage;
    boolean landscape, stopAnimation;

    public SettingsPanel(TextView tv){ textview = tv; curpos=0;}

    public void init(int w, int h){// call: 1.on start,  2.changing screen's orientation
        textview.init(w, h);
        landscape = w > h;
        this.w = w;
        //this.h =  (landscape? 2 : 3) * (big.getHeight()+4);
        wc = w / (landscape ? 6 : 4);
        hc = (landscape ? w : h)/8;

        this.h =  (landscape? 2 : 3) * hc;
        this.W = w; this.H = h;
        dx = landscape ? wc : 0;
        dy = landscape ? 0 : hc;

        //Debug.log("big="+(big.getHeight()+4)+" medium="+medium.getHeight()+" hc="+hc);
        Y = H-this.h;
        if (1!=mode){
            bgImage = Image.createImage(w, h);
            Graphics g = bgImage.getGraphics();
            g.setColor(App.getColor(c.COLOR_BG));
            g.fillRect(0, 0, W, H);
            textview.paint(g);
            popImage = Image.createImage(w, this.h);
            popy = 0==mode ? H : H-this.h;
            g = popImage.getGraphics();
            paintPanel(g, 0);
            stopAnimation = false;
            start = System.currentTimeMillis();
        }
    }
    public void onExit(){
        textview.onExit();
    }
    public boolean updateAndRepaint(){
        if (1!=mode){
            if (stopAnimation){
                if (0==mode){
                    mode = 1;
                }else
                    MyCanvas.getInstance().setView(textview);
                bgImage=null; popImage=null;
                return true;
            }
            long dt = System.currentTimeMillis() - start;
            int dy = 0==mode ? -1 : 1;
            int newy = popy + dy * (int)((dt * dt)>>10);
            if (newy > H){
                newy = H;
                stopAnimation = true;
            }else if (newy<H-h){
                newy = H - h;
                stopAnimation = true;
            }
            popy = newy;
            return true;
        }
        return needRepaint;
    }
    /* Portrait: 0369     Landscape: 012345
                 147A                6789AB
                 258B  */
    public void updateKey(int key){
        if (EXIT_KEY==key){
            mode = 2;
            init(W, H);
        }
        if (MyCanvas.KEY_FIRE==key){
            int x = landscape ? curpos%6 : curpos/3;
            int y = landscape ? curpos/6 : curpos%3;
            updateValues(x, y);
            return;
        }
        int newcur = curpos;
        if (MyCanvas.KEY_RIGHT==key){
            newcur += landscape ? 1:3;
            if (newcur>11)
                newcur%=3;
        }else if (MyCanvas.KEY_LEFT==key){
            int dx = landscape ? 1:3;
            newcur -= dx;
            if (newcur<0)
                newcur=12-dx;
        }else if (MyCanvas.KEY_DOWN==key){
            int dy = landscape ? 6 : 1;
            newcur +=dy;
            if (landscape && newcur>11){
                newcur -=12;
            }else if (!landscape && 0==newcur%3)
                newcur-=3;
        }else if (MyCanvas.KEY_UP==key){
            int dy = landscape ? 6 : 1;
            newcur -=dy;
            if (landscape && newcur<0)
                newcur+=12;
            else if (!landscape && 0==(newcur+dy)%3)
                newcur+=3;
        }
        curpos = newcur;
    }
    protected void updateValues(int x, int y){
        int id = landscape? (0==y ? (x<3 ? Res.tCOLORTHEME:Res.tBORDER) : (x<3 ? Res.tBETWEEN_LINE:Res.tFONT_SIZE)) :
                0==x ? Res.tCOLORTHEME : 1==x ? Res.tBORDER : 2==x ? Res.tBETWEEN_LINE : Res.tFONT_SIZE;
        int value = landscape ? x%3 : y;
        App.put(id, value);
        textview.initTextParams();
        needRepaint = true;
    }
    public void updateTouch(int touchx, int touchy, int touchmode){
        if (mode==1 && touchmode==c.TOUCH_MODE_RELEASED){
            if (touchy>Y && touchy<Y+h){// inside this Panel
                int wc = w / (landscape ? 6 : 4);
                int hc = h / (landscape ? 2 : 3);
                int x = touchx/wc;
                int y = (landscape? 1:2)-(Y-touchy+h)/hc;
                updateValues(x, y);
            }else {
                mode = 2;
                init(W, H);
            }
        }
    }
    protected void drawLetters(Graphics g, int id, int Y){
        int x = !landscape ? id * wc : 0==(id&1) ? 0 : w/2;
        int y =  landscape&(id>1) ? Y+hc : Y;

        int font_size = App.getInt(Res.tFONT_SIZE);
        String s = "A a";
        for(int i=0; i<3; i++, x+=dx, y+=dy){
            Font f = fonts[i];
            g.setFont(f);
            g.setColor(i==font_size ? FOCUS : FG);
            g.drawString(s, x+1+(wc - f.stringWidth(s))/2, y + (hc+f.getHeight())/2, ANCHOR);
        }
    }
    protected void drawLines(Graphics g, int id, int Y){
        int x = !landscape ? id * wc : 0==(id&1) ? 0 : w/2;
        int y =  landscape&(id>1) ? Y+hc : Y;

        int sz = hc-8;
        int between = App.getInt(Res.tBETWEEN_LINE);
        x += (wc-sz)/2;
        for(int i=0; i<3; i++, x+=dx, y+=dy){
            int yy = y + (hc - (i+3)*3)/2;
            g.setColor(i==between ? FOCUS : FG);
            for (int j=0; j<4; j++, yy+=i+3)
                g.drawLine(x, yy, x+sz, yy);
        }
    }
    protected void drawThemes(Graphics g, int id, int Y){
        int x = !landscape ? id * wc : 0==(id&1) ? 0 : w/2;
        int y =  landscape&(id>1) ? Y+hc : Y;

        int sz = medium.getHeight()-3;
        x += (wc-sz)/2;
        y += (hc-sz)/2;
        g.setFont(medium);
        int temp = App.getInt(Res.tCOLORTHEME);
        for(int i=0; i<3; i++, y+=dy, x+=dx){
            App.put(Res.tCOLORTHEME, i);
            g.setColor(App.getColor(c.COLOR_BG));
            g.fillRect(x, y, sz, sz);
            g.setColor(i==temp ? FOCUS : App.getColor(c.COLOR_FG));
            g.drawRect(x, y, sz, sz);
            if (i==temp)
                g.drawRect(x-1, y-1, sz+2, sz+2);
            g.setColor(App.getColor(c.COLOR_FG));
            g.drawChar('T', x+1+(sz-medium.charWidth('T'))/2, y+medium.getHeight(), ANCHOR);
        }
        App.put(Res.tCOLORTHEME, temp);
    }
    protected void drawPointRect(Graphics g, int X, int Y, int w, int h){
        int x = X;
        while (x<=X+w){
            g.drawLine(x, Y, x, Y);
            g.drawLine(x, Y+h, x, Y+h);
            x+=2;
        }
        int y = Y;
        while (y<=Y+h){
            g.drawLine(X, y, X, y);
            g.drawLine(X+w, y, X+w, y);
            y+=2;
        }
    }
    protected void drawBorders(Graphics g, int id, int Y){
        int x = !landscape ? id * wc : 0==(id&1) ? 0 : w/2;
        int y =  landscape&(id>1) ? Y+hc : Y;

        int border = App.getInt(Res.tBORDER);
        int sz = medium.getHeight()-3;
        x += (wc-sz)/2;
        y += (hc-sz)/2;
        for(int i=0; i<3; i++, x+=dx, y+=dy){
            g.setColor(i==border ? FOCUS : FG);
            g.drawRect(x, y, sz, sz);
            drawPointRect(g, x+i+2, y+i+2, sz-(i+2)*2, sz-(i+2)*2);
        }
    }
    /* Portrait: 0369     Landscape: 012345
                 147A                6789AB
                 258B  */
    protected void drawCursor(Graphics g, int Y){
        if (false==App.istouch){
            int x = (landscape ? curpos%6 : curpos/3)*wc;
            int y = Y + (landscape ? curpos/6 : curpos%3)*hc;
            g.setColor(CURSOR);
            g.fillRect(x+2, y+2, wc-3, hc-3);
        }
    }
    public void paint(Graphics g){
        if (1==mode){
            if (null!=textview)
                textview.paint(g);
            paintPanel(g, Y);
        } else {
            g.drawImage(bgImage, 0, 0, ANCHORi);
            g.drawImage(popImage, 0, popy, ANCHORi);
        }
        needRepaint = false;
    }
    protected void paintPanel(Graphics g, int Y){
        g.setColor(BG);
        g.fillRect(0, Y, w, h);
        drawCursor (g, Y);
//
        paintContent(g, Y);
//
        g.setColor(FRAME);
        for (int i=landscape?1:2; i>=1; i--)
            g.drawLine(0, Y+hc*i, w, Y+hc*i);
        for (int i=landscape?5:3; i>=1; i--)
            g.drawLine(wc*i, Y, wc*i, Y+h);
    }
    protected void paintContent(Graphics g, int Y){
        drawThemes (g, 0, Y);
        drawBorders(g, 1, Y);
        drawLines  (g, 2, Y);
        drawLetters(g, 3, Y);
    }
}
