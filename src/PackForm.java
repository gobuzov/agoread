import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.*;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 11.03.15
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class PackForm implements Runnable, CommandListener {
    HistoryForm historyForm;
    FileLink link;
    boolean running, canceled = false;
    int filesize, loaded = 0, newsize;

    public PackForm(HistoryForm prev, FileLink link){
        this.historyForm = prev;
        this.link = link;
        ProgressCanvas pc = new ProgressCanvas(Res.getString(Res.tPACKING), false);
        pc.addCommand(App.cancel);
        pc.setCommandListener(this);
        App.show(pc);
        new Thread(this).start();
    }
    public void run() {
        FileConnection fcin = null, fcout = null;
        InputStream fis = null;
        OutputStream fos = null;
        net.sf.jazzlib.GZIPOutputStream gzos = null;
        String path = link.getPath(), newpath = path.concat(".gz");

        try {// todo: проверить - можно ли обойтись одним fileconnection? Ответ - нельзя, не будет работать CANCEL
            fcin =(FileConnection) Connector.open("file://localhost/".concat(path), Connector.READ_WRITE);
            filesize = link.getFilesize();
            fis = fcin.openInputStream();
            fcout =(FileConnection) Connector.open("file://localhost/".concat(newpath), Connector.READ_WRITE);
            fcout.create();// todo проверить на exist, на доступное место
            fos = fcout.openOutputStream();
            byte[] buff = new byte[32768];
            gzos = new net.sf.jazzlib.GZIPOutputStream(fos);
            running = true;

            while (running && false==canceled) {
                int readed = fis.read(buff);
                if (-1!=readed){
                    gzos.write(buff, 0, readed);
                    loaded+=readed;
                    App.setProgress(loaded * 100 / filesize);
                }else{
                    running = false;
                }
            }
        } catch (Exception x) { x.printStackTrace(); }
        finally {
            try{
                if (null!=gzos){
                    gzos.finish(); gzos.close();
                }
                if (null!=fos)
                    fos.close();
                if (null!=fis)
                    fis.close();
                if (null!=fcin){
                    if (false==canceled)
                        fcin.delete();
                    fcin.close();
                }
                if (null!=fcout){
                    newsize = (int)fcout.fileSize();
                }
            }catch (Exception exc){exc.printStackTrace();}
        }
        try{
            if (canceled){
                 if (null!=fcout)
                     fcout.delete();
            }else{
                link.setFilesize(newsize);
                link.setPath(newpath);
                link.saveToRms();
                historyForm.lm.updatePath(path, newpath);
                historyForm.updateForm();
            }
            fcout.close();
        }catch (Exception exc){exc.printStackTrace();}
        App.show(historyForm);
    }
    public void commandAction(Command c, Displayable d){
        if (App.cancel==c)
            canceled = true;
    }
}
