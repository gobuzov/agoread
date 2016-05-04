import javax.microedition.lcdui.*;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 29.10.14
 * Time: 12:13
 * To change this template use File | Settings | File Templates.
 */
public class Page {
    public final static int ANCHOR = Graphics.BOTTOM | Graphics.LEFT;
    public final static int TYPE_SPACE = 0, TYPE_WORD = 1;
    private int[] offsets, lens, x, y, w, h, types;
    private FileLink link;
    private TextPart txp;
    private String[] picNames, remarks;
    private int[] picZone;

    public Page(FileLink link, TextPart txp){
        this.link = link;
        this.txp = txp;
    }
    public void paint(Graphics g, boolean paintHint){
        if (false==paintHint)
            paintPicSign(g);
        int offset = link.offset;

        Flyweight qf = txp.getQueFonts();
        Flyweight qc = txp.getQueColors();
        CharVector cv = txp.getChars();

        qf.setStart(offset); g.setFont(qf.getFont()); qf.nextPosition();
        int nextFntOffset = qf.getOffset();
        //
        qc.setStart(offset); g.setColor(qc.getColor()); qc.nextPosition();
        int nextColorOffset = qc.getOffset();
        //
        int sz = offsets.length;
        for (int i=0; i<sz; i++){
            int type  = types[i];
            if (TYPE_SPACE!=type){
                offset = offsets[i];
                if (nextFntOffset<=offset){
                    g.setFont(qf.getFont()); qf.nextPosition(); nextFntOffset = qf.getOffset();
                }
                if (nextColorOffset<=offset){
                    g.setColor(qc.getColor()); qc.nextPosition(); nextColorOffset = qc.getOffset();
                }
                cv.drawChars(g, offset, lens[i], x[i], y[i]);
            }
        }
    }
    public void paintPicSign(Graphics g){
        if (null!=picZone){
            int x = picZone[0];
            int y = picZone[1];
            int w = picZone[2]/2;
            g.setColor(App.getColor(c.COLOR_PIC1));
            g.fillRect(x, y, w, w);
            g.setColor(App.getColor(c.COLOR_PIC2));
            g.fillRect(x+w, y, w, w);
            g.setColor(App.getColor(c.COLOR_PIC3));
            g.fillRect(x, y+w, w, w);
            g.setColor(App.getColor(c.COLOR_PIC4));
            g.fillRect(x+w, y+w, w, w);
        }
    }
    public boolean isInNoteZone(int px, int py){
        for (int i=offsets.length-1; i>=0; i--){
            if (px>x[i] && px<x[i]+w[i] && py<y[i] && py>y[i]-h[i]){
                CharVector cv = txp.getChars();
                System.out.println(cv.getString(offsets[i], lens[i]));//*/
                return txp.findRemark(offsets[i]);
            }
        }
        return false;
    }
    public boolean isInPicZone(int x, int y){
        return null!=picZone && x>picZone[0] && x<picZone[0]+picZone[2] && y>picZone[1] && y<picZone[1]+picZone[2];
    }
    public String[] getPicNames(){return picNames;}
    public String[] getRemarks(){return remarks;}

    class StrElem{
        int x, y, w, h, offset, len, type;
        public StrElem(int offset, int len, int w, int type){
            this.offset = offset;
            this.len = len;
            this.w = w;
            this.type = type;
        }
    }
    public void renderBack(int W, int H){
        int offset = link.getOffset();
        int newoffset = offset - (link.getNextOffset()  - offset);
        link.setOffset(newoffset < 0 ? 0 : newoffset);
        render(W, H);
        newoffset-=(link.getNextOffset()-offset);
        link.setOffset(newoffset < 0 ? 0 : newoffset);
        render(W, H);
    }
    public void render(int W, int H){
        Flyweight qf = txp.getQueFonts();
        CharVector cv = txp.getChars();

        int offset = link.getOffset();
        qf.setStart(offset);
        Font fnt = qf.getFont(); qf.nextPosition();
        int nextFntOffset = qf.getOffset();

        int len = cv.getLen();
        int border = App.getBorder();
        int between = App.getBetween();
        int hPage = 0;
        Vector page = new Vector(), line = new Vector();
        int wSpace = fnt.charWidth(' '), hLine;
        int wstr = 0, wmax = W - (border<<1);
        while (true){// loop for all lines
            int begin = offset, wword = 0;
            hLine = fnt.getHeight();
            char ch = 0;
            while (true){// loop for all words inside one line
                ch = cv.getChar(offset++);
                if ('\r'==ch){
                    if (0!=offset-begin-1)
                        line.addElement(new StrElem(begin, offset-begin-1, wword, TYPE_WORD));
                    wstr+=wword;
                    break;
                }
                if (' '==ch){
                   if (0!=wword){
                       line.addElement(new StrElem(begin, offset-begin-1, wword, TYPE_WORD));
                       wstr+=wword;
                   }
                   if (wstr>wmax)
                       break;
                   line.addElement(new StrElem(0, 0, wSpace, TYPE_SPACE));
                   while (offset<len && ' '==cv.getChar(offset))
                       ++offset;
                   wstr+=wSpace;
                   wword = 0; begin = offset;
                }else/// not space
                    wword+=fnt.charWidth(ch);
                if (nextFntOffset<=offset){  // todo: сейчас не обрабатывается смена шрифта в 1 слове.
                    fnt = qf.getFont(); qf.nextPosition(); nextFntOffset = qf.getOffset();
                    wSpace = fnt.charWidth(' ');
                    if (hLine < fnt.getHeight())
                        hLine = fnt.getHeight();
                }
                if (offset>=len){
                    line.addElement(new StrElem(begin, offset-begin, wword, TYPE_WORD));
                    break;
                }
            }
            Vector newline = new Vector(line.size());
            hPage+=(hLine-between);
            boolean breakLine = wstr>wmax;
            if (breakLine){
                if (1==line.size()){// todo: особый случай - длинное слово. надо его разрезать. надо понять, с какой стороны лучше
                    oneLongWord(line);
                }else{
                    StrElem se = (StrElem)line.lastElement();
                    newline.addElement(se);
                    offset = se.offset + se.len;
                    line.removeElementAt(line.size()-1);
                    line.removeElementAt(line.size()-1);
                }
            }
/// justify
            if (false==line.isEmpty() && ((StrElem)line.lastElement()).type==TYPE_SPACE)// if last <SPACE> then kill it
                line.removeElementAt(line.size()-1);
            int sz = line.size();
            StrElem[] elems = new StrElem[sz];
            line.copyInto(elems);

            if (breakLine && sz>4){
                int start = 0;
                int w = wmax;
                if (elems[0].type==TYPE_SPACE){
                    start = 1; w-=elems[0].w;
                }
                if ('-'==cv.getChar(elems[start].offset)){
                    w-=elems[start++].w;
                    w-=elems[start++].w;
                }
                int words = 0;
                wword = 0;
                for (int i=start; i<sz; i++){
                    if (elems[i].type!=TYPE_SPACE){
                        words++;
                        wword+=elems[i].w;
                    }
                }
                --words;
                int newWspace = (w - wword)/words;
                int wAddon = w - wword - newWspace*words;
                for (int i=start; i<sz; i++)
                    if (elems[i].type==TYPE_SPACE)
                        elems[i].w = wAddon--> 0 ? newWspace+1 : newWspace;
            }
            for (int i=0, x = border; i<sz; i++){
                StrElem se = elems[i];
                se.x = x; se.y = hPage; se.h = hLine;
                x+=se.w;
            }
//\
            page.addElement(elems);
            line = newline;
            if (hPage+hLine>H || offset>=len){
                if (false==line.isEmpty())
                    offset = ((StrElem)line.lastElement()).offset;
                break;
            }
            wstr = line.isEmpty() ? 0 :((StrElem)line.lastElement()).w;
        }
/// Move Vector page to Arrays
        movePageToArrays(page);
/// correct Y
        int dy = border + (H%(hLine-between))/2;
        for (int i=y.length-1; i>=0; i--)
            y[i]+=dy;
//\
        link.setNextOffset(offset); ///
        picNames = txp.getPicturesOnPage(link);
        if (null!=picNames){
            int w = (W>H ? H : W)/3;
            picZone = new int[]{(W - w)>>1, (H - w)>>1, w};
        }else
            picZone = null;
        remarks = txp.getRemarksOnPage(link);
    }
    void oneLongWord(Vector line){
        System.out.println("oneLongWord");
    }
    void movePageToArrays(Vector page){
        int sz = 0;
        for (int i=0; i<page.size(); i++)
            sz+=((StrElem[])page.elementAt(i)).length;

        offsets = new int[sz]; lens = new int[sz]; x = new int[sz]; y = new int[sz];
        w = new int[sz]; h = new int[sz]; types = new int[sz];
        int k = 0;
        for (int i=0; i<page.size(); i++){
            StrElem[] line = (StrElem[])page.elementAt(i);
            for (int j=0; j<line.length; j++){
                StrElem se = line[j];
                types[k] = se.type;
                x[k] = se.x; y[k] = se.y; w[k] = se.w;
                if (se.type!=TYPE_SPACE){
                    offsets[k] = se.offset;
                    lens[k] = se.len;
                }
                h[k++] = se.h;
            }
        }
    }
}
