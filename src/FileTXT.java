import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 17.11.14
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public class FileTXT extends FileFB2{
    public void process(byte[] buffer, InputStream is, FileLink link, int length){
        delims = new String[]{". ", "."};
        this.buffer = buffer;
        this.is  = is;
        this.len = length;

        int begin = 0;
        try{
            while (true){
                if (len < App.LIM){
                    link.addSection(begin + ofset, len);
                    break;
                }
                int end = findEdge(begin + (App.LIM*8/10), App.LIM*2/10);
                link.addSection(begin + ofset, (end - begin));
                len -= (end - begin);
                begin = end;
            }
        }catch (Exception exc){exc.printStackTrace();}
        link.closeLink();
        is = null; buffer = null;
    }
}
