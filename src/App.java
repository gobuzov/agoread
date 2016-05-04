/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 29.07.13
 * Time: 19:25
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
// todo: open libs api
// https://openlibrary.org/dev/docs/restful_api
// https://developers.google.com/books/?hl=ru&csw=1
// https://developers.google.com/books/docs/v1/using
// http://www.gutenberg.org/browse/languages/ru
// https://www.goodreads.com/api

//todo: 
// обнаружил, что в большинстве текстов не более 200 различных символов, следовательно текст можно хранить в памяти
// как byte[] а не char[]

// todo: java pdf look: https://www.idrsolutions.com

public class App extends MIDlet implements Runnable, CommandListener {
    public static App instance;
    public final static boolean LITE = 1 == 0;
    public final static boolean DEBUG = 1 == 1;
    public final static int SPLASH_TIME = 200; // msec
    public final static String RMS_NAME = "RMS14";

    public static String platform = null;
    public static int platform_id;
    public static boolean isnokia = false;
    public static boolean istouch = false;
    private long now;
    Splash splash;
    public static String local;
    //
    static Command back, cancel, ok, next, exit, clear, feedback, news, fullversion, moreapps, submit, change, sms,
    delete, deletewithfiles, read, buy, search, pack, share, register;
    public static int LIM = 0;
    //
    public App() {
        instance = this;
        //RMS.deleteAllRMS(); log("don't forget remove RMS cleaning !!!");
        get(0);// Just init hashtable
        if (checkClass("com.nokia.mid.ui.orientation.Orientation")){
            OrientListener.start();
        }
        splash = new Splash();
        show(splash);
        splash.repaint(); /// need ?
        splash.serviceRepaints();
        /// temp
        /*Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(1);
        int i = calendar.get(2) + 1;
        String key = String.valueOf((year + 0x2f101 + 0x25bd6) - i);
        put(333, key);//*/

    }
    private void memDetect(){
        int free = (int)Runtime.getRuntime().freeMemory();
        System.out.println("free="+free);
        long total = Runtime.getRuntime().totalMemory();
        System.out.println("total="+total);
/// check extending memory. make it every time, because memsize can be changed between starts
        Vector v = new Vector();
        v.addElement(new byte[free-2048]);
        v.addElement(new byte[256]);
        if (total==Runtime.getRuntime().totalMemory())
            LIM = (free*20)/100; // 25
        System.out.println("LIM: "+ LIM+" free: "+free);
        v = null;
        System.gc();
    }
    public void run() {
        now = System.currentTimeMillis();
        platform_id = checkPlatform();
        if (0==getInt(Res.tUID)){
            int uid = (int)(now + Runtime.getRuntime().freeMemory() + platform.hashCode()*31);
            if (uid<0)
                uid = -uid;
            put(Res.tUID, uid);
            put(Res.tNEWS, new Long(now + 1000*60*60*24*3));// 3 days
        }
        Res.loadDic();
        exit   = new Command(Res.getStringUpper(Res.tEXIT), Command.BACK, 1);
        back   = new Command(Res.getStringUpper(Res.tBACK), Command.BACK, 1);
        ok     = new Command(Res.getStringUpper(Res.tOK),   Command.OK, 1);
        next   = new Command(Res.getStringUpper(Res.tNEXT), Command.OK, 1);
        cancel = new Command(Res.getStringUpper(Res.tCANCEL), Command.CANCEL, 1);
        clear  = new Command(Res.getStringUpper(Res.tCLEAR), Command.OK, 1);
        feedback = new Command(Res.getStringUpper(Res.tFEEDBACK), Command.OK, 1);
        news = new Command(Res.getStringUpper(Res.tNEWS), Command.OK, 1);
        fullversion = new Command(Res.getStringUpper(Res.tFULLVERSION), Command.OK, 1);
        moreapps = new Command(Res.getStringUpper(Res.tMOREAPPS), Command.OK, 1);
        submit = new Command(Res.getStringUpper(Res.tSUBMIT), Command.OK, 1);
        change = new Command(Res.getStringUpper(Res.tCHANGE), Command.OK, 1);
        sms = new Command("sms", Command.OK, 1);
        delete  = new Command(Res.getStringUpper(Res.tDELETE), Command.OK, 1);
        deletewithfiles = new Command(Res.getStringUpper(Res.tDEL_FILES), Command.OK, 1);
        read = new Command(Res.getStringUpper(Res.tREAD), Command.OK, 1);
        buy = new Command(Res.getStringUpper(Res.tRUPAY), Command.OK, 1);
        search = new Command(Res.getStringUpper(Res.tSEARCH), Command.OK, 1);
        pack  = new Command(Res.getStringUpper(Res.tPACK), Command.OK, 1);
        share  = new Command(Res.getStringUpper(Res.tSHARE), Command.OK, 1);
        register = new Command(Res.getStringUpper(Res.tREGISTER), Command.OK, 1);
        memDetect();
        try{
            instance = this;
            //RMS.deleteAllRMS(); // still
            long dt = System.currentTimeMillis() - now;
            if (dt<SPLASH_TIME){
                try {Thread.sleep(SPLASH_TIME-dt);}catch (Exception xc){}
            }
            splash.ended = true; /// coz Asha still gfx on Canvas
            splash.repaint();
            splash.serviceRepaints();
            try {Thread.sleep(100);}catch (Exception xc){}
            splash = null;
            //
            int starts = getInt(Res.tSTARTS)+1;// used for starts
            put(Res.tSTARTS, starts);
            boolean do_feedback = (getInt(Res.tFEEDBACK)<1) && (0==starts%8);
            show(LITE ? liteInfo() : do_feedback? new FeedbackForm() : (Displayable)MainMenu.getInstance());
        }catch(Exception exc){exc.printStackTrace();}
    }
    private Form liteInfo(){
        Form form = new Form(Res.getStringLower(App.LITE ? Res.tLITETITLE: Res.tTITLE));
        form.append(Res.getString(Res.tLITEINFO));
        form.addCommand(App.ok);
        form.setCommandListener(this);
        return form;
    }
    protected void startApp() throws MIDletStateChangeException {}
    public void destroyApp(boolean arg0) throws MIDletStateChangeException {
        show(null);
        RMS.setBytesToRms(RMS_NAME, Tools.obj2bytes(vars));
        notifyDestroyed();
        instance = null;
    }
    protected void pauseApp() {}
    //
    int checkPlatform(){
        platform = getProp("microedition.platform");
        if (null == platform){
            platform = "null";
        }
        //Debug.put("platform", platform);
        put(Res.tABOUT, platform);

        String str = platform.toLowerCase().trim();

        if (str.endsWith("javasdk") || str.endsWith("wtk"))// Sony & Sun JDK emulator
            return 0;
        isnokia = -1!=str.indexOf("nokia");
        if (-1!=str.indexOf("nokia50")){
            //com.nokia.mid.ui.DeviceControl.setLights(0, 20);
            return 501;
        }
        if (-1!=str.indexOf("sonyericsson"))
            return 890;
        return 1;
    }
    public static boolean checkAutoOrientation(){
        return true;
    }
    public static String getProp(String str){
        try {
            return System.getProperty(str);
        } catch (Exception exc) {
            return null;
        }
    }
    public static void log(Object obj){
        System.out.println(obj);
    }
    public static Hashtable vars;

    public static boolean getBool(int key){
        return (null!=get(key));
    }
    public static long getLong(int key){
        Object obj = get(key);
        return (null==obj)? 0 : ((Long)obj).longValue();
    }
    public static int getInt(int key){
        Object obj = get(key);
        return (null==obj)? 0 : obj.hashCode();
    }
    public static int getInt(int key, int def){
        Object obj = get(key);
        return (null==obj)? def : obj.hashCode();
    }
    public static String get(int key, String def){
        String res = (String)get(key);
        return (null==res) ? def : res;
    }
    public static String gets(int key){ return (String)get(key);}
    public static Object get(int key){
        if (null==vars){
            vars = (Hashtable)Tools.bytes2obj(RMS.getBytesFromRms(RMS_NAME));
            if (null==vars)
                vars = new Hashtable();
        }
        return vars.get(new Integer(key));
    }
    public static void put(int kid, int value){
        put(kid, new Integer(value));
    }
    public static void put(int kid, Object value){
        Integer key = new Integer(kid);
        if (null==value)
            vars.remove(key);
        else
            vars.put(key, value);
    }
    public static void show(Displayable dsp){
        Display.getDisplay(instance).setCurrent(dsp);
        //Display.getDisplay(instance).flashBacklight(3000);
    }
    public static void showProgress(String s, boolean fullscreen){
        Display.getDisplay(instance).setCurrent(new ProgressCanvas(s, fullscreen));
    }
    public static void setProgress(int i){
        Displayable displayable = Display.getDisplay(instance).getCurrent();
        if (displayable instanceof ProgressCanvas){
            ((ProgressCanvas) displayable).setProgress(i);
        }
    }
    public static void setFocus(Item item){ Display.getDisplay(instance).setCurrentItem(item); }
///                   BG        FG        CORNER    PIC1      PIC2      PIC3      PIC4      NOTE
    static int[] t0 ={0xffffff, 0x000000, 0xe0e0e0, 0xccffcc, 0xffffcc, 0xccccff, 0xffcccc, 0x8888ff};
    static int[] t1 ={0x181818, 0xffffff, 0x444444, 0x336633, 0x666633, 0x333366, 0x663333, 0x8888ff};
    static int[] t2 ={0xeeddcc, 0x553311, 0xe0a0a0, 0xccffcc, 0xffffcc, 0xccccff, 0xffcccc, 0x8888ff};
    public static int getColor(int id){
        int tid = App.getInt(Res.tCOLORTHEME);
        return (tid==0 ? t0 : tid==1 ? t1 : t2)[id];
    }
    public static int getSystemColor(int colorSpecifier){
        return Display.getDisplay(instance).getColor(colorSpecifier);
    }
    public static int getBorder(){return 2+App.getInt(Res.tBORDER)*2;}
    public static int getBetween(){return (2-App.getInt(Res.tBETWEEN_LINE))*2;}
    public static Font getFont(){
        int fnt_id = App.getInt(Res.tFONT_SIZE);
        int fnt_sz = 0==fnt_id ? Font.SIZE_SMALL : 1==fnt_id ? Font.SIZE_MEDIUM : Font.SIZE_LARGE;
        return Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, fnt_sz);
    }
    public void commandAction(Command c, Displayable d){
        if (c==ok){
            show(MainMenu.getInstance());
        }
    }
    public static boolean isAsha3XX(){
        return isnokia && istouch && (501!=platform_id);
    }
    public static boolean isAsha(){
        return isnokia && istouch;
    }
    private static boolean checkClass(String s){
        try{
            Class.forName(s);
            return true;
        }catch(ClassNotFoundException _ex){
            return false;
        }
    }
}