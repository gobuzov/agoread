import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 15.09.14
 * Time: 20:56
 * To change this template use File | Settings | File Templates.
   Loads file (or start part) to byte array, define encoding & type,
   then going to FileProc for define parts of file
 */
public class FilePreload{

    public FilePreload(InputStream is, FileLink link){
        System.gc();
        int memsize = (int)Runtime.getRuntime().freeMemory()*75/100;
        int filesize = link.getFilesize();
        if (link.isArchive())
            filesize<<=2;
        if (memsize > filesize)
            memsize = filesize;

        byte[] buffer = new byte[memsize];
        int len = 0;
        try{len = Res.readTotal(is, buffer, 0, buffer.length);}catch (IOException e){e.printStackTrace();}
/// define encoding && filetype
        link.checkEncoding(new String(buffer, 0, len < 1024 ? len : 1024).trim().toUpperCase());
        FileProc fp = link.getFileProcessing();
/// define parts of file, will save in FileLink
        fp.process(buffer, is, link, len);
        try{ is.close(); }catch (IOException ioe){ioe.printStackTrace();}
        buffer = null; is = null; System.gc();
        Debug.log("FilePreload good");
    }
 }