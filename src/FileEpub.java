import net.sf.jazzlib.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 07.01.15
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public class FileEpub {
    public final static Integer PATH = new Integer(0);
    public final static Integer LIST = new Integer(1);
// FileLink - Вычислять общую длину по sections, хранить кроме оффсета в текущей секции, еще и размер ОБРАБОТАННОЙ секции
// при показе FilePreload - вычислять % прочитанного. Аппроксимируя абсолютный offset.

    public ByteVector getData(FileLink link, FileBrowser fb){
        try{
            if (false==link.isEmpty() || process(link, fb)){
                ZipFile zf = new ZipFile("file://localhost/".concat(link.getPath()));
                int sectionId = link.getSectionId();
                Vector filenames = (Vector)link.get(LIST);
                String filename = (String)filenames.elementAt(sectionId);
                String path = link.gets(PATH);
                ZipEntry ze = zf.getEntry(path.concat(filename));
                InputStream zis = zf.getInputStream(ze);
                ByteVector bv = new ByteVector(zis, link.getSectionLen()); // todo: check loading on slow phones
                try{zis.close();}catch (IOException ioe){ioe.printStackTrace();}
                return bv;
            }
        }catch (Exception exc){fb.error_msg = exc.getMessage(); exc.printStackTrace();}
        return null;
    }
    private boolean process(FileLink link, FileBrowser fb){
        try{
            ZipFile zf = new ZipFile("file://localhost/".concat(link.getPath()));
            ZipEntry ze = zf.getEntry("META-INF/container.xml");
            if(null==ze)
                throw new Exception("Missing manifest in epub file");
            InputStream in = zf.getInputStream(ze);
            MyXml myx = MyXml.parse(in);
            if (null==myx)
                throw new Exception("Incorrect manifest");
            myx = myx.findByType("rootfile");
            if (null==myx)
                throw new Exception("No rootfile in manifest");

            String path = myx.gets("full-path"); // opf file path
            String dir = "";
            int slash = path.lastIndexOf('/');
            if (-1!=slash)
                dir = path.substring(0, slash+1);
            link.put(PATH, dir);

            ze = zf.getEntry(path);
            in = zf.getInputStream(ze);
            myx = MyXml.parse(in);
            if (null==myx)
                throw new Exception("Error with opf file");
            MyXml manifest = myx.findByType("manifest");
            if (null==manifest)
                throw new Exception("No manifest in opf file");
            Vector items = manifest.getChildren();
            if (items.isEmpty())
                throw new Exception("Empty manifest in opf file");
            Hashtable manifest_items = new Hashtable();
            for (int i=0; i<items.size(); i++){
                MyXml item = (MyXml)items.elementAt(i);
                String id = item.gets("id");
                String href = item.gets("href");
                if (null!=id && null!=href)
                    manifest_items.put(id, href);
            }
//
            MyXml spine = myx.findByType("spine");
            if (null==spine)
                throw new Exception("No spine in opf file");
            items = spine.getChildren();
            if (items.isEmpty())
                throw new Exception("Empty spine in opf file");
            Vector list = new Vector();
            for (int i=0; i<items.size(); i++){
                MyXml item = (MyXml)items.elementAt(i);
                String idref = item.gets("idref");
                if (null!=idref){
                    String fname = (String)manifest_items.get(idref);
                    if (null!=fname){
                        ze = zf.getEntry(dir.concat(fname));
                        link.addSection((int)ze.getSize());
                        list.addElement(fname);
                    }
                }
            }
            link.put(LIST, list);
            link.closeLink();
            return true;
        }catch (Exception exc){fb.error_msg = exc.getMessage(); exc.printStackTrace();}
        return false;
    }
}
