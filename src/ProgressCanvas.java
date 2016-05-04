/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 24.03.14
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
import javax.microedition.lcdui.*;

public class ProgressCanvas extends Canvas{
    boolean fullscreen;
    protected int progress = 0; // 0..100 %
    String text;

    public ProgressCanvas(String s, boolean fullscreen){
        text = s;
        this.fullscreen = fullscreen;
        this.setFullScreenMode(true);
    }
    public void setText(String str){this.text = str; progress = 0;}
    public void setProgress(int i){
        if (i!=progress){
            progress = i;
            repaint();
            serviceRepaints();
        }
    }
    public void showNotify() {
        setFullScreenMode(fullscreen);
    }
    public void paint(Graphics g) {
        int W = getWidth();
        int H = getHeight();
        g.setColor(App.getColor(c.COLOR_BG));
        g.fillRect(0, 0, W, H);
//
        int w = W *7/8;
        int h = H /8;
        int x = (W-w)/2;
        int y = (H-h)/2;

        if (progress <= 100 && progress > 0){
            int ww = progress * w / 100;
            g.setColor(0x88ff88);
            g.fillRect(x, y, ww, h);
            g.setColor(App.getColor(c.COLOR_FG));
            g.drawRect(x, y, w, h);
        }
        g.setColor(App.getColor(c.COLOR_FG));
        Font fnt = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        g.setFont(fnt);
        g.drawString(text, (W - fnt.stringWidth(text))/2, (H/2 + fnt.getHeight())/2, Graphics.BOTTOM | Graphics.LEFT);

    }
}
