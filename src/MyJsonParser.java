import java.util.*;
/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 21.10.14
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
public class MyJsonParser {
    private String pstring;
    private int i=0, len;

    public MyJsonParser (String str){
        pstring = str;
        len = pstring.length();
    }
    public Object parse(){
        Hashtable table = null;
        Vector vector = null;
        String str = null;
        while (i <len && null==table && null==vector && null==str){
            char ch = pstring.charAt(i++);
            if ('{'==ch){
                table = new Hashtable();
            } else if ('['==ch){
                vector = new Vector(3, 1);
            } else if (']'==ch){
                str = "]";
            } else if ('}'==ch){
                str = "}";
            } else if ('"'==ch){
                int start = i;
                while ('"'!=pstring.charAt(i++)||pstring.charAt(i -2)=='\\');
                str = pstring.substring(start, i -1);
            } else if ((ch>='0' && ch<='9')||'t'==ch || 'f'==ch || 'n'==ch){// true, false, null
                int start = i -1;
                do{
                    ch = pstring.charAt(i++);
                } while ('}'!=ch && ']'!=ch && ','!=ch);
                str = pstring.substring(start, --i);
            }
        }
        if (null!=table){
            while (true){
                Object key = parse();
                if ("}".equals(key))
                    return table;
                table.put(key, parse());
            }
        }else if (null!=vector){
            while (true){
                Object obj = parse();
                if ("]".equals(obj))
                    return vector;
                vector.addElement(obj);
            }
        }
        return str;
    }
}