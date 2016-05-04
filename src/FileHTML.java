import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 12.11.14
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class FileHTML extends FileFB2{
    String[] begins = {"<!----------- Собственно произведение --------------->", "<!--Section Begins-->", "</form>"};
    String[] ends = {"<hr noshade=\"noshade\">", "<hr size=1 noshade>"};
    //                                                //
    public void process(byte[] buffer, InputStream is, FileLink link, int length){
        delims = new String[]{"<p>","<div","<ul>","<dd>", ". ", "."};
        this.buffer = buffer;
        this.is  = is;
        this.len = length;

        int begin = 0, end = 0;
        try{
            for (int i=0; i<begins.length; i++){
                begin = findStringHere(begins[i], 0);
                if (-1!=begin){
                    System.out.println(begins[i]+" finded!!!");
                    break;
                }
            }
           // if (-1!=begin)
           //     len-=begin;
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
        }catch (Exception exc){exc.printStackTrace();}
        link.closeLink();
        is = null; buffer = null;
    }
}
