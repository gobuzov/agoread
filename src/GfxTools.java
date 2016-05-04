import javax.microedition.lcdui.*;
/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 27.09.14
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public class GfxTools {
    public static Image knob_up, knob_vcenter, knob_down, knob_left, knob_hcenter, knob_right;
    public static Image shadowBlue, shadowRed, shadowGreen, shadowOrange;
    //
    static {
        initKnob();
    }
    public static void paintVerticalProgress(Graphics g, int W, int H, int total, int volume, int ofset){
        initKnob();
        int x = W - knob_vcenter.getWidth();
        int knob = (H * volume) / total;
        if (knob < 8)
            knob = 8;
        int yk = ((H-knob) * ofset) / (total-volume); // где рисуем кноб
        g.drawImage(knob_up, x, yk, 20);
        fillImage(g, knob_vcenter, x, yk+2, knob_vcenter.getWidth(), knob - 4);
        g.drawImage(knob_down, x, yk+knob-2, 20);
    }
    public static void paintHorizontalProgress(Graphics g, int W, int H, int total, int volume, int ofset){
        initKnob();
        int y = H - knob_hcenter.getHeight();
        int knob = (W * volume) / total;

        if (knob < 8)
            knob = 8;
        int xk = ((W-knob) * ofset) / (total-volume); // где рисуем кноб
        g.drawImage(knob_left, xk, y, 20);
        fillImage(g, knob_hcenter, xk+2, y, knob - 4, knob_vcenter.getWidth());
        g.drawImage(knob_right, xk+knob-2, y, 20);
    }
    private static void initKnob(){
        int mem[] = new int[256];
        int i = 0;
        boolean alfa = Display.getDisplay(App.instance).numAlphaLevels()>2;
        while (i < 256)
            mem[i++] = 0x40000080;
        shadowBlue = Image.createRGBImage(mem, 16, 16, true);
        while (i!=0)
            mem[--i] = 0x40800000;
        shadowRed = Image.createRGBImage(mem, 16, 16, true);
        while (i < 256)
            mem[i++] = 0x40008000;
        shadowGreen = Image.createRGBImage(mem, 16, 16, true);
        while (i!=0)
            mem[--i] = 0x40c06000;
        shadowOrange = Image.createRGBImage(mem, 16, 16, true);

        int a = (alfa) ? 0:0xffffffff;
        int b = (alfa) ? 0xa0dddddd:0xffdddddd;
        int c = (alfa) ? 0xa0444444:0xff444444;
        knob_up = Image.createRGBImage(new int[] {a, b, b, a, b, c, c, b}, 4, 2, alfa);
        int[]arr = new int[]{b, c, c, b};
        for (; i < 64; i++)
            mem[i] = arr[i & 3];
        knob_vcenter = Image.createRGBImage(mem, 4, 16, alfa);
        knob_down  = Image.createRGBImage(new int[] {b, c, c, b, a, b, b, a}, 4, 2, alfa);
//
        knob_left = Image.createRGBImage(new int[] {a, b, b, c, b, c, a, b}, 2, 4, alfa);
        for (i = 0; i < 64; i++)
            mem[i] = i<16 || i>=48 ? b : c;
        knob_hcenter = Image.createRGBImage(mem, 16, 4, alfa);
        knob_right  = Image.createRGBImage(new int[] {b, a, c, b, c, b, b, a}, 2, 4, alfa);
    }
    public static void fillImage (Graphics g, Image img, int x, int y, int w, int h){
        if (null!=img){
            int clipx = g.getClipX();
            int clipy = g.getClipY();
            int clipw = g.getClipWidth();
            int cliph = g.getClipHeight();
            g.setClip(x, y, w, h);
            for (int yy = y; yy < y + h; yy += img.getHeight()) {
                for (int xx = x; xx < x + w; xx += img.getWidth())
                    g.drawImage(img, xx, yy, Graphics.TOP | Graphics.LEFT);
            }
            g.setClip(clipx, clipy, clipw, cliph);
        }
    }
    public static void drawPolyLine (Graphics g, int[]pnts){
        for (int i=0; i<pnts.length-2; i+=2)
            g.drawLine(pnts[i], pnts[i+1], pnts[i+2], pnts[i+3]);
        g.drawLine(pnts[0], pnts[1], pnts[pnts.length-2], pnts[pnts.length-1]);
    }

//-------------------------------- Gradient Tools
    public final static int COLOR_3 = 0xFF00;
    public final static int COLOR_2 = 0xFFFF00;
    public final static int COLOR_1 = 0xFF0000;
    private static int[] gradient;
    public static int getGradient(int id){
        if (null==gradient)
            gradient = uniteArrays(doGradient(COLOR_1, COLOR_2, 30), doGradient(COLOR_2, COLOR_3, 30));
        if (id>=gradient.length)
            id = gradient.length-1;
        if (id<0)
            id = 0;
        return gradient[id];
    }
    public static Image doRectangle(int c1, int c2, int w, int h){
        int sz = h>>1;
        int[] gradient1 = doGradient(c1, c2, sz);
        if ((h&1)!=0)
            ++sz;
        int[] gradient2 = doGradient(c2, c1, sz);
        int[] gradient = uniteArrays(gradient1, gradient2);
        Image img = Image.createImage(w, h);
        Graphics gfx = img.getGraphics();
        for (int i=0; i<h; i++){
            gfx.setColor(gradient[i]);
            gfx.drawLine(0, i, w-1, i);
        }
        return img;
    }
    public static int[] doGradient(int c1, int c2, int len){
        int[] colors = new int[len];
        int r = c1 >> 8 & 0xff00;
        int g = c1 & 0xff00;
        int b = c1 << 8 & 0xff00;
        int dr = (r - (c2 >> 8 & 0xff00)) / len;
        int dg = (g - (c2 & 0xff00)) / len;
        int db = (b - (c2 << 8 & 0xff00)) / len;
        for(int i = 0; i < len; i++){
            colors[i] = (r & 0xff00) << 8 | g & 0xff00 | b >> 8;
            r -= dr;
            g -= dg;
            b -= db;
        }
        return  colors;
    }
    /*private void gradient(Graphics gfx, boolean horizontal, int c1, int c2, int x, int y, int w, int h){
        int r = c1 >> 8 & 0xff00;
        int g = c1 & 0xff00;
        int b = c1 << 8 & 0xff00;
        int blocklen = w;
        --h;
        if(horizontal){
            blocklen = h;
            --w;
        }
        int dr = (r - (c2 >> 8 & 0xff00)) / blocklen;
        int dg = (g - (c2 & 0xff00)) / blocklen;
        int db = (b - (c2 << 8 & 0xff00)) / blocklen;
        for(int k3 = 0; k3 < blocklen; k3++){
            gfx.setColor((r & 0xff00) << 8 | g & 0xff00 | b >> 8);
            if(horizontal)
                gfx.drawLine(x, y + k3, x + w, y + k3);
            else
                gfx.drawLine(x + k3, y, x + k3, y + h);
            r -= dr;
            g -= dg;
            b -= db;
        }
    }*/
    public static int[] uniteArrays(int[] arr1, int[] arr2){
        int len1 = arr1.length;
        int len2 = arr2.length;
        int[] newarr = new int[len1 + len2];
        System.arraycopy(arr1, 0, newarr, 0, len1);
        System.arraycopy(arr2, 0, newarr, len1, len2);
        return newarr;
    }
//\-------------------------------- Gradient Tools

}
