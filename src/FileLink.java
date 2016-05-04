import java.util.Hashtable;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 27.01.14
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public class FileLink {
    public final static String RMS_PREFIX = "FL";
    private String path;
    String cover;// <coverpage> in fb2
    int offset = 0;// in current section
    int nextoffset = 0;
    int sectionId = 0; // curr
    private int rmsId = -1;
    private int filesize;
    int encoding = Encoder.ENC_UNKNOWN;
    boolean html, fb2, encoding_defined, from_lib/*загружен ли файл из онлайн-библиотеки*/;
    Vector notes;// String id, String title; тоесть по 2 строки на примечание
    Vector binaries;// Object[] [0]-String id, [1]- Integer - offset, [2]- Integer - blocklen
    Hashtable table;
    private int[] bookmarks; // now only 6   // todo: проверить, учитываются ли разные части файла здесь?
    private int[] sections_offsets, sections_lens;
//
    Vector sections_offsets_temp, sections_lens_temp;// only for add, later move to not temp int[], see closeLink()

    public FileLink(String path){
        this.path = path;
        offset = 0;
        sectionId = 0;
        notes = new Vector(3);
        binaries = new Vector(3);
    }
    public FileLink (Vector v){
        try{
            int i = 0;
            path = (String)v.elementAt(i++);
            cover = (String)v.elementAt(i++);
            html = ((Boolean)(v.elementAt(i++))).booleanValue();
            fb2 = ((Boolean)(v.elementAt(i++))).booleanValue();
            encoding_defined = ((Boolean)(v.elementAt(i++))).booleanValue();
            from_lib = ((Boolean)(v.elementAt(i++))).booleanValue();
            notes = (Vector)v.elementAt(i++);
            binaries = (Vector)v.elementAt(i++);
            table = (Hashtable)v.elementAt(i++);
            bookmarks = (int[])v.elementAt(i++);
            sections_offsets  = (int[])v.elementAt(i++);
            sections_lens  = (int[])v.elementAt(i++);
            offset = v.elementAt(i++).hashCode();
            sectionId = v.elementAt(i++).hashCode();
            filesize = v.elementAt(i++).hashCode();
            encoding = v.elementAt(i++).hashCode();
            rmsId = v.elementAt(i++).hashCode();
        }catch (Exception e){e.printStackTrace();}
    }
    private Vector toVector(){
        Vector v = new Vector(16);
        v.addElement(path);
        v.addElement(cover);
        v.addElement(new Boolean(html));
        v.addElement(new Boolean(fb2));
        v.addElement(new Boolean(encoding_defined));
        v.addElement(new Boolean(from_lib));
        v.addElement(notes);
        v.addElement(binaries);
        v.addElement(table);
        v.addElement(bookmarks);
        v.addElement(sections_offsets);
        v.addElement(sections_lens);
        v.addElement(new Integer(offset));
        v.addElement(new Integer(sectionId));
        v.addElement(new Integer(filesize));
        v.addElement(new Integer(encoding));
        v.addElement(new Integer(rmsId));
        return v;
    }
    //
    public int[] getBookmarks(){return bookmarks;}
    public void setBookmarks(int[] bookmarks){this.bookmarks = bookmarks;}
    //
    public String getPath(){return path;}
    public int getSectionOfset(){ return sections_offsets[sectionId]; }
    public int getSectionLen(){ return sections_lens[sectionId]; }

    public int getSectionId(){return sectionId;}
    public int getOffset(){return offset;}
    public int getNextOffset(){return nextoffset;}
    public int getPageSize(){return nextoffset - offset;}

    public boolean isFirstPage(){return 0==offset && 0==sectionId;}
    public int getFilesize(){return filesize;}
    public int getEncoding(){return encoding;}
    public int getRmsId(){return rmsId;}
    public boolean isEmpty(){return null==sections_lens || sections_lens.length==0;}
    //
    public void setPath(String newpath){ path = newpath;}
    public void setSectionId(int id){sectionId = id;}
    public void setOffset(int newoffset){ offset = newoffset; }
    public void setNextOffset(int newoffset){ nextoffset = newoffset; }
    public void setRmsId(int id){rmsId = id;}
    public void put(Object key, Object value){
        if (null==table)
            table = new Hashtable();
        table.put(key, value);
    }
    public Object get(Object key){ return null==table ? null : table.get(key); }
    public String gets(Object key){ return (String)get(key);}

    public boolean isArchive(){
        return path.endsWith(".gz") || path.endsWith(".fbz") || isEpub();
    }
    public boolean isEpub(){
        return  path.endsWith(".epub");
    }
    public void addSection (int ofset, int len){
        if (null==sections_lens_temp){
            sections_lens_temp = new Vector();
            sections_offsets_temp = new Vector();
        }
        System.out.println(" addSection "+ofset + " " +len);
        sections_offsets_temp.addElement(new Integer(ofset));
        sections_lens_temp.addElement(new Integer(len));
    }
    public void addSection (int len){
        if (null==sections_lens_temp){
            sections_lens_temp = new Vector();
        }
        System.out.println(" addSection "+len);
        sections_lens_temp.addElement(new Integer(len));
    }
    public void closeLink(){
        if (null!=sections_offsets_temp){
            int sz = sections_offsets_temp.size();
            sections_offsets = new int[sz];
            for (int i=0; i<sz; i++)
                sections_offsets[i] = sections_offsets_temp.elementAt(i).hashCode();
            sections_offsets_temp = null;
        }
        if (null!=sections_lens_temp){
            int sz = sections_lens_temp.size();
            sections_lens = new int[sz];
            for (int i=0; i<sz; i++)
                sections_lens[i] = sections_lens_temp.elementAt(i).hashCode();
            sections_lens_temp = null;
        }
    }
    public int getTextsize(){
        int sum = 0;
        for (int i=1; i<sections_lens.length; i++)
            sum+=sections_lens[i];
        return sum;
    }
    public void addNote (String id, String title){
        notes.addElement(id);
        notes.addElement(title);
    }
    public void addBinary (String id, int ofset, int len){
        binaries.addElement(new Object[]{id, new Integer(ofset), new Integer(len)});
    }
    public Vector getNotes(){return notes;}
    public Vector getBinaries(){return binaries;}
    public String getNote(String id){
        for (int i=0; i<notes.size(); i+=2)
            if (notes.elementAt(i).equals(id))
                return (String)notes.elementAt(i+1);
        return null;
    }
    public Object[] getBinary(String id){
        for (int i=0; i<binaries.size(); i++){
            Object[] obj = (Object[])binaries.elementAt(i);
            if (obj[0].equals(id))
                return obj;
        }
        return null;
    }
    public void setFilesize(int filesize){this.filesize = filesize;}
    public void setEncoding(int enc){this.encoding = enc;}
    //
    public boolean nextPortion(){
        if (sectionId<sections_lens.length-1){
            ++sectionId;
            return true;
        }
        return false;
    }
    public boolean prevPortion(){
        if (0==sectionId)
            return false;
        --sectionId;
        return true;
    }
//
    public void checkEncoding(String s){  //plain = !html && !fb2;
        if (encoding!= Encoder.ENC_UNKNOWN)
            return;
        encoding_defined = true;
        html = -1!=s.indexOf("<HTML");
        fb2 = -1!=s.indexOf("<FICTIONBOOK");

        if (-1!=s.indexOf("WINDOWS-125")||-1!=s.indexOf("WINDOWS125")||-1!=s.indexOf("WIN125")||-1!=s.indexOf("CP125")){
            if (-1!=s.indexOf("1250"))
                encoding = Encoder.ENC_1250;
            else if (-1!=s.indexOf("1251"))
                encoding = Encoder.ENC_1251;
            else if (-1!=s.indexOf("1257"))
                encoding = Encoder.ENC_1257;
        } else if (-1!=s.indexOf("KOI8-R"))
            encoding = Encoder.ENC_KOI8;
        else if (-1!=s.indexOf("ISO-8859-1"))
            encoding = Encoder.ENC_ISO8859_1;
        else if (-1!=s.indexOf("ISO-8859-2"))
            encoding = Encoder.ENC_ISO8859_2;
        else if (-1!=s.indexOf("UTF-8"))
            encoding = Encoder.ENC_UTF8;
        else {
            encoding = App.getInt(Res.tDEF_ENCODING, Encoder.ENC_UTF8);
            encoding_defined = false;
        }
    }
    public FileProc getFileProcessing(){
        if (fb2)
            return new FileFB2();
        else if (html)
            return new FileHTML();
        else
            return new FileTXT();
    }
    public void saveToRms(){
        String rmsName = RMS_PREFIX.concat(Integer.toString(rmsId));
        RMS.setBytesToRms(rmsName, Tools.obj2bytes(toVector()));
    }
    public boolean equals(Object obj){
        if (obj instanceof FileLink){
            FileLink fl = (FileLink)obj;
            if (path.equals(fl.path))
                return true;
        }
        return false;
    }
}