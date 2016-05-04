/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 29.07.13
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.*;
import java.io.*;

public class Tools {
    private static Random rnd = new Random(System.currentTimeMillis() + Runtime.getRuntime().freeMemory());

    /*public static void shuffle(Vector v) {
        if (null != v && false == v.isEmpty()) {
            int sz = v.size();
            Object[] arr = new Object[sz];
            v.copyInto(arr);
            shuffle(arr);
            v.removeAllElements();
            for (int i = 0; i < sz; i++)
                v.addElement(arr[i]);
        }
    }
    public static void shuffle(Object[] arr) {
        int n = arr.length;
        while (n > 1) {
            int k = nextInt(n--); //decrements after using the value
            Object tmp = arr[n];
            arr[n] = arr[k];
            arr[k] = tmp;
        }
    }      */
    public static void shuffle(String[] arr) {
        int n = arr.length;
        while (n > 1) {
            int k = nextInt(n--); //decrements after using the value
            String tmp = arr[n];
            arr[n] = arr[k];
            arr[k] = tmp;
        }
    }
    public static void shuffle(int[] arr) {
        int n = arr.length;
        while (n > 1) {
            int k = nextInt(n--); //decrements after using the value
            int tmp = arr[n];
            arr[n] = arr[k];
            arr[k] = tmp;
        }
    }
    /*public static void partShuffle(int[] arr, int start, int blocklen) {
        while (blocklen > 1) {
            int k = nextInt(blocklen--); //decrements after using the value
            int tmp = arr[start + blocklen];
            arr[start + blocklen] = arr[start + k];
            arr[start + k] = tmp;
        }
    }
    public static int[] getShuffledArray(int start, int blocklen){
        int[] arr = new int[blocklen];
        for(int i=0; i<blocklen; i++)
            arr[i] = start++;
        shuffle(arr);
        return arr;
    }

    public static void shuffle(byte[] arr) {
        int n = arr.length;
        byte tmp;
        while (n > 1) {
            int k = nextInt(n--); //decrements after using the value
            tmp = arr[n];
            arr[n] = arr[k];
            arr[k] = tmp;
        }
    }           */
    public static int[] extendArray(int[] arr, int add){
        int[] newarr = new int[arr.length + add];
        //arraycopy(Object src, int src_position, Object dst, int dst_position, int length)
        System.arraycopy(arr, 0, newarr, 0, arr.length);
        return newarr;
    }

    public static String[] getSubstrings(String str, char delim) {
        if (-1 == str.indexOf(delim))
            return new String[]{str};
        int i = 0;
        int start = 0;
        int skobki = 0;
        Vector vct = new Vector(4);
        int len = str.length();
        while (i != len) {
            char ch = str.charAt(i++);
            if ('(' == ch)
                skobki++;
            else if (')' == ch)
                skobki--;
            else if (delim == ch && 0 == skobki) {
                vct.addElement(new String((str.substring(start, i - 1)).trim()));
                start = i;
            }
        }
        if (start < len) {
            str = new String(str.substring(start).trim());
            if (false == "".equals(str))
                vct.addElement(str);
        }
        String[] mas = new String[vct.size()];
        vct.copyInto(mas);
        return mas;
    }
    public static int nextInt(int range) {
        int rand = rnd.nextInt();
        if (rand < 0)
            rand = -rand;
        return rand % range;
    }
    public static boolean isInt(String s){
        for (int i=s.length()-1; i>=0; i--)
            if (s.charAt(i)<'0' || s.charAt(i)>'9')
                return false;
        return true;
    }
    public static String shortNumber(long i){
        int bytes  = (int)i & 0x3ff;
        int kbytes = (int)((i>>10) & 0x3ff);
        int mbytes = (int)((i>>20) & 0x3ff);
        int gbytes = (int)((i>>30) & 0x3ff);
        if (0!=gbytes){
            mbytes = mbytes/103;
            return gbytes + (mbytes==0? "" : "."+Integer.toString(mbytes))+" Gb";
        }
        if (0!=mbytes){
            kbytes = kbytes/103;
            return mbytes + (kbytes==0? "" : "."+Integer.toString(kbytes))+" Mb";
        }
        if (0!=kbytes){
            bytes = bytes/103;
            return kbytes + (bytes==0? "" : "."+Integer.toString(bytes))+" Kb";
        }
        return Integer.toString(bytes)+" bytes";
    }
    //image l:href="#image002.jpg"
    public static String findAttr(String s, String attr_name){
        int begin = s.indexOf(attr_name);
        if (-1!=begin){
            begin +=attr_name.length()+3;// 3: ="#
            int end = s.indexOf('"', begin);
            if (-1!=end)
                return s.substring(begin, end);
        }
        return null;
    }
    private static boolean isEmailSymbols(String s){
        String[] array = getSubstrings(s, '.');
        for (int j=0; j<array.length; j++){
            s = array[j];
            if (null==s || "".equals(s))
                return false;
            for (int i=s.length()-1; i>=0; i--){
                char ch = s.charAt(i);
                if (false ==((ch>='0'&&ch<='9') || (ch>='A'&&ch<='Z') || (ch>='a'&&ch<='z') || ch=='_'))
                    return false;
            }
        }
        return true;
    }
    public static String getHeader(String path){
        int id = path.lastIndexOf('/');
        return  (-1==id) ? path : path.substring(id+1);
    }
    public static boolean checkName(String s){
        return null!=s && s.length()>=4 && -1==s.indexOf(' ');
    }
    public static boolean checkName(String s, int len){
        return null!=s && s.length()>=len;
    }
    public static boolean isEmail(String s){
        if (null!=s && s.length()>=6){
            String[] mail = getSubstrings(s, '@');
            if (2==mail.length){
                if (false==isEmailSymbols(mail[0]))
                    return false;
                int last_point = mail[1].lastIndexOf('.');
                if (-1==last_point||last_point>=mail[1].length()-2)
                    return false;
                return isEmailSymbols(mail[1]);
            }
        }
        return false;
    }
/// base 64
    public static int decodeBytes64(byte[] out){
        byte[] arr = new byte[123];
        for (byte i=0; i<64; i++)
            arr["ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(i)] = i;
        int len = out.length;
        byte[] byte4 = new byte[4];
        int i = 0;
        try{
            for (int j=0; j<len;){
                for (int k=0; k<4;){
                    byte b = out[j++];
                    if (b>='+' && b<='z')
                        byte4[k++] = b;
                }
                out[i++] = (byte)(arr[byte4[0]]<<2 | arr[byte4[1]]>>4);
                if ('='!=byte4[2])
                    out[i++] = (byte)(arr[byte4[1]]<<4 | arr[byte4[2]]>>2);
                if ('='!=byte4[3])
                    out[i++] = (byte)(arr[byte4[2]]<<6 | arr[byte4[3]]);
            }
        }catch (Exception exc){/*exc.printStackTrace();*/}
        return i;
    }
///* TYPES for serialization
    public final static byte TYPE_NULL    = 0;
    public final static byte TYPE_BYTES   = 1;
    public final static byte TYPE_STRINGS = 2;
    public final static byte TYPE_INTS    = 3;
    public final static byte TYPE_BOOLS   = 4;
    public final static byte TYPE_STRING  = 5;
    public final static byte TYPE_HASHTABLE = 6;
    public final static byte TYPE_INTEGER   = 7;
    public final static byte TYPE_BOOLEAN   = 8;
    public final static byte TYPE_VECTOR    = 9;
    public final static byte TYPE_OBJECTS   = 10;
    public final static byte TYPE_SHORT     = 11;
    public final static byte TYPE_SHORTS    = 12;
    public final static byte TYPE_LONG      = 14;
    public final static byte TYPE_      = 16;
//
    final static Boolean TRUE = new Boolean(true);
    final static Boolean FALSE = new Boolean(false);
//
    public static void writeObjectToStream(DataOutputStream dos, Object obj) throws IOException {
        if (null == obj) {
            dos.writeByte(TYPE_NULL);
        } else {
            int i = 0;
            if (obj instanceof String) {
                dos.writeByte(TYPE_STRING);
                dos.writeUTF((String) obj);
            } else if (obj instanceof Vector) {
                Vector vct = (Vector) obj;
                dos.writeByte(TYPE_VECTOR);
                dos.writeInt(vct.size());
                while (i < vct.size())
                    writeObjectToStream(dos, vct.elementAt(i++));
            }
            else if (obj instanceof Integer) {
                dos.writeByte(TYPE_INTEGER);
                dos.writeInt(obj.hashCode());
            } else if (obj instanceof Boolean) {
                dos.writeByte(TYPE_BOOLEAN);
                dos.writeBoolean(((Boolean) obj).booleanValue());
            } else if (obj instanceof byte[]) {
                dos.writeByte(TYPE_BYTES);
                dos.writeInt(((byte[]) obj).length);
                dos.write((byte[]) obj);
            } else if (obj instanceof String[]) {
                String[] mas = (String[]) obj;
                dos.writeByte(TYPE_STRINGS);
                dos.writeInt(mas.length);
                while (i < mas.length)
                    writeObjectToStream(dos, mas[i++]);
            } else if (obj instanceof int[]) {
                int[] mas = (int[]) obj;
                dos.writeByte(TYPE_INTS);
                dos.writeInt(mas.length);
                while (i < mas.length)
                    dos.writeInt(mas[i++]);
            } else if (obj instanceof boolean[]) {
                boolean[] mas = (boolean[]) obj;
                dos.writeByte(TYPE_BOOLS);
                dos.writeInt(mas.length);
                while (i < mas.length)
                    dos.writeBoolean(mas[i++]);
            } else if (obj instanceof Hashtable) {
                Hashtable tab = (Hashtable) obj;
                dos.writeByte(TYPE_HASHTABLE);
                dos.writeInt(tab.size());
                Enumeration e = tab.keys();
                Enumeration e2 = tab.elements();
                while (e.hasMoreElements()){
                    writeObjectToStream(dos, e.nextElement());
                    writeObjectToStream(dos, e2.nextElement());
                }
            }
            else if (obj instanceof Short) { // set new here
                dos.writeByte(TYPE_SHORT);
                dos.writeShort(obj.hashCode());
            }
            else if (obj instanceof short[]) {
                short[] mas = (short[]) obj;
                dos.writeByte(TYPE_SHORTS);
                dos.writeInt(mas.length);
                while (i < mas.length)
                    dos.writeShort(mas[i++]);
            }
            else if (obj instanceof Long) {
                dos.writeByte(TYPE_LONG);
                dos.writeLong(((Long) obj).longValue());
            }
            /* else if (obj instanceof NEW_TYPE) { // set new here
                             }*/
            else if (obj instanceof Object[]) { /* MUST TO BE LAST */
                Object[] objs = (Object[]) obj;
                dos.writeByte(TYPE_OBJECTS);
                dos.writeInt(objs.length);
                while (i < objs.length)
                    writeObjectToStream(dos, objs[i++]);
            }
        }
    }
//
    public static Object readObjectFromStream(DataInputStream dis) throws IOException {
        int i = dis.readByte();
        switch (i) {
            case TYPE_NULL:
                return null;
            case TYPE_STRING:{
                String res = null;
                try{
                    res = dis.readUTF();
                }catch(Exception exc){
                    exc.printStackTrace();
                }
                return res;
            }
            case TYPE_HASHTABLE:
                i = dis.readInt();
                Hashtable tab = new Hashtable((i * 4 / 3) | 1);
                while (--i >= 0)
                    tab.put(readObjectFromStream(dis), readObjectFromStream(dis));
                return tab;
            case TYPE_BOOLEAN:
                return (dis.readBoolean()) ?  TRUE : FALSE;
            /// Arrays & Vector
            case TYPE_VECTOR:
                i = dis.readInt();
                Vector vct = new Vector(i);
                while (--i >= 0)
                    vct.addElement(readObjectFromStream(dis));
                return vct;
            case TYPE_BYTES:
                byte[] mas = new byte[dis.readInt()];
                dis.read(mas);
                return mas;
            case TYPE_STRINGS:
                Object[] keys = new String[dis.readInt()];
                for (i = 0; i < keys.length; )
                    keys[i++] = readObjectFromStream(dis);
                return keys;
            case TYPE_INTS:
                int[] mas3 = new int[dis.readInt()];
                for (i = 0; i < mas3.length; )
                    mas3[i++] = dis.readInt();
                return mas3;
            case TYPE_BOOLS:
                boolean[] mas4 = new boolean[dis.readInt()];
                for (i = 0; i < mas4.length; )
                    mas4[i++] = dis.readBoolean();
                return mas4;
            case TYPE_INTEGER:
                return new Integer(dis.readInt());
            case TYPE_SHORT:
                return new Short(dis.readShort());
            case TYPE_SHORTS:
                short[] mas5 = new short[dis.readInt()];
                for (i = 0; i < mas5.length; )
                    mas5[i++] = dis.readShort();
                return mas5;
            case TYPE_OBJECTS: ///---- MUST TO BE LAST
                keys = new Object[dis.readInt()];
                for (i = 0; i < keys.length; )
                    keys[i++] = readObjectFromStream(dis);
                return keys;
            case TYPE_LONG:
                return new Long(dis.readLong());
            default:
                return null;
        }
    }
    public static byte[] obj2bytes(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            writeObjectToStream(dos, obj);
            byte[] res = baos.toByteArray();
            dos.close();
            return res;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
    public static Object bytes2obj(byte[] arr) {
        if (null!=arr){
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(arr));
            try {
                Object res = readObjectFromStream(dis);
                dis.close();
                return res;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }
}