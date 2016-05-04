import javax.microedition.lcdui.*;
/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 18.11.14
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class BookmarksPanel extends SettingsPanel{
    public final static int MAX_BOOKMARKS = 6;
    int[] bookmarks;
    int newbookmark = -1;
    long flashtime;

    public BookmarksPanel(TextView tv){ super(tv);
        BG = 0xddaa66; FG = 0xffffff; FOCUS = 0; FRAME = 0xbb8844; CURSOR = 0xffcc88; EXIT_KEY ='1';
        bookmarks = textview.getLink().getBookmarks();
        if (null==bookmarks){
            bookmarks = new int[MAX_BOOKMARKS];
            for (int i = 0; i<MAX_BOOKMARKS; i++)
                bookmarks[i] = -1;
        }
    }
    public boolean updateAndRepaint(){
        boolean res = super.updateAndRepaint();
        if (-1!=newbookmark && System.currentTimeMillis()-flashtime>100){
            newbookmark = -1;
            return true;
        }
        return res || -1!=newbookmark;
    }
    protected void updateValues(int x, int y){
        int value = landscape ? x : y + (x>1 ? 3 : 0);
        int bookmark = bookmarks[value];
        if ((landscape && 0==y) || (!landscape && (x==0 || x==2))){ // + + +
            if (-1!=bookmark){
                flashtime = System.currentTimeMillis();
                newbookmark = value;
            }
            FileLink link = textview.getLink();
            bookmarks[value] = link.getOffset();
            link.setBookmarks(bookmarks);
        }else
        if (((landscape && 1==y) || (!landscape && (1==x || 3==x))) && -1!=bookmark){ // 1 2 3 4 5 6
            textview.viewNewOffset(bookmark);
        }
        textview.initTextParams();
        needRepaint = true;
    }
    protected void paintContent(Graphics g, int Y){
        drawBookmarks (g, 0, Y, true);
        drawBookmarks (g, landscape ? 2:1, Y, false);
        drawBookmarks (g, landscape ? 1:2, Y, true);
        drawBookmarks (g, 3, Y, false);
        //drawPages     (g, landscape? 1:2, Y);
        //drawLetters(g, 3, Y);/// other services
    }
    /* Portrait: 0369     Landscape: 012345
                 147A                6789AB
                 258B  */
    protected void drawBookmarks(Graphics g, int id, int Y, boolean plus){
        int x = !landscape ? id * wc : 0==(id&1) ? 0 : w/2;
        int y =  landscape&(id>1) ? Y+hc : Y;

        int wb = wc * 2 / 3;
        int hb = hc * 2 / 3;//medium.getHeight()&0xfffe;

        x += (wc-wb)/2;
        y += (hc-hb)/2;
        g.setFont(medium);
        int bg = plus? 0x00cc00 : 0xffffff;
        int fg = plus? 0xffffff : 0;
        for(int i=0; i<3; i++, y+=dy, x+=dx){
            int bookmarkId = i+(id==3? id:0);
            boolean b = plus | bookmarks[bookmarkId]==-1 | bookmarkId==newbookmark; // is setted bookmarks
            g.setColor(b ? bg : fg);
            int tx = x + wb - hb/2;
            g.fillRect(x, y, tx-x, hb);
            g.fillTriangle(x+wb, y, tx, y, tx, y+hb);
            g.fillTriangle(x+wb, y+hb, tx, y, tx, y+hb);

            g.setColor(b ? fg : bg);
            GfxTools.drawPolyLine(g, new int[]{x, y, x+wb, y, tx+hb/4, y+hb/2, x+wb, y+hb, x, y+hb});
            char ch = plus ? '+' : (char)(bookmarkId+'1');
            g.drawChar(ch, x+1+(wb-medium.charWidth(ch))/2, y+hb/2+medium.getHeight()/2+1, ANCHOR);
        }
    }
    /*protected void drawPages(Graphics g, int id, int Y){
        int x = !landscape ? id * wc : 0==(id&1) ? 0 : w/2;
        int y =  landscape&(id>1) ? Y+hc : Y;
        int wb = wc * 2 / 3;
        int hb = hc * 2 / 3;

        x += (wc-wb)/2;
        y += (hc-hb)/2;
        int paging = App.getInt(Res.tFAST_PAGING);

        for(int i=0; i<3; i++, y+=dy, x+=dx){
            g.setColor(i==paging ? FOCUS : FG);
            g.drawRect(x, y, wb, hb);
            if (0==i){
                g.drawLine(x+wb/2, y, x+wb/2, y+hb);
            }else if (1==i){
                for (int j=x+wb-3; j>x+wb/2; j-=3)
                    g.drawLine(j, y, j, y+hb);
            }else{
                for (int j=y+hb-3; j>y+hb/2; j-=3)
                    g.drawLine(x, j, x+wb, j);
            }
        }
    }*/
}