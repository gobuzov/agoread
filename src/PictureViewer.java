/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 26.09.14
 * Time: 21:13
 * To change this template use File | Settings | File Templates.
 */
import javax.microedition.lcdui.*;
import java.util.Vector;

public class PictureViewer  extends AbstractView  implements CommandListener{
    AbstractView backView;
    String[] picNames;
    FileLink link;
    Image image;
    int imgId = 0, x, y, W, H;

    public PictureViewer(String[] pictures, FileLink link, AbstractView backView){
//        System.out.println("PictureViewer constructor");
        this.picNames = pictures;
        this.link = link;
        this.backView = backView;
        if (App.isAsha())
            MyCanvas.getInstance().setCommandListener(this);
        initImage();
    }
    public void init(int w, int h){
        W = w; H = h;
        if (null!=image){
            int iw = image.getWidth();
            int ih = image.getHeight();
            x = iw > W ? 0 : (iw-W)/2;
            y = ih > H ? 0 : (ih-H)/2;
        }
    }
    public void onExit(){
        link.saveToRms();
    }
    private long keyStart;
    private int x0, y0, dx, dy, tx, ty;
    private boolean keydown = false;
    public void updateKey(int key){
        if (MyCanvas.KEY_LEFT==key || MyCanvas.KEY_RIGHT==key || MyCanvas.KEY_UP==key || MyCanvas.KEY_DOWN==key){
            if (false==keydown){
                keydown = true;
                int iw = image.getWidth();
                if (((MyCanvas.KEY_LEFT==key&&(0==x || iw<W)) ||
                     (MyCanvas.KEY_RIGHT==key&&(x==iw-W || iw<W))))
                {
                    changeImage(MyCanvas.KEY_LEFT==key);
                    return;
                }
                x0 = x; y0 = y;
                keyStart = System.currentTimeMillis();
                dx = (iw<W) ? 0 : MyCanvas.KEY_LEFT==key ? -1 : MyCanvas.KEY_RIGHT==key ? 1 : 0;
                dy = (image.getHeight()<H) ? 0 : MyCanvas.KEY_UP==key ? -1 : MyCanvas.KEY_DOWN==key ? 1 : 0;
            }
        }else if (MyCanvas.KEY_FIRE==key){
            MyCanvas.getInstance().setView(backView);
        }else if (MyCanvas.KEY_OFF==key){
            keydown = false;
            dx = 0; dy = 0;
        }
    }
    private void changeImage(boolean toleft){
        int newId = imgId + ((toleft) ? -1 : 1);
        if (newId<0)
            newId = picNames.length-1;
        if (newId==picNames.length)
            newId = 0;
        if (newId!=imgId){
            imgId = newId;
            image = null;
            System.gc();
            initImage();
        }
    }
    private void setXY (int newx, int newy){
        int iw = image.getWidth();
        int ih = image.getHeight();
        if (iw>W){
            if (newx<0){
                newx = 0;
            }else if (newx>iw-W){
                newx=iw-W;
            }
            x = newx;
        }
        if (ih>H){
            if (newy<=0)
                newy = 0;
            if (newy>=ih-H)
                newy=ih-H;
            y = newy;
        }
    }
    boolean touchdown;
    public void updateTouch(int touchx, int touchy, int touchmode){
        if (c.TOUCH_MODE_PRESSED==touchmode){
            x0 = x; tx = touchx; y0 = x; ty = touchy;
            touchdown = true;
        }else if (c.TOUCH_MODE_DRAGGED==touchmode && touchdown){
//            System.out.println("dx="+(touchx-x0)+" dy="+(touchy-y0));
            int iw = image.getWidth();
            if ((((iw<W) || (0==x0)) && tx < touchx) ||
                (((iw<W) || (iw-W==x0)) && tx > touchx)){
                changeImage(tx < touchx);
                touchdown = false;
                return;
            }
            int newx = x0 + tx - touchx;
            int newy = y0 + ty - touchy;
            setXY(newx, newy);
        }else if (touchmode == c.TOUCH_MODE_RELEASED){
            touchdown = false;
        }
    }
    public boolean updateAndRepaint(){
        boolean res = dx!=0 || dy!=0;
        if (res){
            long dt = System.currentTimeMillis() - keyStart;
            int newx = x0 + dx * (int)((dt * dt)>>10);
            int newy = y0 + dy * (int)((dt * dt)>>10);
            setXY(newx, newy);
        }
        return res;
    }
    public void paint(Graphics g){
        if (null!=image){
            g.drawImage(image, -x, -y, ANCHORi);
            if (picNames.length>1){
                Font fnt = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
                StringBuffer sb = new StringBuffer(10);
                sb.append(Integer.toString(imgId+1)).append(' ').append(Res.getString(Res.tOF)).append(' ').append(Integer.toString(picNames.length));
                String s = sb.toString();
                int x = W-fnt.stringWidth(s)-10;
                int y = fnt.getHeight()+10;
                g.setFont(fnt);
                g.setColor(0);
                g.drawString(s, x-1, y, ANCHOR);
                g.drawString(s, x+1, y, ANCHOR);
                g.drawString(s, x, y-1, ANCHOR);
                g.drawString(s, x, y+1, ANCHOR);
                g.setColor(0xffffff);
                g.drawString(s, x, y, ANCHOR);
            }
            if (image.getHeight()>H)
                GfxTools.paintVerticalProgress(g, W, H, image.getHeight(), H, y);
            if (image.getWidth()>W)
                GfxTools.paintHorizontalProgress(g, W, H, image.getWidth(), W, x);
        }
    }
//
    public void initImage(){
        FileBrowser fb = FileBrowser.getFileBrowser();
        image = fb.getFB2Image(picNames[imgId], link);
        init(W, H);
    }
    public void commandAction(Command c, Displayable d){
        MyCanvas mc =MyCanvas.getInstance();
        mc.setView(backView);
        mc.setCommandListener(mc);
        /*try {
            Thread.sleep(250);
        }catch (Exception e){}   //*/
    }

}