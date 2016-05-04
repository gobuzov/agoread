import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 04.01.15
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class MyXml {
    private Hashtable table = new Hashtable(5);
    private Vector children = new Vector(1);
    private String type, content;

    private static int line; // current line
    private static boolean utf8 = true;
    private static MyXml root;// root element
    private static InputStream is;
    private static StringBuffer sb;
    private static Hashtable dic;
    private static Stack stack;
    //
    public MyXml (String type){this.type = type;}
    public void put(Object key, Object value){table.put(key, value);}
    public Object get(Object key){return table.get(key);}
    public String gets(Object key){return (String)table.get(key);}

    public MyXml findByType(String t){
        if (type.equals(t))
            return this;
        for (int i=0; i<children.size(); i++){
            MyXml res = ((MyXml)children.elementAt(i)).findByType(t);
            if (null!=res)
                return res;
        }
        return null;
    }
    public Vector getChildren(){return children;}

    public static MyXml parse (InputStream d) throws IOException{
        if (null!=d){
            is = d;
            IOException ioe = null;
            line = 0; utf8 = true; sb = new StringBuffer(48); dic = new Hashtable(); stack = new Stack();

            try {
                do {
                    handleText();
                } while (handleTag());
            } catch (IOException x) {
                ioe = new IOException(x.getMessage().concat(" at line " + line));
            }
            dic.clear(); // after good or bad parsing, free memory
            dic = null;
            sb = null;
            stack = null;

            if (null!=is){
                try{
                    is.close();
                }catch (IOException x) {
                    x.printStackTrace();
                }
            }
            is = null;

            if (null != ioe) {
                throw ioe;
            }
        }else{
            throw new IOException("InputStream is null");
        }
        MyXml myx = root; root = null; return myx;
    }
    //
    private static void handleText() throws IOException{
        String str = readUntil('<');
        /// check: if only symbols with spaces, skip this string
        boolean noSpace = false;
        for (int i = str.length() - 1; i >= 0; i--)
            noSpace |= str.charAt(i) > ' ';
        if (null != root && noSpace) {
            str = useDic(checkEntities(str));
            root.content = root.content==null? str : root.content.concat(str);
        }
    }
    private static boolean handleTag() throws IOException {
        String data = readUntil('>');
        char ch = data.charAt(0);
        if ('?' == ch) {
            utf8 = ( -1 != data.indexOf("encoding=\"UTF-8\"")); // add other encoding here
            return true;
        }
// <!-- Check comments --> (& skip it)
        else if ('!' == ch) {
            if (data.startsWith("!--") && data.endsWith("--"))
                return true;
            else
                throw new IOException("Invalid XML comment : " + data + " ");
        }
// check close Tag : </tagName>
        else if ('/' == ch) {
            if (null==root)
                throw new IOException("closed XML element at start");
            if (root.type.equals(getWord(data, 1))) {
                stack.pop();
                if (stack.empty())
                    return false;
                root = (MyXml) stack.peek();
                return true;
            } else {
                throw new IOException("Unclose XML element");
            }
        }
// open tag <tagName>
        String tagName = useDic(getWord(data, 0));
// try to fix son;
        MyXml son = new MyXml(tagName);
        int i = tagName.length(); // id at data
        int len = data.length();
        while (true) {
// get attrs
            String aName = getWord(data, i);
            if (null == aName || 0 == aName.length())
                break;
            i = data.indexOf('=', i);
            if ( -1 == i)
                throw new IOException("Attribute not complete :"+aName);   // 903 7702650
/// try find first time symbol _'_ or _"_
            for (++i; i < len; i++) {
                ch = data.charAt(i);
                if ('\'' == ch || '"' == ch)
                    break;
            }
/// check for second time symbol _'_ or _"_
            end = data.indexOf(ch, ++i);
            if ( -1 == end)
                throw new IOException("Attribute not complete :"+aName);

            son.put(useDic(aName), useDic(checkEntities(data.substring(i, end))));
            i = end + 1;
        }
        if (null != root)
            root.children.addElement(son);
// check : new dat is closed? <newData />
        if ('/' != data.charAt(data.length() - 1)) {
            stack.push(son);
            root = son;
        }else{
            if (null==root){
                root = son;
                return false;
            }
        }
        return true;
    }
    //
    private static String readUntil( char endChar ) throws IOException{
        sb.setLength(0);
        int ch = 0;
        do {
            ch = is.read();
            if (-1==ch)
                throw new IOException("EOF");
            if (0xa == ch)
                line++;
            if (ch>127 && utf8){
                int nextByte = 0;
                if ((ch & 0xf8) == 0xf0) {
                    ch = (ch & 0x1f) << 6;
                    nextByte = is.read() & 0xff;
                    if ((nextByte & 0xc0) != 0x80)
                        throw new IOException("Invalid UTF-8 format");
                    ch += (nextByte & 0x3f) << 6;
                    nextByte = is.read() & 0xff;
                    if ((nextByte & 0xc0) != 0x80)
                        throw new IOException("Invalid UTF-8 format");
                    ch += (nextByte & 0x3f) << 6;
                    nextByte = is.read() & 0xff;
                    if ((nextByte & 0xc0) != 0x80)
                        throw new IOException("Invalid UTF-8 format");
                    ch += (nextByte & 0x3f);
                } else if ((ch & 0xf0) == 0xe0) {
                    ch = (ch & 0x1f) << 6;
                    nextByte = is.read() & 0xff;
                    if ((nextByte & 0xc0) != 0x80)
                        throw new IOException("Invalid UTF-8 format");
                    ch += (nextByte & 0x3f) << 6;
                    nextByte = is.read() & 0xff;
                    if ((nextByte & 0xc0) != 0x80)
                        throw new IOException("Invalid UTF-8 format");
                    ch += (nextByte & 0x3f);
                } else if ((ch & 0xe0) == 0xc0) {
                    ch = (ch & 0x1f) << 6;
                    nextByte = is.read() & 0xff;
                    if ((nextByte & 0xc0) != 0x80)
                        throw new IOException("Invalid UTF-8 format");
                    ch += (nextByte & 0x3f);
                }
            }
            if (ch!=endChar)
                sb.append((char) ch);
        } while (ch!=endChar);
        return sb.toString();
    }
    private static int end; // global 4 return
    public static String getWord (String str, int start){
        if (null == str)
            return null;
        int len = str.length();

        while (start < len && (str.charAt(start)<=' ' || str.charAt(start)=='/'))
            start++;

        if (start >= len)
            return null;

        end = start;
        while (true) {
            char ch = str.charAt(end);
            if (ch<=' ' || '=' == ch || '/' == ch)
                break;
            if (++end == len)
                break;
        }
        return str.substring(start, end).trim();
    }
    private static String checkEntities(String str) throws IOException{
        if (-1==str.indexOf('&'))
            return str;
        int start = 0;
        sb.setLength(0);
        String[] entities = {"&<>'\"", "amp", "lt", "gt", "apos", "quot"};
        while(true){
            int i = str.indexOf('&', start);
            if (i<0){
                if (start<str.length())
                    sb.append(str.substring(start));
                break;
            }
            else {
// add before entites
                sb.append(str.substring(start, i));
                int j = str.indexOf(';', i+1);
                if (-1!=j){
                    String ent = str.substring(i+1, j);
                    for (i=entities.length-1; i>0; i--){
                        if (entities[i].equals(ent)){
                            sb.append(entities[0].charAt(i-1));
                            break;
                        }
                    }
                    if (-1==i)
                        throw new IOException("Unknown Entity: ".concat(str));
                    start = j+1;
                }else{
                    throw new IOException("Invalid Entity, not closed : ".concat(str));
                }
            }
        }
        return sb.toString();
    }
    private static String useDic(String str){
        Object obj = dic.get(str);
        if (null != obj)
            return (String) obj;
/// new String, due leak memory at StringBuffer.toString() & String.substring()
        String newStr = new String(str);
        dic.put(newStr, newStr);
        return newStr;
    }
}