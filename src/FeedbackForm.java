import javax.microedition.lcdui.*;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 17.03.14
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */
public class FeedbackForm extends Form implements CommandListener, ItemCommandListener, HttpListener {
    protected TextField nameField;
    protected TextField emailField;
    protected TextField notesField;

    public FeedbackForm() {
        super(Res.getStringLower(Res.tFEEDBACK));
        String s = Res.getString(Res.tYOURNAME) +" ("+ Res.getString(Res.tOPTIONAL) + "):";
        nameField = new TextField(s, App.get(Res.tYOURNAME, ""), 16, TextField.ANY);
        s = Res.getString(Res.tYOUR_EMAIL) +" ("+ Res.getString(Res.tOPTIONAL) + "):";
        emailField = new TextField(s, App.get(Res.tYOUR_EMAIL, "@"), 40, TextField.EMAILADDR);
        notesField = new TextField(Res.getString(Res.tYOUR_NOTES), App.get(Res.tYOUR_NOTES, ""), 200, TextField.ANY);
        append(notesField);
        append(nameField);
        append(emailField);
        StringItem si = new StringItem(null, Res.getStringUpper(Res.tSUBMIT), Item.BUTTON);
        si.setDefaultCommand(new Command(Res.getStringUpper(Res.tSUBMIT), Command.OK, 1));
        si.setItemCommandListener(this);
        append(si);

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
    public void commandAction(Command c, Item item){
        String name  = nameField.getString().trim();
        String email = emailField.getString();
        String notes = notesField.getString().trim();
        App.put(Res.tYOURNAME, name);
        App.put(Res.tYOUR_EMAIL, email);
        App.put(Res.tYOUR_NOTES, notes);

        if (!Tools.checkName(notes, 4)){
            Form form = new Form(Res.getString(Res.tINFORMATION));
            form.append(Res.getString(Res.tPLEASE_ENTER));
            form.addCommand(App.ok);
            form.setCommandListener(this);
            App.show(form);
            return;
        }
        Hashtable h = new Hashtable(31);
        h.put("name",  Res.translit(name));
        h.put("email", email);
        h.put("notes", Res.translit(notes));
//
        h.put("platform", App.get (Res.tABOUT));
        h.put("starts", Integer.toString(App.getInt(Res.tSTARTS)));// used for starts
        h.put("filename", Res.translit(HistoryForm.getLastFileName()));
        h.put("hsize", Integer.toString(HistoryForm.getHistorySize()));
        h.put("uid", Integer.toString(App.getInt(Res.tUID)));
        h.put("local", App.local);
// todo: send also w, h, total_memory, starts_text, time_of_reading
        Http.go(this, h, this, "feedback");
    }
    public void commandAction(Command c, Displayable d){
        if (App.ok == c){
            App.show(this);
        }else if (App.back == c){
                App.show(MainMenu.getInstance());
        }
    }
}