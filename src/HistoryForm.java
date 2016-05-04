import javax.microedition.lcdui.*;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 29.01.14
 * Time: 9:21
 * To change this template use File | Settings | File Templates.
 */
public class HistoryForm extends List implements CommandListener, Runnable, ItemCommandListener{
    static LinkManager lm = new LinkManager(Res.tHISTORY, FileLink.RMS_PREFIX);

    public HistoryForm () {
        super(Res.getStringLower(Res.tHISTORY), List.IMPLICIT);
        addCommand(App.back);
        setCommandListener(this);
        updateForm();
    }
    public void updateForm(){
        deleteAll();
        removeCommand(App.clear);
        Vector paths = lm.getCaptions();
        if (paths.isEmpty())
            append(Res.getString(Res.tHISTORY_EMPTY), null);
        else{
            addCommand(App.clear);
            Image icon = Res.loadImage("about.png");
            for (int i=0; i<paths.size(); i++){
                String label = Tools.getHeader((String)paths.elementAt(i));
                append(label, icon);
            }
        }
    }
    public static int getHistorySize(){ return lm.getCaptions().size(); }
    public static String getLastFileName(){
        Vector paths = lm.getCaptions();
        if (paths.isEmpty())
            return "null";
        return Tools.getHeader((String)paths.firstElement());
    }
    Alert alert;
    public void initAlert(Command command, int messageid){
        if (null==command)
            command = App.ok;
        String message = Res.getString(messageid);

        alert = new Alert(Res.getString(Res.tINFORMATION));
        alert.setString(message);
        alert.setCommandListener(this);
        alert.addCommand(command);
        if (command!=App.ok)
            alert.addCommand(App.cancel);
        if (501==App.platform_id)
            alert.addCommand(App.back);
        App.show(alert);
    }
    FileLink filelink;
    Command run_cmd;
    public void run(){
        FileBrowser fb = FileBrowser.getFileBrowser();
        if (run_cmd==App.deletewithfiles){
            FileBrowser.getFileBrowser().deleteFile(filelink.getPath());
        }else if (run_cmd==App.read){
            if (false==fb.showFile(filelink, -1)){
                if (fb.file_not_exists){
                    lm.updateLink(filelink.getPath(), true);
                    updateForm();
                    initAlert(null, Res.tFILENOTEXIST);
                }else
                    initAlert(null, Res.tFILE_ERROR);
            }
        }
    }
    public void showForm(){
        String path = filelink.getPath();
        Form form = new Form(Tools.getHeader(path));
        form.append(path);
        String s = Res.getString(Res.tFILESIZE) + " " + Tools.shortNumber(filelink.getTextsize());
        form.append(s);
        s = "% " + Res.getString(Res.tREADED);
        form.append(s);
        //
        StringItem si = null;
        Item focus = null;

        si = new StringItem(null, Res.getStringUpper(Res.tREAD), Item.BUTTON);
        si.setDefaultCommand(App.read);
        si.setItemCommandListener(this);
        form.append(si);
        focus = si;
        if (false==App.istouch)
            form.addCommand(App.read);
        si = new StringItem(null, Res.getStringUpper(Res.tDELETE), Item.BUTTON);
        si.setDefaultCommand(App.delete);
        si.setItemCommandListener(this);
        form.append(si);
        si = new StringItem(null, Res.getStringUpper(Res.tDEL_FILES), Item.BUTTON);
        si.setDefaultCommand(App.deletewithfiles);
        si.setItemCommandListener(this);
        form.append(si);
/// pack & share
        if (false==filelink.isArchive()){
            si = new StringItem(null, Res.getStringUpper(Res.tPACK), Item.BUTTON);
            si.setDefaultCommand(App.pack);
            si.setItemCommandListener(this);
            form.append(si);
        }
        if (false==filelink.from_lib){
            si = new StringItem(null, Res.getStringUpper(Res.tSHARE), Item.BUTTON);
            si.setDefaultCommand(App.share);
            si.setItemCommandListener(this);
            form.append(si);
        }
//
        form.addCommand(App.back);
        form.setCommandListener(this);
        App.show(form);
        if (null!=focus)
            App.setFocus(focus);
    }
    private void read(){
        run_cmd = App.read;
        new Thread(this).start();
    }
    public void commandAction(Command c, Item item){
        if (App.delete == c){
            initAlert(App.delete, Res.tDELETE_FROM_HISTORY);
        }else if (App.deletewithfiles == c){
            initAlert(App.deletewithfiles, Res.tDELETE_FROM_HISTORY_AND_FILE);
        }else if (App.read == c){
            read();
        }else if (App.pack.equals(c)){
            new PackForm(this, filelink);
        }else  if (App.share.equals(c)){
            System.out.println("share");
        }
    }
    public void commandAction(Command c, Displayable d){
        if (false==d.equals(this)){
            if (App.read == c){
                read();
                return;
            }else if (App.clear.equals(c)){
                lm.removeAll();
            }else  if (App.delete==c || App.deletewithfiles==c){
                lm.updateLink(filelink.getPath(), true);
                if (App.deletewithfiles==c){
                    run_cmd = App.deletewithfiles;
                    new Thread(this).start();
                }
            }
            updateForm();
            App.show(this);
            return;
        }
        if (App.back==c){
            App.show(MainMenu.getInstance());
            return;
        }
        if (App.clear == c) {
            initAlert(App.clear, Res.tCLEARALERT);
            return;
        }
        if (lm.getCaptions().isEmpty()){
            App.show(FileBrowser.getInstance());
            return;
        }
        int selected = this.getSelectedIndex();
        Vector v = (Vector)lm.getObject(selected);
        filelink = new FileLink(v);
        showForm();
    }
}