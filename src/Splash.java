/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 29.07.13
 * Time: 8:00
 * To change this template use File | Settings | File Templates.
 */
import javax.microedition.lcdui.*;

public class Splash extends Canvas {
    public final static int ANCHORi = Graphics.TOP | Graphics.LEFT;
    public final static int ANCHOR = Graphics.BOTTOM | Graphics.LEFT;
    public boolean started = false;
    public boolean ended = false;
    String note = "Any Book Any Time";
    private Image books;

    public Splash() {
        App.istouch = hasPointerEvents();
    }
    public Image makeImage(){
        String[] books = {"F.Dostoyevsky", "Leo Tolstoy", "R.Sheckley", "A.Conan Doyle", "Bruce Eckel", "R.Tagore", "Mark Twain"};
        Tools.shuffle(books);
        int k = 8;
        int[] addheights =  {k, k, 2*k, 2*k, 2*k, 3*k, 3*k};
        int WMAX = 3*k;
        //Tools.shuffle(addheights);
        int[] colors = {0x800000, 0xff0000, 0x80, 0xff, 0x8000, 0xff00, 0x806030, 0xffcc66};
        int style = App.isnokia ? Font.STYLE_ITALIC : Font.STYLE_BOLD;
        Font fnt = Font.getFont(Font.FACE_PROPORTIONAL, style, Font.SIZE_SMALL);

        int fh = fnt.getHeight();
        int wmax = 0;
        for (int i=0; i<books.length; i++){
            int wstr = fnt.stringWidth(books[i]);
            if (wstr>wmax)
                wmax = wstr;
        }
        wmax += 6;
        int cid = 0;
        int w = wmax + WMAX;
        int h = fh*books.length;

        Image img = Image.createImage(w, h);
        Graphics gfx = img.getGraphics();
        gfx.setColor(App.getColor(c.COLOR_BG));
        gfx.fillRect(0,0,w,h);
        gfx.setFont(fnt);

        for (int i=0, y=0; i<books.length; i++){
            int wb = wmax+addheights[i];
            int xb = wmax + WMAX - wb;
            Image bg_img = GfxTools.doRectangle(colors[cid++], colors[cid++], wb, fh);
            gfx.drawImage(bg_img, xb, y, ANCHORi);
            int wstr = fnt.stringWidth(books[i]);
            gfx.setColor(0xffffff);
            gfx.drawString(books[i], xb + (wb-wstr)/2, y + fh, ANCHOR);

            gfx.setColor(0xcccccc);
            gfx.drawLine(xb, y+2, xb, y+ fh-3);
            if (cid>=colors.length)
                cid = 0;
            y+=fh;
        }
        Image img2 = Image.createImage(h, w);
        img2.getGraphics().drawRegion(img, 0, 0, w, h, 5, 0, 0, 20);
        return img2;
    }
    public void paint(Graphics g) {
        setFullScreenMode(true);
        int w = getWidth();
        int h = getHeight();
        MyCanvas.W = w;
        MyCanvas.H = h;
        g.setColor(App.getColor(c.COLOR_BG));
        g.fillRect(0, 0, w, h);
        if (ended){
            return;
        }
        if (null==books)
            books = makeImage();
        int x = (w-books.getWidth())>>1;
        int y = (h+books.getHeight())>>1;
        g.drawImage(books, x, y-books.getHeight(), 20);
        g.setColor(0x666666);
        g.drawLine(x-5, y, w-x+5, y);

        g.setColor(App.getColor(c.COLOR_FG));
        Font f = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        g.setFont(f);

        y+=f.getHeight()*3/2;
        g.drawString(note, (w - f.stringWidth(note))/2, y, ANCHOR);
        if (!started) {
            started = true;
            (new Thread(App.instance)).start();
        }
    }
}