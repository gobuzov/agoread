import javax.microedition.lcdui.*;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 27.09.14
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
public class TextView extends AbstractView implements CommandListener{
    private int W, H;
    private TextPart txp;
    private FileLink link;
    private static boolean paintHint = true;
    Page page;

    public TextView(TextPart txp, FileLink link){
        this.txp = txp;
        this.link = link;
    }
    public void init(int w, int h){
        W = w; H = h;
        initTextParams();
    }
    public FileLink getLink(){return link;}

    public void initTextParams(){
        page = new Page(link, txp);
        page.render(W, H);
    }
    public boolean updateAndRepaint(){
        return false;
    }
    public void updateKey(int key){
        if (paintHint){
            paintHint = false;
            return;
        }
        if ('0'==key)
            initSettingsPanel();
        else if ('1'==key)
            initBookmarksPanel();
        else if (MyCanvas.KEY_FIRE==key || MyCanvas.KEY_STAR==key){
            boolean pic = null!=page.getPicNames();
            boolean note = null!= page.getRemarks();
            if (MyCanvas.KEY_FIRE==key && pic){
                goPictureViewer();
            }else if (note &&((MyCanvas.KEY_FIRE==key && !pic)||MyCanvas.KEY_STAR==key))
                goNotesViewer();
        }else{
            if (MyCanvas.KEY_DOWN==key){
                goNextPart();
            }else if (MyCanvas.KEY_UP==key){
                goPrevPart();
            }else if (MyCanvas.KEY_LEFT==key){
                goPrevPage();
            }else if (MyCanvas.KEY_RIGHT==key){
                goNextPage();
            }
        }
    }
    private void goPictureViewer(){
        String[] picNames = page.getPicNames();
        if (picNames.length>1){
            PictureViewer pv = new PictureViewer(picNames, link, this);
            MyCanvas.getInstance().setView(pv);
            return;
        }
        Form form = new Form(Res.getStringLower(Res.tCOVER));
        FileBrowser fb = FileBrowser.getFileBrowser();
        Image image = fb.getFB2Image(picNames[0], link);
        ImageItem imageItem = new ImageItem( null, image, Item.LAYOUT_DEFAULT,  null);

        imageItem.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_CENTER);
        form.append(imageItem);
        form.addCommand(App.back);
        form.setCommandListener(this);
        App.show(form);
    }
    private void goNotesViewer(){
        Form form = new Form(Res.getStringLower(Res.tNOTES));
        String[] remarks = page.getRemarks();
        for (int i=0; i< remarks.length; i++)
            form.append(link.getNote(remarks[i]));
        form.addCommand(App.back);
        form.setCommandListener(this);
        App.show(form);
    }
    private void initSettingsPanel(){
        SettingsPanel sp = new SettingsPanel(this);
        MyCanvas.getInstance().setView(sp);
    }
    private void initBookmarksPanel(){
        BookmarksPanel bp = new BookmarksPanel(this);
        MyCanvas.getInstance().setView(bp);
    }
    public void viewNewOffset(int newOffset){
        link.setOffset(newOffset);
        page.render(W, H);
    }
    public void commandAction(Command c, Displayable d){
        App.show(MyCanvas.getInstance());
    }
    private int startx;
    /*private long startmove;
    private void moveOnText1(int touchx, int touchmode){
        Debug.log("x= "+touchx);
        if (1==1)
            return;
        if (touchmode==c.TOUCH_MODE_PRESSED){
            startx = touchx;
            startmove = System.currentTimeMillis();
        }
        if (touchmode==c.TOUCH_MODE_DRAGGED){

        }
        //z
        int partsize = txp.getLen()>>4;
        int currpart = link.getOffset()/partsize;
        int new_ofset = 0;
        if (0!=currpart){
            new_ofset = --currpart*partsize;
        }
        link.setOffset(new_ofset);

    }  */
    public void updateTouch(int tx, int ty, int mode){
        if (paintHint==false){
            if (page.isInNoteZone(tx, ty)){
                if (mode==c.TOUCH_MODE_RELEASED)
                    goNotesViewer();
                return;
            }
            if (page.isInPicZone(tx, ty)){
                if (mode==c.TOUCH_MODE_RELEASED)
                    goPictureViewer();
                return;
            }
            if (ty<H>>2 && tx<W>>1){
                if (mode==c.TOUCH_MODE_RELEASED)
                    initSettingsPanel();
                return;
            }
            if (ty<H>>2 && tx>W>>1){
                if (mode==c.TOUCH_MODE_RELEASED)
                    initBookmarksPanel();
                return;
            }
            /*if (ty>H-20){
                moveOnText(tx, mode);
            }else*/
            if (mode==c.TOUCH_MODE_PRESSED){
                startx = tx;
                if (ty<(H>>2)){

                }else
                if (tx > (W>>1))
                    goNextPage();
                else
                    goPrevPage();
            }
        }else {
                paintHint = false;
        }
    }
    public void paint(Graphics g){
        page.paint(g, paintHint);

        if (link.getPageSize()<txp.getLen())
            GfxTools.paintVerticalProgress(g, W, H, txp.getLen(), link.getPageSize(), link.getOffset());
        if (paintHint)
            paintHint(g);
    }
    public void onExit(){
        link.saveToRms();
    }
    public void kill(){
        txp = null;
    }
    void goPrevPart(){
        int partsize = txp.getLen()>>4;
        int currpart = link.getOffset()/partsize;
        int new_ofset = 0;
        if (0!=currpart){
            new_ofset = --currpart*partsize;
        }
        viewNewOffset(new_ofset);
    }//*/
    void goNextPart(){
        if (link.getNextOffset() < txp.getLen()){
            int partsize = txp.getLen()>>4;
            int currpart = link.getOffset()/partsize;
            if (currpart<16){
                viewNewOffset(16==++currpart? txp.getLen()-(link.getNextOffset()- link.getOffset())/2:currpart*partsize);
            }
        }
    }
    void goNextPage(){
        if (link.getNextOffset() < txp.getLen()){
            boolean anim = App.getBool(Res.tPAGE_ANIMATION);
            anim = false;
            if (anim){
                PageSwitcher ps = new PageSwitcher(this, true, link.getOffset(), link.getNextOffset(), startx);
                MyCanvas.getInstance().setView(ps);
            }else {
                viewNewOffset(link.getNextOffset());
            }
        } else {
            if (App.LITE){
                kill();
                MainMenu.getInstance().initAbout();
            }else if (link.nextPortion()){
                kill();
                FileBrowser.getFileBrowser().showFile(link, 0);
            }
        }
    }
    void goPrevPage(){
        int ofset = link.getOffset();
        if (0==ofset){
            int pagesize = link.getNextOffset();
            if (App.LITE){
                kill();
                MainMenu.getInstance().initAbout();
            }else if (link.prevPortion()){
                kill();
                FileBrowser.getFileBrowser().showFile(link, pagesize);
            }
        }else
            page.renderBack(W, H);
    }
    public void paintHint(Graphics g){// todo: перенести в Hinter ???
        /*if (true)
            return;//*/
        Font fnt = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        g.setFont(fnt);
        int fh = fnt.getHeight();
        if (App.istouch){
            GfxTools.fillImage(g, GfxTools.shadowGreen, 0, 0, W>>1, H>>2);
            String str = Res.getString(Res.tSETTINGS);
            int w = fnt.stringWidth(str) + 10;
            int h = fh + 10;
            int xx = (((W>>1) - w)>>1);
            int yy = ((H>>2)-h)>>1;
            g.setColor(0x80cc80);
            g.fillRect(xx, yy, w, h);
            g.setColor(0xffffff);
            g.drawString(str, xx+5, yy+5+fh, ANCHOR);
//
            GfxTools.fillImage(g, GfxTools.shadowOrange, W>>1, 0, W>>1, H>>2);
            str = Res.getString(Res.tBOOKMARKZ);
            w = fnt.stringWidth(str) + 10;
            xx = (W>>1)+(((W>>1) - w)>>1);
            g.setColor(0xffc080);
            g.fillRect(xx, yy, w, h);
            g.setColor(0xffffff);
            g.drawString(str, xx+5, yy+5+fh, ANCHOR);
//
            GfxTools.fillImage(g, GfxTools.shadowBlue, 0, H>>2, W>>1, H-(H>>2));
            str = Res.getString(Res.tPREVPAGE);
            w = fnt.stringWidth(str) + 10;
            xx = (((W>>1) - w)>>1);
            yy = (H+(H>>2)-h)>>1;
            g.setColor(0x8080ff);
            g.fillRect(xx, yy, w, h);
            g.setColor(0xffffff);
            g.drawString(str, xx+5, yy+5+fh, ANCHOR);
//
            GfxTools.fillImage(g, GfxTools.shadowRed, W>>1, H>>2, W>>1, H-(H>>2));
            str = Res.getString(Res.tNEXTPAGE);
            w = fnt.stringWidth(str) + 10;
            xx = (W>>1)+(((W>>1) - w)>>1);
            g.setColor(0xff8080);
            g.fillRect(xx, yy, w, h);
            g.setColor(0xffffff);
            g.drawString(str, xx+5, yy+5+fh, ANCHOR);
        }else{// Hint for keys
            int border = 10;
            int[] ids = {Res.tSETTING_PANEL, Res.tBOOKMARKS, Res.tFAST_PAGING,Res.tPREVIOUS_PAGE,Res.tSEE_PICTURES,
                    Res.tNEXT_PAGE,Res.tSEE_NOTES, Res.tTURN_SCREEN };
            int wmax = 0;
            for (int i=ids.length-1; i>=0; i--){
                int w = fnt.stringWidth(Res.getString(ids[i]));
                if (w>wmax)
                    wmax = w;
            }
            int w = wmax+border*2;
            int h = ids.length*fh+border*2;
            int x = (W - w)/2;
            int y = (H - h)/2;
            g.setColor(0x80cc80);
            g.fillRect(x, y, w, h);
            x+=border;
            y+=fh+border;
            g.setColor(0xffffff);
            for (int i=0; i< ids.length; i++, y+=fh){
                g.drawString(Res.getString(ids[i]), x, y, ANCHOR);
            }
        }
    }
}
