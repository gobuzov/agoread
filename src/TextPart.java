import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 28.01.14
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class TextPart {
    public class Remark {
        String noteId;
        int start;
        int len;
    }
    private static Hashtable h = new Hashtable(); // HTML characters
    static {
        h.put("nbsp", " ");
        h.put("euro", "И");
        //h.put("pound", "?");
        h.put("laquo", "Ђ");
        h.put("raquo", "ї");
        h.put("bull", "Х");
        h.put("copy", "©");
        h.put("reg", "Ѓ");
        h.put("trade", "Щ");
        h.put("deg", "∞");
        h.put("eacute", "e");
        h.put("agrave", "a");
        h.put("ocirc", "o");
        /*h.put("", "");
        h.put("", "");//*/
    }
/*
 int value = 0, DVALUE;
 DVALUE = bytes.length>>4;
 OptimizingSplash splash = OptimizingSplash.getInstance();
 splash.setText(Res.getString(Res.tENCODING));
*/
    private CharVector cv;
    private Hashtable pictures = new Hashtable();// (key : String picId,  value: Integer ofset)
    private Vector remarks = new Vector();   // of Remark
    private Flyweight queFonts = new Flyweight(), queColors = new Flyweight();

    public int getLen(){ return cv.getLen();}
    public Flyweight getQueFonts(){ return queFonts; }
    public Flyweight getQueColors(){ return queColors; }
    public CharVector getChars(){ return cv;}

    public String getString(){ return cv.getString(0, cv.getLen());}
    public String[] getPicturesOnPage(FileLink link){
        int begin = link.getOffset(), end = link.getNextOffset();
        Vector result = new Vector();
        Vector prev   = new Vector();
        Vector after  = new Vector();
        Enumeration keys = pictures.keys();
        Enumeration values = pictures.elements();
        while (keys.hasMoreElements()){
            Object key = keys.nextElement();
            int ofset = values.nextElement().hashCode();
            if (ofset<begin)
                prev.addElement(key);
            else if (ofset>end)
                after.addElement(key);
            else
                result.addElement(key);
        }
        if (false==result.isEmpty()){
            for (int i=0; i<after.size(); i++)
                result.addElement(after.elementAt(i));
            for (int i=0; i<prev.size(); i++)
                result.addElement(prev.elementAt(i));
        }
        if (result.isEmpty())
            return null;
        String[] arr = new String[result.size()];
        result.copyInto(arr); result = null;
        return arr;
    }
    public String[] getRemarksOnPage(FileLink link){
        int begin = link.getOffset(), end = link.getNextOffset();
        Vector result = new Vector(2);
        for (int i=0; i< remarks.size(); i++){
            Remark rem = (Remark) remarks.elementAt(i);
            if (rem.start>=begin && rem.start<=end)
                result.addElement(rem.noteId);
        }
        if (result.isEmpty())
            return null;
        String[] arr = new String[result.size()];
        result.copyInto(arr); result = null;
        return arr;
    }
    public boolean findRemark(int offset){
        for (int i=0; i<remarks.size(); i++){
            Remark rem = (Remark) remarks.elementAt(i);
            if (Math.abs(rem.start-offset)<=10)
                return true;
        }
        return false;
    }
    public TextPart(ByteVector bv, FileLink fl){
        Debug.logMem("start process:");
        cv = new CharVector();
        Debug.logMem("new chars:");
        new Encoder(bv, cv, fl);
        bv = null;
        Debug.logMem("bytes = null:");
/*        if (chars.length<300){
            String s = new String(chars);
            Debug.log(s);
        }*/
        Debug.log("encoded:"+cv.getLen());
/*
        value = 0;
        splash.setText(Res.getString(Res.tOPTIMIZING));*/
        queFonts.addRecord(0, 24);//24 = 3<<3
        queColors.addRecord(0, c.COLOR_FG);
        //
        optimizeText(fl, queColors);
        Debug.log("optimized:" + cv.getLen());
        System.out.println("FREE: "+Runtime.getRuntime().freeMemory());
        cv.trim(cv.getLen());
        queFonts.trim();
        queColors.trim();
        System.gc();
        System.out.println("FREE: "+Runtime.getRuntime().freeMemory());
    }
    private void checkFB2Cover(FileLink fl){
        if (fl.fb2 && null!=fl.cover && fl.isFirstPage()){
            pictures.put(fl.cover, new Integer(0));
        }
    }
    Remark remark;
    private void optimizeText(FileLink fl, Flyweight queColors){
        int sz = cv.getLen()-1;
        int i = 0, j = 0;
        boolean fb2 = fl.fb2;
        boolean html = fl.html;
        boolean plain = !fb2 && !html && !fl.isEpub();
        int colorNote = c.COLOR_NOTE;
        int colorFg = c.COLOR_FG;
        int cnt = 0;
        checkFB2Cover(fl);
        try{
            char a=' ', b=' ', c=' ';// обрабатываем b, но и имеем предыдущий (a) и следущий символ (c)
            while (i<sz){
                c = cv.getChar(i++);
                if ('<'==c && false==plain){// в plaintext оставл€ем содержимое тегов.
                    int k = i;
                    do{// kill tag
                        c = cv.getChar(k++);
                    }while (c!='>');
                    c = ' ';
                    String tag = cv.getString(i, k-i-1);
                    cnt++;
                    if (fb2){
                        if (tag.equals("/p"))
                            c = 0xd;
                        if (tag.startsWith("image")){
                            String picId = Tools.findAttr(tag, "href");
                            pictures.put(picId, new Integer(j));
                            Debug.log(picId);
                        }else if (tag.startsWith("a ")){
                            String noteId = Tools.findAttr(tag, "href");
                            remark = new Remark();
                            remark.noteId = noteId;
                            remark.start = j;
                            queColors.addRecord(j, colorNote);
                        }else if ("/a".equals(tag)){
                            remark.len = j - remark.start;
                            remarks.addElement(remark);
                            queColors.addRecord(j, colorFg);
                        }
                    }else if (html){
                        System.out.println(cnt+" "+tag);
                        MyTag mt = new MyTag(tag);
                        System.out.println(mt.toString());
                    }
                    i = k;
                }
                if ('&'==c){// html characters
                    int k = i;
                    if ('#'==cv.getChar(k)){// Ex: &#121
                        ++k;
                        while (cv.getChar(k)>='0' && cv.getChar(k)<='9')
                            ++k;
                        c = ' ';
                        try {
                            String s = cv.getString(i+1, k-i+1);
                            c = (char)Integer.parseInt(s);
                        }catch (Exception exc){exc.printStackTrace();};
                        i = k;
                    }else{// Ex: &eacute;
                        while (k-i < 10 && (';'!=cv.getChar(k)))
                            ++k;
                        if (';'==cv.getChar(k)){
                            String s = cv.getString(i, k-i);
                            String htm = (String)h.get(s);
                            c = (null==htm)? ' ' : htm.charAt(0);
                            i = k+1;
                        }
                    }
                }
                if (0xa==c)
                    c = 0xd;
                if (0xa0==c)
                    c = 0x20;
                if ('Ч'==c || 8211==c)
                    c = '-';
                if ('\\'==b){
                    if('n'==c){
                        b = 0xd;
                        c = ' ';
                    }
                }else if (0xd==b){
                    if (-1==".;)!?".indexOf(a))// предложение не кончилось
                        b = ' ';
                    if (a!=b || (0xd!=b && ' '!=b))// не будут 2 '\n' подр€д и 2 ' '
                        cv.putChar(j++, b);
                }else if (' '==b){
                    if (' '!=a)// не будут 2 ' ' подр€д
                        cv.putChar(j++, b);
                }else if ('-'==b){
                    if ('-'==a){//// не будут 2 '-' подр€д

                    }else if (' '==a)// правильное употребление ' -'
                        cv.putChar(j++, b);
                    else{
                        if (' '!=c)// тоже правильное, например: что-то, кто-то итд
                            cv.putChar(j++, b);
                        else// удал€ем остаток переноса, например: переправ-  лена
                            while (' '==c)
                                c = cv.getChar(i++);
                    }
                }else {
                    cv.putChar(j++, b);
                    if (i>=sz){// tag - последнее в строке
                        cv.setLen(j);
                        return;
                    }
                }
                a = b; b = c;
                /*if (i>value){
                    value+=DVALUE;
                    OptimizingSplash.getInstance().setValue(i*8/sz);
                }//*/
            }
            cv.putChar(j++, c);
            //chars[j++] = chars[sz];
        }catch (Exception exc){exc.printStackTrace();}
        cv.setLen(j);
    }
}