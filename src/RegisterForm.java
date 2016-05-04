import javax.microedition.lcdui.*;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 19.03.15
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */

// схема регистрации така€ - посылаем regcode на сервер, с другими параметрами, в том числе с local.
// ¬ случае успешной регистрации он в ответ присылает local, не успешной что-то другое
// ѕри инициализации программы, провер€ем local и tTHANK_REG если они совпадают, в tREGISTER заносим Ќ≈ null

public class RegisterForm extends Form implements CommandListener, ItemCommandListener, HttpListener {
    protected TextField regCode;

    public RegisterForm() {
        super(Res.getStringLower(Res.tREGISTRATION));
        regCode = new TextField(Res.getString(Res.tREG_CODE), App.get(Res.tREG_CODE, ""), 32, TextField.ANY);
        append(regCode);
        StringItem si = new StringItem(null, Res.getStringUpper(Res.tSUBMIT), Item.BUTTON);
        si.setDefaultCommand(new Command(Res.getStringUpper(Res.tSUBMIT), Command.OK, 1));
        si.setItemCommandListener(this);
        append(si);
        append(Res.getString(Res.tREG_NOTES));

        addCommand(App.back);
        setCommandListener(this);
    }
    public void process(Http http){
        String responce = http.getResponce();
        Form form = new Form(Res.getStringLower(Res.tREGISTRATION));
        App.put(Res.tTHANK_REG, responce);
        form.append(Res.getString(Res.tTHANK_REG));
        form.addCommand(App.back);
        form.setCommandListener(this);
        App.show(form);
    }
    public void commandAction(Command c, Item item){
        String regcode  = regCode.getString().trim();
        if (!Tools.checkName(regcode, 4)){
            Form form = new Form(Res.getString(Res.tINFORMATION));
            form.append(Res.getString(Res.tPLEASE_REG));
            form.addCommand(App.ok);
            form.setCommandListener(this);
            App.show(form);
            return;
        }
        App.put(Res.tREG_CODE, regcode);
        Hashtable h = new Hashtable(17);
        h.put("reg",  regcode);
        h.put("platform", App.get (Res.tABOUT));
        h.put("starts", Integer.toString(App.getInt(Res.tSTARTS)));// used for starts
        h.put("hsize", Integer.toString(HistoryForm.getHistorySize()));
        h.put("local", App.local);
        Http.go(this, h, this, "mobile_reg");
    }
    public void commandAction(Command c, Displayable d){
        if (App.ok == c){
            App.show(this);
        }else if (App.back == c){
            App.show(MainMenu.getInstance());
        }
    }
}