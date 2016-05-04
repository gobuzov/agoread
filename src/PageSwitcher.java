import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 16.10.14
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class PageSwitcher extends AbstractView{
    public final static int NOT_DEFINED = 0, START_ANIMATION = 1, ANIMATION = 2, STOP_ANIMATION = 3;
    TextView textview;
    int W, H;
    boolean needRepaint;
    //
    int hidex, oldOfset, newOfset, startx, mode = NOT_DEFINED;
    long start;
    Image oldPage, newPage;
    boolean landscape, rightToLeft;

    public PageSwitcher(TextView tv, boolean r2l, int ofset1, int ofset2, int sx){
        textview = tv;
        rightToLeft = r2l;
        oldOfset = ofset1;
        newOfset = ofset2;
        startx = sx;
    }
    public void init(int w, int h){// call: 1.on start,  2.changing screen's orientation
        FileLink fl = textview.getLink();
        fl.setOffset(oldOfset);
        textview.init(w, h);
        landscape = w > h;
        this.W = w; this.H = h;
        oldPage = Image.createImage(w, h);
        Graphics g = oldPage.getGraphics();
        g.setColor(App.getColor(c.COLOR_BG)-0x181818);
        g.fillRect(0, 0, W, H);
        textview.paint(g);
//
        newPage = Image.createImage(w, h);
        g = newPage.getGraphics();
        g.setColor(App.getColor(c.COLOR_BG)); // here - change offset
        g.fillRect(0, 0, W, H);
//
        fl.setOffset(newOfset);
        textview.initTextParams();
        textview.paint(g);
//
    }
    public void onExit(){
        textview.onExit();
    }
    public boolean updateAndRepaint(){
        if (START_ANIMATION==mode){
            start = System.currentTimeMillis();
            mode = ANIMATION;
        }else if (ANIMATION==mode){
            long dt = System.currentTimeMillis() - start;
            int newx = hidex + (int)((dt * dt)>>10);
            if (newx > W){
                newx = W;
                mode = STOP_ANIMATION;
            }
            hidex = newx;
        }else if (STOP_ANIMATION==mode){
            MyCanvas.getInstance().setView(textview);
            oldPage =null; newPage =null;
        }
        return true;
    }
    public void updateKey(int key){}
    public void updateTouch(int touchx, int touchy, int touchmode){
        if (c.TOUCH_MODE_RELEASED==touchmode){
            mode = START_ANIMATION;
        }else if (c.TOUCH_MODE_DRAGGED==touchmode){
            int newx = startx - touchx;
            hidex = newx;
        }
    }
    public void paint(Graphics g){
        g.drawImage(newPage, 0, 0, ANCHORi);
        int sign = rightToLeft ? -1: 1;
        g.drawImage(oldPage, hidex*sign, 0, ANCHORi);

        needRepaint = false;
    }
}
