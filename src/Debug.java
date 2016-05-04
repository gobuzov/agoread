/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 29.07.13
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import javax.microedition.lcdui.*;

public class Debug implements CommandListener {
    private static Vector vct;
    private static Hashtable table;
    private static Displayable prev;
    private static long start_time;
    //
    public static void startTime(){ start_time = System.currentTimeMillis();}
    public static void logTime(String str){
        log(str+(System.currentTimeMillis()-start_time));
        startTime();
    }
    public static void logObj(Object obj) {
        if (null == vct)
            vct = new Vector(5);
        if (obj instanceof String)
            log(obj.toString());
        else if (obj instanceof String[]){
            String[] arr = (String[])obj;
            for (int i=0; i<arr.length; i++)
                log(arr[i]);
        }else if (obj instanceof Hashtable){
            StringBuffer sb = new StringBuffer("Hashtable: ");
            Hashtable t = (Hashtable)obj;
            Enumeration e = t.keys();
            Enumeration e2 = t.elements();
            while (e.hasMoreElements())
                sb.append(e.nextElement()).append(": ").append(e2.nextElement()).append("; ");
            sb.append('\n');
            log(sb.toString());
        }else if (obj instanceof Vector){
            StringBuffer sb = new StringBuffer("Vector: ");
            Vector v = (Vector)obj;
            for(int i=0; i<v.size(); i++)
                sb.append(v.elementAt(i)).append(", ");
            sb.append('\n');
            log(sb.toString());
        }
    }
    public static void logMem(String str){
        System.gc();
        log(str+" "+Long.toString(Runtime.getRuntime().freeMemory()) + " / " +
                Long.toString(Runtime.getRuntime().totalMemory()));
    }
    public static void log(String str){
        if (null == vct)
            vct = new Vector(5);
        //vct.addElement(str);
        System.out.print(str);
    }

    public static void put(String key, String value) {
        if (null == table) {
            table = new Hashtable(5);
        }
        table.put(key, value);
    }
    public static void show(Displayable curr) {
        prev = curr;
        System.gc();
        Form form = new Form(Long.toString(Runtime.getRuntime().freeMemory()) + " / " +
                Long.toString(Runtime.getRuntime().totalMemory()));

        if (null != vct || null != table) {
            StringBuffer sb = new StringBuffer(128);
            if (null != table) {
                Enumeration e = table.keys();
                Enumeration e2 = table.elements();
                while (e.hasMoreElements()) {
                    sb.append(e.nextElement()).append(": ").append(e2.nextElement()).append('\n');
                }
            }
            for (int i = 0; null != vct && i < vct.size(); i++) {
                sb.append(vct.elementAt(i)).append('\n');
            }
            if (0 != sb.length()) {
                form.append(new TextField(null, sb.toString(), sb.length(), TextField.ANY));
                form.addCommand(new Command("Clear", Command.OK, 1));
            }
        }
//
        form.addCommand(new Command("Back", Command.BACK, 1));
        form.setCommandListener(new Debug());
        App.show(form);
    }
    public void commandAction(Command cmd, Displayable form) {
        if (cmd.OK == cmd.getCommandType()) {
            if (null != table)
                table.clear();
            if (null != vct)
                vct.removeAllElements();
        }
        App.show(prev);
    }
}
