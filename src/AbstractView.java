import javax.microedition.lcdui.Graphics;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 27.09.14
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractView {
    public final static int ANCHORi = Graphics.TOP | Graphics.LEFT;
    public final static int ANCHOR = Graphics.BOTTOM | Graphics.LEFT;
/// call: 1.on start,  2.changing screen's orientation
    public abstract void init(int w, int h);
    public abstract boolean updateAndRepaint();
    public abstract void updateKey(int key);
    public abstract void updateTouch(int touchx, int touchy, int touchmode);
    public abstract void paint(Graphics g);
    public abstract void onExit();
}