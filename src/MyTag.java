import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 30.10.14
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public class MyTag {
    private String name;
    private Hashtable table;

    public MyTag(String s){
        int len = s.length();
        int i = 1;
        while (i<len && ' '!=s.charAt(i))
            i++;
        name = s.substring(0, i);
        while (-1!=i && i<len){
            i = s.indexOf(' ', i);
            while (-1!=i&&i<len &&' '==s.charAt(i)){++i;}
            if (-1!=i && i<len){
                int end = s.indexOf('=', i+1);
                if (-1!=end){
                    String key = s.substring(i, end);
                    i=end+1;
                    if (i<len && '"'==s.charAt(i)){
                        end = s.indexOf('"', ++i);
                        if (-1!=end){
                            String value = s.substring(i, end);
                            put (key, value);
                            i = end+1;
                        }
                    }
                }
            }
        }
    }
    public void put(String key, String value){
        if (null==table)
            table = new Hashtable(7);
        if ('#'==value.charAt(0))
            value = value.substring(1);
        table.put(key, value);
    }
    public String get(String key){
        if (null!=table)
            return (String)table.get(key);
        return null;
    }
    public boolean equals(String s){ return name.equals(s); }
    public String toString(){
        if (null==table)
            return name;
        else{
            StringBuffer sb = new StringBuffer(128);
            sb.append(name).append(" [");
            Enumeration e = table.keys();
            Enumeration e2 = table.elements();
            while (e.hasMoreElements()){
                sb.append(e.nextElement()).append(':').append(e2.nextElement());
                if (e.hasMoreElements())
                    sb.append(',');
            }
            sb.append(" ]");
            return sb.toString();
        }
    }
}
