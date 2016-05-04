/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 29.07.13
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
import javax.microedition.lcdui.*;
import java.util.Vector;

public class MyCanvas extends Canvas implements Runnable, CommandListener{
    public final static int SLEEP = 30;
    public final static int KEYPAUSE = 500;
    public final static int WAIT = 1500;// must be > 500 msc
    public final static int BACK_SIZE = 40; // for BACK touch, on Nokia ASHA 3XX
    public final static int AFTER_TURN_UPDATE_TIME = 700;
    public final static long AUTOPAGE_PAUSE = 500;
    public final static int CLEAR_PAUSE = 60;
    ///* key press
    public final static int KEY_SOFT_LEFT = 10, KEY_SOFT_RIGHT = 20, KEY_CLEAR = 12, KEY_BACK = 11;
    public final static int KEY_UP = 2, KEY_RIGHT = 6, KEY_DOWN = 8, KEY_LEFT = 4, KEY_FIRE = 5, KEY_OFF = -1, NOKEY = 0;

    public static int W, H;
    private static MyCanvas instance;

    public int lastkey = NOKEY;
    ///* touch screen
    int touchmode, touchx, touchy;
    long touchstart;
    //
    private boolean newOrientation = false;
    private int orientationId;

    private boolean running = true, canpaint = false, touchdown = false;
    private Thread thread;
    //
    private long lastturntime = System.currentTimeMillis();

    private boolean landscape = false, landscape_key = false;
    //
    AbstractView av;
//
    private MyCanvas() {
        setFullScreenMode(true);

        landscape = W>H;
        if (hasPointerEvents())
            addCommand(App.back);
        setCommandListener(this);
//        av.init(W, H);
        lastturntime = System.currentTimeMillis();
        needRepaint = true;
        thread = new Thread(this);
        thread.start();
        instance = this;
    }
    public static MyCanvas getInstance(){ return null==instance ? new MyCanvas() : instance;}
    public void killInstance(){
        wakeup();
        running = false;
        av.onExit();
        av = null;
        instance = null;
    }
    public void setView(AbstractView av){
        this.av = av;
        av.init(W, H);
        needRepaint = true;
//        wakeup();
    }
    public void wakeup() {
        lastupdate = System.currentTimeMillis();
        if (null!=thread){
            synchronized (thread) {
                thread.notify();
            }
        }
    }
/// last time user press key, or touch screen
    public long lastupdate = System.currentTimeMillis();
    private long lastkeyupdate  = System.currentTimeMillis();
    private boolean keydown = false;
    public void run() {
        try {
            while (running) {
                update();
                repaint();
                serviceRepaints();

                boolean sleep = (System.currentTimeMillis() - lastupdate < WAIT) | touchdown;
                if (sleep) {
                    Thread.sleep(SLEEP);
                } else {
                    synchronized (thread) {
                        //System.out.println("* before wait");
                        thread.wait(0);
                    }
                }
            }
        } catch (Exception x) { x.printStackTrace(); }
        //System.out.println("mc.run.out");
    }
    private void update() {
        canpaint = false;
        int key = lastkey; /// lastkey can be changed in other thread by user pressing
        long now = System.currentTimeMillis();
//
        if (newOrientation)
            changeDisplayOrientation();
        if (touchmode != c.TOUCH_MODE_NONE) {
            if (c.PH_EMUL==App.platform_id)
                touchy +=56;
            boolean corner300 = App.isAsha3XX()&& touchx>W-BACK_SIZE && touchy>H-BACK_SIZE;
            if (corner300){
                if (touchmode == c.TOUCH_MODE_RELEASED){
                    touchmode = c.TOUCH_MODE_NONE;
                    showAlert();
                    return;
                }
            }else{// todo: opti для всех режимов - много одинакового
                if (c.TOUCH_MODE_PRESSED==touchmode && (!touchdown||(now - touchstart > AUTOPAGE_PAUSE))){
                    System.out.println("*********** TOUCH_MODE_PRESSED");
                    if (false==touchdown)
                        touchstart = now;
                    touchdown = true;
                    if (null!=av)
                        av.updateTouch(touchx, touchy, touchmode);
                    needRepaint = true;
                }else
                if (touchmode == c.TOUCH_MODE_DRAGGED) {
                    System.out.println("TOUCH_MODE_DRAGGED");
                    if (null!=av)
                        av.updateTouch(touchx, touchy, touchmode);
                    needRepaint = true;
                }else
                if (touchmode == c.TOUCH_MODE_RELEASED) {
                    //System.out.println("TOUCH_MODE_REALISED");
                    if (null!=av)
                        av.updateTouch(touchx, touchy, touchmode);
                    needRepaint = true;
                    touchdown = false;
                    touchmode = c.TOUCH_MODE_NONE; // need?
                }
            }
        }
        if (NOKEY != key) {
//            Debug.log("key= "+key); /// todo: opti ifs
            if (KEY_LEFT==key || KEY_RIGHT==key || KEY_DOWN==key || KEY_UP==key || '0'==key || '1'==key) {// второй автоповтор сработает только после KEYPAUSE
                if (landscape_key){
                    if (KEY_DOWN==key)
                        key = KEY_RIGHT;
                    else if (KEY_UP==key)
                        key = KEY_LEFT;
                    else if (KEY_RIGHT==key)
                        key = KEY_UP;
                    else if (KEY_LEFT==key)
                        key = KEY_DOWN;
                }
                if (false == keydown || (now - lastkeyupdate > KEYPAUSE)){
                    if (false == keydown)
                        lastkeyupdate = now;
                    keydown = true;
                    if (null!=av)
                        av.updateKey(key);
                    wakeup();
                    needRepaint = true;
                }
            }
            if (KEY_FIRE==key || KEY_STAR==key){
                if (null!=av)
                    av.updateKey(key);
                lastkey = NOKEY;
                keydown = false;
            }
            if (KEY_OFF == key) {
                if (key == lastkey)
                    lastkey = NOKEY;
                if (null!=av)
                    av.updateKey(key);
                keydown = false;
            }
            if (KEY_SOFT_RIGHT == key || KEY_SOFT_LEFT == key) {
                lastkey = NOKEY;
                showAlert();
            }
            if (Canvas.KEY_POUND==key){
                lastkey = NOKEY;
                landscape_key = !landscape_key;
                orientationId = landscape_key ? com.nokia.mid.ui.orientation.Orientation.ORIENTATION_LANDSCAPE :
                        com.nokia.mid.ui.orientation.Orientation.ORIENTATION_PORTRAIT;
                changeDisplayOrientation();
                wakeup();
                needRepaint = true;
            }
        }
        if (null!=av && av.updateAndRepaint())
            needRepaint = true;
        canpaint = true;
    }
    private Graphics gtemp;
    //private boolean repaintFlag = false;
    public boolean clearGfx = false;
    private boolean needRepaint = false;
    private Graphics gfx_land;
    private Image img_land;
    public void paint(Graphics g) {
        if (clearGfx){
            g.setColor(0xffffff);
            g.fillRect(0, 0, W, H);
            return;
        }
        if (false==canpaint)
            return;
        needRepaint |= !g.equals(gtemp);// bugfix for SonyEricsson
        needRepaint |= (System.currentTimeMillis() - lastturntime) < AFTER_TURN_UPDATE_TIME; // for Asha 5XX turn screen bugfix
        if (false==needRepaint)
            return;
        gtemp = g;
        if (landscape_key){
            if (null==gfx_land){
                img_land = Image.createImage(getHeight(), getWidth());
                gfx_land = img_land.getGraphics();
            }
            g = gfx_land;
        }
/*        if (null!=screen)
            screen.paint(g, repaintFlag);//*/
        g.setColor(App.getColor(c.COLOR_BG));
        g.fillRect(0, 0, W, H);
        if (App.isAsha3XX()){
            g.setColor(App.getColor(c.COLOR_CORNER));
            g.fillRect(W-BACK_SIZE, H-BACK_SIZE, BACK_SIZE, BACK_SIZE);
        }
        if (null!=av)
            av.paint(g);
        if(landscape_key)
            gtemp.drawRegion(img_land, 0, 0, W, H, 5, 0, 0, 20);

        needRepaint = false;
//        Debug.log("cnt: "+cnt++);
    }
    protected void keyPressed(int key) {
        lastkey = commonCode(key);
        /*if (App.DEBUG)
            if (Canvas.KEY_STAR == key) {
                Debug.show(this);
                return;
            }  //*/
        wakeup();
    }
    protected void keyReleased(int key) {
        lastkey = KEY_OFF;
        if (Canvas.KEY_STAR != key)
            wakeup();
    }
    protected void pointerPressed(int x, int y){
        touchmode = c.TOUCH_MODE_PRESSED;
        touchx = x; touchy = y;
        wakeup();
    }
    protected void pointerReleased(int x, int y){
        touchmode = c.TOUCH_MODE_RELEASED;
        touchx = x; touchy = y;
        wakeup();
    }
    protected void pointerDragged(int x, int y){
        touchmode = c.TOUCH_MODE_DRAGGED;
        touchx = x; touchy = y;
        wakeup();
    }
    private int commonCode(int code) {
        try {
            int ga = getGameAction(code);
            if (UP == ga) {
                return KEY_UP;
            } else if (DOWN == ga) {
                return KEY_DOWN;
            } else if (RIGHT == ga) {
                return KEY_RIGHT;
            } else if (LEFT == ga) {
                return KEY_LEFT;
            } else if (FIRE == ga)
                return KEY_FIRE;
        } catch (IllegalArgumentException iae) {}

        if (code == -6 || (!App.DEBUG && '*' == code))
            return KEY_SOFT_LEFT;
        else if (code == -7)
            return KEY_SOFT_RIGHT;
        else if (code == -5 || '5' == code)
            return KEY_FIRE;
        else if ('4' == code)
            return KEY_LEFT;
        else if ('2' == code)
            return KEY_UP;
        else if ('6' == code)
            return KEY_RIGHT;
        else if ('8' == code)
            return KEY_DOWN;
        return code;
    }
    public void showNotify() {
        setFullScreenMode(true);
        lastkey = NOKEY;
        needRepaint = true;
        lastturntime = System.currentTimeMillis();
        wakeup();
    }
    public void changeOrientation(int newDisplayOrientation){
        orientationId = newDisplayOrientation;
        newOrientation = true;
        lastturntime = System.currentTimeMillis();
        wakeup();
    }
    private void changeDisplayOrientation(){
        newOrientation = false;
        if (com.nokia.mid.ui.orientation.Orientation.ORIENTATION_LANDSCAPE==orientationId)
            landscape = true;
        else if (com.nokia.mid.ui.orientation.Orientation.ORIENTATION_PORTRAIT==orientationId)
            landscape = false;
        if ((landscape&&W<H)||(false==landscape&&H<W)){
            int temp = H; H = W; W = temp;
        }
        if (null!=av)
            av.init(W, H);
    }
    private void showAlert(){
        Alert alert = new Alert(Res.getString(Res.tINFORMATION));
        alert.setString(Res.getString(Res.tASK_QUIT));
        alert.setCommandListener(this);
        alert.addCommand(App.ok);
        alert.addCommand(App.cancel);
        if (501==App.platform_id)
            alert.addCommand(App.back);
        App.show(alert);
    }
    public void commandAction(Command c, Displayable d){
        if (this == d){
            showAlert();
        }else {
            if (App.cancel==c || App.back==c){
                App.show(this);
            }else if (App.ok==c){
                killInstance();
                App.show(MainMenu.getInstance());
            }
        }
    }
}