import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 12.11.14
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 * <image l:href="#i_001.png"/>
 * <a l:href="#n_1" type="remark">1</a>
 */
public class FileFB2 extends FileProc{
    protected InputStream is;
    protected byte[] buffer;
    protected int ofset, len;
    //
    protected int moveAndLoad(int rest){
        //arraycopy(Object src, int src_position, Object dst, int dst_position, int length)
        System.arraycopy(buffer, buffer.length-rest, buffer, 0, rest);
        ofset+=(buffer.length-rest);
        try{
            return Res.readTotal(is, buffer, rest, buffer.length - rest);
        }catch (Exception exc){exc.printStackTrace();}
        return 0;
    }
    protected int findBytes(byte[] bytes, int i){// possible speed-up
        byte first = bytes[0];
        int sz = bytes.length;
        try{
            while (true){
                while (buffer[i]!=first)
                    ++i;
                int j = 1;
                while (j<sz && buffer[i+j]==bytes[j])
                    ++j;
                if(j==sz)
                    return i;
                ++i;
            }
        }catch (ArrayIndexOutOfBoundsException aiob){ aiob.printStackTrace();}
        return -1;
    }
    protected int findStringHere(String s, int start){
        byte[] tmp = s.getBytes();
        return findBytes(tmp, start);
    }
    String[] delims;
    protected int findEdge(int begin, int len){
        for (int i=0; i<delims.length; i++){
            int end = findStringHere(delims[i], begin);
            if (-1!=end && end < (begin + len))
                return end;
        }
        return 0;
    }
    protected int findString(String s, int start){
        byte[] tmp = s.getBytes();

        while (true){
            int local = findBytes(tmp, start);
            if (-1==local){
                if (0==moveAndLoad(tmp.length-1))
                    return -1;
                start = 0;
            }else{
                int ret = ofset + local;
                int rest = buffer.length-local;
                if (rest < 1000)
                    moveAndLoad(rest);
                return ret;
            }
        }
    }
    protected int findLocalString(String s, int start){ return findLocalString(s, start, 128);}
    protected int findLocalString(String s, int start, int DISTANCE){
        byte[] tmp = s.getBytes();
        int end = start + DISTANCE;
        for (int i=start; i<end; i++)
            if (tmp[0]==buffer[i]){
                int j=1;
                while (j<tmp.length && tmp[j]==buffer[i+j])
                    ++j;
                if (j==tmp.length)
                    return i;
            }
        return -1;
    }
    protected String findAttr(int begin, String attr_name){// id=""
        int len = attr_name.length()+2;
        StringBuffer sb = new StringBuffer(len);
        sb.append(attr_name).append('=').append('"');
        begin = findLocalString(sb.toString(), begin);
        if (-1!=begin){
            begin+=len;
            len = begin;
            while (buffer[len]!='"')
                ++len;
            if ('#'==buffer[begin])
                ++begin;
            return new String(buffer, begin, len-begin);
        }
        return null;
    }
    protected int findByte(int i, int b){
        while (buffer[i]!=b)
            ++i;
        return i;
    }
    protected String makeString (int begin, int len, FileLink fl){
        ByteVector bv = new ByteVector(buffer, begin, len);
        TextPart txp = new TextPart(bv, fl);
        return txp.getString();
    }
/// FB2 CONSTANTS
    public final static String SECTION = "<section";
    public final static String SECTION_END = "</section";
    public final static String BINARY = "<binary";
    public final static String BINARY_END = "</binary>";
    public final static String NOTES = "<body name=\"notes\">";
    public final static String BODY_END = "</body>";
    public final static String BODY = "<body";
    public final static String BOOK_END = "</FictionBook>";
    public final static String COVERPAGE = "<coverpage>";
//
    public void process(byte[] buffer, InputStream is, FileLink link, int length){
        delims = new String[]{SECTION, "<empty-line/>", "<p>"};
        this.buffer = buffer;
        this.is  = is;
        this.len = length;
        int begin = 0, end = 0;
        try{
            begin = findLocalString(COVERPAGE, 0, 5000);//Ex: <coverpage><image l:href="#cover.jpg"/></coverpage>
            if (-1!=begin){
                link.cover = findAttr(begin+COVERPAGE.length(), "href");
            }else
                begin = 0;
            begin = findStringHere(BODY, 0) + BODY.length();
            begin = findByte(begin + BODY.length(), '>')+1;

            len -= begin;
            end = findStringHere(BODY_END, begin);
            if (-1==end){
                while(true){
                    int ofset_tmp = ofset;
                    if (len < App.LIM){
                        moveAndLoad(len);
                        len = findStringHere(BODY_END, len - BODY_END.length()+1);
                        begin =0;
                        if (-1!=len){
                            int nextbody = findStringHere(BODY, len);
                            if (-1!=nextbody){
                                String name = findAttr(nextbody, "name");
                                if ("notes".equals(name))
                                    break;
                            }else
                                break;
                        }
                        ofset_tmp = ofset;
                        len = buffer.length;
                    }
                    end = findEdge(begin + (App.LIM*8/10), App.LIM*2/10);// zx
                    link.addSection(begin + ofset_tmp, (end - begin));
                    len -= (end - begin);
                    begin = end;
                }
            }else {// short text
                len = end - begin;
            }
            while (true){
                if (len < App.LIM){
                    link.addSection(begin + ofset, len);
                    break;
                }
                end = findEdge(begin + (App.LIM*8/10), App.LIM*2/10);
                link.addSection(begin + ofset, (end - begin));
                len -= (end - begin);
                begin = end;
            }
            begin +=(len+BODY_END.length());
            int notes = findLocalString(NOTES, begin);
            if (-1!=notes){
                begin +=NOTES.length();
// Ex: <section id="n_1"><title><p>1</p></title><p>Работа выполнена совместно с Л. В. Яблоковой.</p></section>
                while(true){// поиск секций примечания
                    begin = findString(SECTION, begin);
                    String id = findAttr(begin-ofset, "id");// " id=""
                    end = findString(SECTION_END, begin - ofset + SECTION.length());
                    String title = makeString(begin-ofset, end - begin, link).trim();
                    if (null!=title){
                        link.addNote(id, title);
                        Debug.log(id + " " + title);
                    }
                    begin = end-ofset+ SECTION_END.length();
                    end = findLocalString(BODY_END, begin);
                    if (-1!=end){
                        begin = end+BODY_END.length();
                        break;
                    }
                }
            }
            begin = findString(BINARY, begin);
            //begin = findLocalString(BINARY, begin, 2048);
            if (-1!=begin){
                begin -= ofset;
                while (true){// поиск binary
                    begin = findString(BINARY, begin);
                    String id = findAttr(begin-ofset, "id");
                    begin = findByte(begin-ofset, '>')+1+ofset;

                    end = findString(BINARY_END, begin-ofset);
                    link.addBinary(id, begin, end-begin);
                    Debug.log(id +" "+(end-begin));
                    //
                    begin = end-ofset + BINARY_END.length();
                    end = findLocalString(BOOK_END, begin);
                    if (-1!=end){
                        break;
                    }
                }
            }
        }catch (Exception exc){exc.printStackTrace();}
        link.closeLink();
        is = null; buffer = null;
    }
}