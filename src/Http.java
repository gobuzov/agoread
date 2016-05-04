import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;

interface HttpListener { public void process(Http http);}
public class Http implements Runnable, CommandListener{
    public final static String URL = "http://localhost/agoreader/";
    //public final static String URL = "http://gobuzov.ru/agoreader/";
    public final static int ERROR = 0;
    public final static int GOOD = 1;
    public final static int CANCEL = 2;
    private HttpListener listener;
    private Displayable prev;
    private Hashtable params;
    private String com;
    private String responce;
    private int result;
    protected Http(){};
    private Http(HttpListener listener, Hashtable params, Displayable prev, String com){
        this.listener = listener;
        this.params = params;
        this.prev = prev;
        this.com = com;
    }
    public static void go(HttpListener listener, Hashtable params, Displayable prev, String com){
        Http http = new Http(listener, params, prev, com);
        App.show(new LoadingSplash(http));
        new Thread(http).start();
    }
    private String createMessage(){
        StringBuffer sb = new StringBuffer(256);
        Enumeration keys = params.keys();
        Enumeration elements = params.elements();
        while (keys.hasMoreElements()){
            sb.append(keys.nextElement()).append('=').append(elements.nextElement());
            if (keys.hasMoreElements())
                sb.append('&');
        }
        return sb.toString();
    }
    public void run() {
        HttpConnection hc = null;
        InputStream in = null;
        OutputStream out = null;
        String message = createMessage();
        //Debug.log(message);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(32);
        result = ERROR;
        try {
            String url = URL.concat(com).concat(".php");
            //Debug.log(url);
            hc = (HttpConnection) Connector.open(url);
            hc.setRequestMethod(HttpConnection.POST);
            hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            hc.setRequestProperty("Content-Length", Integer.toString(message.length()));
            out = hc.openOutputStream();
            out.write(message.getBytes());
            in = hc.openInputStream();
            byte[] buffer = new byte[256];
            while (CANCEL!=result){
                int read = in.read(buffer);
                if (-1==read)
                    break;
                baos.write(buffer, 0, read);
            }
            responce = new String (baos.toByteArray());
            //Debug.log(responce);
            if (CANCEL!=result)
                result = GOOD;
        }
        catch (Exception ioe) {
            Form form = new Form(Res.getString(Res.tERROR));
            form.append(Res.getString(Res.tERROR_CONNECTION));
            form.append(ioe.toString()); // ? need ?
            form.addCommand(App.ok);
            form.setCommandListener(this);
            App.show(form);
        }
        finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (hc != null) hc.close();
                if (null!=baos) baos.close();
            }
            catch (IOException ioe) {}
        }
        if (GOOD==result)
            listener.process(this);
    }
    public String getResponce(){return responce;}

    public void commandAction(Command c, Displayable d){
        if (App.cancel.equals(c))
            result = CANCEL;
        App.show(prev);
    }
}
class LoadingSplash extends Canvas implements Runnable{
    public final static int PAUSE = 300;
    private boolean running = true;
    int id = 0;

    public LoadingSplash(CommandListener commandListener) {
        super();
        setFullScreenMode(true);
        setTitle(Res.getString(Res.tTITLE));
        addCommand(App.cancel);
        setCommandListener(commandListener);
    }
    public void run(){
        try {
            while (running) {
                repaint();
                serviceRepaints();
                Thread.sleep(PAUSE);
                ++id;
            }
        } catch (Exception x) { x.printStackTrace(); }
    }
    public void showNotify() {
        setFullScreenMode(false);
        running = true;
        new Thread(this).start();
    }
    public void hideNotify() {
        running = false;
        setFullScreenMode(true);
    }
    public void paint(Graphics g) {
        setFullScreenMode(true);
        Font f = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        g.setColor(App.getSystemColor(Display.COLOR_BACKGROUND));
        int w = getWidth();
        int h = getHeight();
        g.fillRect(0, 0, w, h);
        int d = w;
        if (d > h)
            d = h;
        int i = id % 8;
        g.setColor(0xcccccc);
        d = d>>3;
        int x = (w - 7 * d)>>1;
        int y = (h - d)>>1;
        for (int j=0; j<i; j++){
            g.fillRect(x + j*d, y, d-2, d-2);
        }
        g.setFont(f);
        g.setColor(App.getSystemColor(Display.COLOR_FOREGROUND));
        String s = Res.getString(Res.tLOADING);
        g.drawString(s, (w - f.stringWidth(s))>>1, y + d + f.getHeight() + 2, Graphics.BOTTOM | Graphics.LEFT);
    }
}