import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipFile;

import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;
import java.util.*;
import java.io.*;
//
public class FileBrowser extends List implements CommandListener, Runnable{
    public static final String[] extensions = {"txt", "html", "htm", "fb2", "gz", "fbz", "epub"};
    private static final String UP_DIRECTORY = "..";
    private static final String MEGA_ROOT = "/";
    public static final String SEP_STR = "/";
    private String currDirName;
    private Image dirIcon = Res.loadImage("folder.png");
    private Image dirupIcon = Res.loadImage("arrow_up.png");
    private Image fileIcon = Res.loadImage("about.png");
    private static FileBrowser instance;
    private static boolean showhint = true;
    private static boolean firsttime = true;
    private Alert alert;
    public String error_msg;
    public boolean file_not_exists;
//
    private FileBrowser() {
        super("", List.IMPLICIT);
        currDirName = App.get(Res.tCHOOSE_BOOK, MEGA_ROOT);
        addCommand(App.back);
        setCommandListener(this);
    }
    public static Displayable getInstance(){
        getFileBrowser();
        if (App.getInt(Res.tSTARTS)<3 && showhint){// used for starts
            showhint = false;
            return initHint();
        }
        if (firsttime){
            firsttime = false;
            new Thread(instance).start();
        }
        return instance;
    }
    public static FileBrowser getFileBrowser(){
        if (null==instance)
            instance = new FileBrowser();
        return instance;
    }
    private static Displayable initHint(){
        Form form = new Form(Res.getStringLower(Res.tINFORMATION));
        form.append(Res.getString(Res.tPLEASE_SELECT));
        form.addCommand(App.ok);
        form.addCommand(App.back);
        form.setCommandListener(instance);
        return form;
    }
   String currFile = null;
    public void run(){
        if (null==currFile){
            showCurrDir();
        }else {
            if (currFile.endsWith(SEP_STR) || currFile.equals(UP_DIRECTORY)) {
                traverseDirectory(currFile);
            } else {
                LinkManager lm = new LinkManager(Res.tHISTORY, FileLink.RMS_PREFIX);
                String path = currDirName.concat(currFile);
                Vector v = (Vector) lm.getObjectByName(path);
                FileLink link = v!=null ? new FileLink(v) : new FileLink(path);

                if (false== showFile(link, -1))
                    showAlert();
                else if (-1==link.getRmsId()){
                    int rmsId = lm.addLink(path);
                    link.setRmsId(rmsId);
                    link.saveToRms();
                }
                currFile = null;
            }
        }
    }
    private void showAlert(){
        if (null==alert){
            alert = new Alert(Res.getString(Res.tERROR));
            alert.setCommandListener(this);
            alert.addCommand(App.ok);
            if (501==App.platform_id)
                alert.addCommand(App.back);
        }
        alert.setString(Res.getString(Res.tFILE_ERROR) + currDirName + currFile+"\n"+error_msg);
        App.show(alert);
    }
    private boolean isSupportedFile(String s){
        s = s.toLowerCase();
        for (int i=extensions.length-1; i>=0; i--)
            if (s.endsWith("."+extensions[i]))
                return true;
        return false;
    }
    /// show whole file OR part (if file too big)
    /// ofset: -1:still old, 0-first page, X: len-X (last page)
    public boolean showFile(FileLink link, int ofset){
        App.showProgress(Res.getString(Res.tLOADING), false);
        ByteVector bv = getTextData(link);       // todo: optimize
        if (null!=bv){
            TextPart txp = new TextPart(bv, link);
            if (-1!=ofset){
                link.setOffset(0==ofset ? 0 : txp.getLen()-(ofset*9)/10);
            }
            TextView tv = new TextView(txp, link);
            MyCanvas mc = MyCanvas.getInstance();
            mc.setView(tv);
            App.show(mc);
//
            LinkManager lm = new LinkManager(Res.tHISTORY, FileLink.RMS_PREFIX);
            lm.updateLink(link.getPath(), false);
            return true;
        }
        return false;
    }
    public static Vector getDirsInfo(){
        Vector vct = new Vector(3);
        try{
            Enumeration e = FileSystemRegistry.listRoots();
            while (e.hasMoreElements()) {
                String s = (String)e.nextElement();
                FileConnection fc = (FileConnection)Connector.open("file://localhost/" + s, Connector.READ);
                String path = fc.getPath();
                if ('/'==path.charAt(0))
                    path = path.substring(1);
                if ('/'==path.charAt(path.length()-1))
                    path = path.substring(0, path.length()-1);
                if (-1==path.toLowerCase().indexOf("private")){
                    vct.addElement(path);
                    vct.addElement(new Long(fc.totalSize()));
                    vct.addElement(new Long(fc.availableSize()));
                }
            }
        }catch(Exception ioe){ioe.printStackTrace();}
        return vct;
    }
    /*private static boolean a(String s1, byte abyte0[])
    {
        try
        {
            s1 = (FileConnection)Connector.open("file:///".concat(String.valueOf(String.valueOf(s1))), 2);
            if(abyte0 == null)
            {
                s1.mkdir();
            } else
            {
                s1.create();
                DataOutputStream dataoutputstream;
                (dataoutputstream = s1.openDataOutputStream()).write(abyte0);
                dataoutputstream.close();
                s1.close();
            }
            return true;
        }
        catch(Exception _ex)
        {
            return false;
        }
    }*/

    void showCurrDir() {
        setTitle(Res.getStringLower(currDirName));
        App.put(Res.tCHOOSE_BOOK, currDirName);
        Enumeration e;
        FileConnection currDir = null;
        try {
            if (MEGA_ROOT.equals(currDirName)) {
                e = FileSystemRegistry.listRoots();
                deleteAll();
            } else {
                currDir = (FileConnection)Connector.open("file://localhost/" + currDirName, Connector.READ);
                e = currDir.list();
                deleteAll();
                append(UP_DIRECTORY, dirupIcon); // not root - draw UP_DIRECTORY
            }
            Vector folders = new Vector();
            Vector files = new Vector();
            while (e.hasMoreElements()) {
                String s = (String)e.nextElement();
                if (s.endsWith("/"))
                    folders.addElement(s);
                else{
                    if (isSupportedFile(s))
                        files.addElement(s);
                }
            }
            boolean please_wait = (folders.size()+files.size())>15;
            if (please_wait){
                Form form = new Form(Res.getStringLower(Res.tINFORMATION));
                form.append(Res.getString(Res.tPLEASE_WAIT));
                App.show(form);
            }
            QSort.qsort(folders);
            QSort.qsort(files);
            int folds = folders.size();
            for (int i=0; i<files.size(); i++)
                folders.addElement(files.elementAt(i));
            for (int i=0; i<folders.size(); i++)
                append((String)folders.elementAt(i), i < folds ? dirIcon : fileIcon);
            if (currDir != null)
                currDir.close();
            if (please_wait)
                App.show(this);
        } catch (Exception exc){
            Form form = new Form(Res.getStringLower(Res.tERROR));
            form.append(Res.getString(Res.tALLOW_ACCESS));
            form.addCommand(App.exit);
            form.setCommandListener(instance);
            App.show(form);
        }
    }
    void traverseDirectory(String fileName) {
        if (currDirName.equals(MEGA_ROOT)) {
            if (fileName.equals(UP_DIRECTORY)) {
                return;
            }
            currDirName = fileName;
        } else if (fileName.equals(UP_DIRECTORY)) {
            int i = currDirName.lastIndexOf('/', currDirName.length() - 2);
            currDirName = (-1==i) ? MEGA_ROOT : currDirName.substring(0, i + 1);
        } else {
            currDirName = currDirName + fileName;
        }
        showCurrDir();
    }
    private InputStream openInputStream(FileConnection fc) throws IOException{
            InputStream is = fc.openInputStream();
            String fileName = fc.getName().toLowerCase();
            return (fileName.endsWith(".gz") || fileName.endsWith(".fbz")) ?
                    new net.sf.jazzlib.GZIPInputStream(is) :
                    is;
    }
    private ByteVector getTextData(FileLink link){/// first time, file will splitted for view parts (which will good for memory)
        file_not_exists = false;

        if (link.isEpub()){
            FileEpub fe = new FileEpub();
            return fe.getData(link, this);
        }
        String path = link.getPath();

        FileConnection fc = null;
        boolean err = true;
        try{
            fc =(FileConnection)Connector.open("file://localhost/".concat(path), Connector.READ);
            if (!fc.exists()) {
                error_msg = "File does not exists";
                file_not_exists = true;
            }else
                err = false;
        }catch (Exception exc){
            error_msg = exc.getMessage();
        }
        if (err)
            return null;
        try{
            InputStream fis = openInputStream(fc);
            int fsize = (int)fc.fileSize();
            Debug.log("filesize:"+fsize);
            link.setFilesize(fsize);
//
            if (link.isEmpty()){
                FilePreload fp = new FilePreload(fis, link);
// open again
                fis = openInputStream(fc);
            }
            int ofset = link.getSectionOfset();
            int len = link.getSectionLen();
            if (0!=ofset){
                Debug.startTime();
                fis.skip(ofset);
                Debug.logTime("Skipped:"+ofset+" ");
            }
            ByteVector bv = new ByteVector(fis, len); // todo: check loading on slow phones

            fis.close();
            fc.close();
            return bv;
            //Debug.logTime("File loaded: "+path +" size="+res.length+ " time: ");
            //Debug.logMem("Memory: ");
        }catch (Exception exc){error_msg = exc.getMessage(); exc.printStackTrace();}
        return null;
    }
    public Image getFB2Image(String imgName, FileLink link){
        String path = link.getPath();
        FileConnection fc = null;
        boolean err = true;
        file_not_exists = false;
        try{
            fc =(FileConnection)Connector.open("file://localhost/".concat(path), Connector.READ);
            err = false;
        }catch (Exception exc){
            error_msg = exc.getMessage();
        }
        if (err)
            return null;
        try{
            InputStream fis = openInputStream(fc);
            Object[] binaryData = link.getBinary(imgName);
            int ofset = binaryData[1].hashCode();
            int len = binaryData[2].hashCode();
            fis.skip(ofset);

            byte[] res = new byte[len];// check loading on slow phones
            //System.out.println("picture size64 : "+len);

            Res.readTotal(fis, res, 0, res.length);
            fis.close();
            fc.close();
            fis = null;
            fc = null;
//
            len = Tools.decodeBytes64(res);
            Image img = Image.createImage(res, 0, len);
            System.out.println("width:"+img.getWidth()+" height:"+img.getHeight());
            return img;
        }catch (Exception exc){error_msg = exc.getMessage(); exc.printStackTrace();}
        return null;
    }
    public void deleteFile(String path){
        FileConnection fc = null;
        try{
            fc =(FileConnection)Connector.open("file://localhost/".concat(path), Connector.WRITE);
            fc.delete();
        }catch (Exception exc){
            exc.printStackTrace();
        }finally {
            try{
                fc.close();
            }catch (Exception exc){exc.printStackTrace();}
        }
    }
    public byte[] getBinaryFile(String path){
        FileConnection fc = null;
        boolean err = true;
        file_not_exists = false;
        try{
            fc =(FileConnection)Connector.open("file://localhost/".concat(path), Connector.READ);
            err = false;
        }catch (Exception exc){
            error_msg = exc.getMessage();
        }
        if (false==err){
            try{
                InputStream fis = fc.openInputStream();
                int sz = (int)fc.fileSize();
                byte[] res = new byte[sz];// check loading on slow phones
                fis.read(res);
                fis.close(); fc.close(); fis = null; fc = null;
                return res;
            }catch (Exception exc){error_msg = exc.getMessage(); exc.printStackTrace();}
        }
        return null;
    }
    public boolean checkFile(String path){
        FileConnection fc = null;
        boolean result = false;
        try{
            fc =(FileConnection)Connector.open("file://localhost/".concat(path), Connector.READ);
            result = fc.exists();
        }catch (Exception exc){}
        finally { try{if (null!=fc) fc.close();}catch(IOException ioe){ioe.printStackTrace();}}
        return result;
    }
    public void commandAction(Command c, Displayable d){
        //Debug.log("FileBrowser.commandAction");
       if (App.exit==c){
           try{App.instance.destroyApp(true);}catch (Exception xp){};
       }else if (App.ok == c){
           new Thread(this).start();
           App.show(this);
       }else if (App.back == c){
            App.show(MainMenu.getInstance());
        } else{
            currFile = getString(getSelectedIndex());
            new Thread(this).start();
       }
    }
}