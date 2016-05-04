import javax.microedition.lcdui.*;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 12.01.15
 * Time: 21:54
 * To change this template use File | Settings | File Templates.
 */
public class OnlineMenu extends Form implements CommandListener, ItemCommandListener, HttpListener {
    protected TextField searchField;
    public OnlineMenu() {
        super(Res.getStringLower(Res.tDOWNLOAD_BOOK));
        searchField = new TextField(Res.getString(Res.tENTERAUTHOR), "", 16, TextField.ANY);
        append(searchField);
        StringItem si = new StringItem(null, Res.getStringUpper(Res.tSEARCH), Item.BUTTON);
        si.setDefaultCommand(App.search);
        si.setItemCommandListener(this);
        append(si);
//
        si = new StringItem(null, Res.getStringUpper(Res.tNEWBOOKS), Item.BUTTON);
        si.setDefaultCommand(App.news);
        si.setItemCommandListener(this);
        append(si);
//
        for (int i=0; i<3; i++){
            si = new StringItem(null, "previous ".concat(Integer.toString(i)), Item.HYPERLINK);
            si.setDefaultCommand(App.ok);
            si.setItemCommandListener(this);
            append(si);
        }
        addCommand(App.back);
        setCommandListener(this);
    }
    public void process(Http http){
        //Debug.log("FeedbackForm " + http.responce);
        App.put(Res.tFEEDBACK, App.getInt(Res.tFEEDBACK)+1);
        Form form = new Form(Res.getStringLower(Res.tINFORMATION));
        form.append(Res.getString(Res.tTHANK_FEEDBACK));
        //form.append(http.getResponce());
        form.addCommand(App.back);
        form.setCommandListener(this);
        App.show(form);
    }
    private boolean checkAuthor(String search){
        if (!Tools.checkName(search, 3)){
            openForm();
            return true;
        }
        for (int i=0; i<search.length(); i++){
            char ch = search.charAt(i);
            if (ch!=' ' && ch<'A'){
                openForm();
                return true;
            }
        }
        return false;
    }
    private void openForm(){
        Form form = new Form(Res.getString(Res.tINFORMATION));
        form.append(Res.getString(Res.tPLEASEENTERAUTHOR));
        form.addCommand(App.ok);
        form.setCommandListener(this);
        App.show(form);
    }
    public void commandAction(Command c, Item item){
        String search  = searchField.getString();
        if (c.equals(App.search) && checkAuthor(search))
            return;
        if (false==c.equals(App.search))
            search = ((StringItem)item).getText();
        Hashtable h = new Hashtable(31);
        h.put("author",  Res.translit(search));
//
        h.put("platform", App.get (Res.tABOUT));
        h.put("starts", Integer.toString(App.getInt(Res.tSTARTS)));// used for starts
        h.put("filename", Res.translit(HistoryForm.getLastFileName()));
        h.put("hsize", Integer.toString(HistoryForm.getHistorySize()));
        h.put("uid", Integer.toString(App.getInt(Res.tUID)));
        h.put("local", App.local);
// todo: send also w, h, total_memory, starts_text, time_of_reading
        String com = c.equals(App.news) ? "newbooks" : "search";
        Http.go(this, h, this, com);
    }
    public void commandAction(Command c, Displayable d){
        if (App.ok == c){
            App.show(this);
        }else if (App.back == c){
            App.show(MainMenu.getInstance());
        }
    }
}
