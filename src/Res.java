/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 23.05.13
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;

public class Res{
    public final static int CB1 = 0;
    public final static int CB2 = 1;
    public final static int CB3 = 2;
    public final static int CORNER_BACK = 3;
    //text
    public final static int tTITLE = 0;
    public final static int tHISTORY = 1;
    public final static int tCHOOSE_BOOK = 2;
    public final static int tDOWNLOAD_BOOK = 3;
    public final static int tHISTORY_EMPTY = 4;
    public final static int tDELETE_FROM_HISTORY = 5, tSTARTS = 5;
    public final static int tERROR = 6;
    public final static int tERROR_CONNECTION = 7;
    public final static int tEXIT = 8;
    public final static int tPLEASE_WAIT = 9;
    public final static int tASK_QUIT = 10;
    public final static int tABOUT = 11;
    public final static int tOK = 12;
    public final static int tCANCEL = 14;
    public final static int tBACK = 15;
    public final static int tINFORMATION = 16;
    public final static int tNEXT = 17;
    public final static int tNOT_FOUND = 18;
    public final static int tHELP = 19, tPAGE_ANIMATION = 19;
    public final static int tABOUT1 = 20, tSHOW_POINTS = 20;
    public final static int tABOUT2 = 21;
    public final static int tFEEDBACK = 22;
    public final static int tLITETITLE = 23;
    public final static int tAVAILABLE = 24;
    public final static int tLOCAL_ABOUT = 25;
    public final static int tDELETE_FROM_HISTORY_AND_FILE = 26, tBETWEEN_LINE = 26;
    public final static int tDOWNLOAD = 27, tBORDER = 27;
    public final static int tPLATFORM = 28, tFONT_SIZE = 28;
    public final static int tOPTIONAL = 29, tCOLORTHEME = 29;
    public final static int tREGISTER = 30;
    public final static int tREG_CODE = 31;
    public final static int tREG_NOTES = 32;
    public final static int tSETTINGS = 33;
    public final static int tREGISTRATION = 34;
    public final static int tYOURNAME = 35;
    public final static int tLOADING = 36;
    public final static int tRUPAY = 37;
    public final static int tRUPAY_TEXT = 38;
    public final static int tTELL_FRIEND = 39;
    public final static int tCLEAR = 40; // will used as flag for sending uid in news request
    public final static int tCLEARALERT = 41;
    public final static int tFILENOTEXIST = 42;
    public final static int tFILE_ERROR = 43;
    public final static int tNEXTPAGE = 44;
    public final static int tPREVPAGE = 45;
    public final static int tPLEASE_ENTER = 46;
    public final static int tYOUR_EMAIL = 47;
    public final static int tSUBMIT = 48;
    public final static int tYOUR_NOTES = 49;
    public final static int tTHANK_FEEDBACK = 50;
    public final static int tNEWS = 51;
    public final static int tENCODING = 52;
    public final static int tOPTIMIZING = 53;
    public final static int tBOOKMARKZ = 54;
    public final static int tPLEASE_SELECT = 55;
    public final static int tNAVIGATION_TOUCH = 56;
    public final static int tNAVIGATION_KEYS = 57;
    public final static int tALLOW_ACCESS = 58;
    public final static int tHELP_1 = 59;
    public final static int tUID = 60;
    public final static int tFULLVERSION = 61;
    public final static int tLITEINFO = 62;
    public final static int tMOREAPPS = 63;
    public final static int tLOCAL = 64;
    public final static int tRESERVED_1 = 65;
    public final static int tPLEASE_REG = 66;
    public final static int tTHANK_REG = 67;

    public final static int tDEF_ENCODING = 68;
    public final static int tLIST_ENCODINGS = 69;
    public final static int tCHANGE = 70;
    public final static int tSETTING_PANEL = 71;
    public final static int tFAST_PAGING = 72;
    public final static int tPREVIOUS_PAGE = 73;
    public final static int tSEE_PICTURES = 74;
    public final static int tNEXT_PAGE = 75;
    public final static int tSEE_NOTES = 76;
    public final static int tTURN_SCREEN = 77;
    public final static int tOF = 78;
    public final static int tNOTES = 79;
    public final static int tCOVER = 80;
    public final static int tSMS = 81;
    public final static int tDELETE = 82;
    public final static int tDEL_FILES = 83;
    public final static int tREAD = 84;
    public final static int tREADED = 85;
    public final static int tFILESIZE = 86;
    public final static int tTOTALMEMORY = 87;
    public final static int tBOOKMARKS = 88;
    public final static int tSEARCH = 89;
    public final static int tNEWBOOKS = 90;
    public final static int tENTERAUTHOR = 91;
    public final static int tPLEASEENTERAUTHOR = 92;
    public final static int tPACK = 93;
    public final static int tSHARE = 94;
    public final static int tPACKING = 95;

    //public final static int t = 9;
    public static String RU_HI = "¿¡¬√ƒ≈®∆«»… ÀÃÕŒœ–—“”‘’◊÷ÿŸ€⁄‹›ﬁﬂ";
    public static String RU_LO = "‡·‚„‰Â∏ÊÁËÈÍÎÏÌÓÔÒÚÛÙı˜ˆ¯˘˚˙¸˝˛ˇ";
    public static String RU_TR = "ABVGDEEJZIYKLMNOPRSTUFH4CWWY'XEUA";
    public static Hashtable dic = new Hashtable();
    public static String getString(int id)  {
        Object obj = dic.get(new Integer(id));
        return (null==obj) ? "null" : obj.toString();
    }
    private static String changeCase(String s, boolean low){
        String s1 = low ? RU_HI : RU_LO;
        String s2 = low ? RU_LO : RU_HI;
        StringBuffer sb = new StringBuffer(s.length());
        for (int i=0; i<s.length(); i++){
            char c = s.charAt(i);
            if (c<256){
                c = low ? Character.toLowerCase(c) : Character.toUpperCase(c);
            }else {
                int id = s1.indexOf(c);
                if (-1!=id)
                    c = s2.charAt(id);
            }
            sb.append(c);
        }
        return sb.toString();
    }
    public static String translit(String s)  {
        s = changeCase(s, false);
        String s1 = RU_HI;
        String s2 = RU_TR;
        StringBuffer sb = new StringBuffer(s.length());
        for (int i=0; i<s.length(); i++){
            char c = s.charAt(i);
            if (c>=256){
                int id = s1.indexOf(c);
                if (-1!=id)
                    c = s2.charAt(id);
            }
            sb.append(c);
        }
        return sb.toString();
    }
    public static String getStringUpper(int id)  {
        String s = getString(id);
        return App.istouch ? changeCase(s, false) : changeCase(s, true);
    }
    public static String getStringLower(int id)  {
        String s = getString(id);
        return App.istouch ? changeCase(s, true) : s;
    }
    public static String getStringLower(String s)  {
        return App.istouch ? changeCase(s, true) : s;
    }
    public static boolean isUSSR(String s){
        int id = "amazbgbeetkakkkyltlvmotgtkukuz".indexOf(s);
        return 0==id%2;
    }
    public static void loadDic(){
        InputStreamReader isr = null;
        try {
            String loc = System.getProperty("microedition.locale").toLowerCase();
            //System.out.println(loc);
            if (null==loc || loc.length()<2)
                loc = "en";
            else if (loc.length()>2)
                loc = loc.substring(0, 2);
            if (isUSSR(loc))
                loc = "ru";
            //loc = "ru";
            App.local = loc;
            InputStream is = App.instance.getClass().getResourceAsStream("/res/"+loc+".txt");
            if (null==is)
                is = App.instance.getClass().getResourceAsStream("/res/en.txt");
            isr = new InputStreamReader(is, "UTF-8");
            StringBuffer sb = new StringBuffer(31);
            String addon = "";
            int i = 0;
            do {
                i = isr.read();
                sb.append((char)i);
            }while (i!='=');
            addon = sb.toString();
            do {
                sb.setLength(0);
                sb.append(addon);
                do {
                    i = isr.read();
                    if (-1!=i)
                        sb.append((char) i);
                } while (-1!=i && '='!=i);
                String str = sb.toString().trim();
                if (-1!=i){
                    int last = str.lastIndexOf('\n');
                    addon = str.substring(last+1).trim();
                    str = str.substring(0, last).trim();
                }
                if (false == "".equals(str) && false == str.startsWith(";")) {
                    int id = str.indexOf('=');
                    if (-1 != id && (id < str.length() - 2)) {
                        String key = new String((str.substring(0, id)).trim());
                        String value = new String((str.substring(id + 1)).trim());
                        dic.put(new Integer(Integer.parseInt(key)), value);
                    }
                }
            } while (-1 != i);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (null != isr) {
                try {
                    isr.close();
                } catch (IOException io) {io.printStackTrace();};
            }
        }
    }
    public static String[] getStrings(String fname) {
        InputStream is = new ByteArrayInputStream(getResource(fname));
        InputStreamReader isr = null;
        String[] mas = null;
        try {
            isr = new InputStreamReader(is, "UTF-8");
            StringBuffer sb = new StringBuffer(31);
            Vector vct = new Vector();
            int i = 0;
            do {
                sb.setLength(0);
                do {
                    i = isr.read();
                    if (i >= ' ')
                        sb.append((char) i);
                } while (i >= ' ');
                String str = new String(sb.toString().trim());
                if ("".equals(str))
                    str = null;
                if (-1!=i)
                    vct.addElement(str);
            } while (-1 != i);
            mas = new String[vct.size()];
            vct.copyInto(mas);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (null != isr)
                try {
                    isr.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            ;
            if (null != is)
                try {
                    is.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            ;
        }
        return mas;
    }
    public static byte[] getResource(String fname) {
        /*if(App.DEBUG)
            Debug.startTime();//*/
        byte[] res = null;
        try {
            DataInputStream in=new DataInputStream(App.instance.getClass().getResourceAsStream("/res/"+fname));
            res = new byte[in.available()];
            in.read(res);
            in.close();
        } catch(Throwable e) { e.printStackTrace();}
        /*if(App.DEBUG)
            Debug.logTime("loading: "+fname+" " ); //*/
        return res;
    }
    private static Hashtable imagesCash = new Hashtable();
    public static Image loadImage(String file) {
        Image image = (Image)imagesCash.get(file);
        if (null==image){
            try {
                image = Image.createImage("/res/" + file);
                imagesCash.put(file, image);
            } catch (Exception exc) {
                exc.printStackTrace();
                //Debug.log("Image not found or invalid: " + file);
            }
        }
        return image;
    }
    public static int readTotal(InputStream is, byte[]bigbuffer, int start, int sz) throws IOException {
        int BUFSIZE = 4096;
        byte[]buf = new byte[sz<BUFSIZE ? sz : BUFSIZE];
        int sum = 0;
        while (true){
            int readed = is.read(buf);
            if (-1!=readed){
                //arraycopy(Object src, int src_position, Object dst, int dst_position, int length)
                System.arraycopy(buf, 0, bigbuffer, start, readed);
                start+=readed;
                sum+=readed;
                sz-=readed;
                if (sz<buf.length){
                    if (0==sz)
                        break;
                    buf = new byte[sz];
                }
            }else
                break;
        }
        return sum;
    }
}
