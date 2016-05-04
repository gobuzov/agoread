import javax.microedition.lcdui.*;
import java.util.Hashtable;
import java.util.Vector;

public class MainMenu extends List implements ItemCommandListener, CommandListener, HttpListener {
    int[] namesId = {Res.tHISTORY, Res.tCHOOSE_BOOK, Res.tDOWNLOAD_BOOK, Res.tHELP, Res.tABOUT};
    String[] iNames = {"star.png","folder.png","world.png","help.png","about.png"};
//    int[] namesId = {Res.tHISTORY, Res.tCHOOSE_BOOK, Res.tHELP, Res.tABOUT};
//    String[] iNames = {"star.png","folder.png","help.png","about.png"};
    private static MainMenu instance;

    private MainMenu () {
        super(Res.getStringLower(App.LITE ? Res.tLITETITLE: Res.tTITLE), List.IMPLICIT);
        for (int i=0; i<namesId.length; i++){
            String label = "  ".concat(Res.getString(namesId[i]));
            Image icon = Res.loadImage(iNames[i]);
            append(label, icon);
        }
        //setTitle(System.getProperty("microedition.locale"));
        addCommand(App.exit);
        setCommandListener(this);
    }
    public static MainMenu getInstance(){
        if (null==instance)
            instance = new MainMenu();
        return instance;
    }
    public void initHelp(){
        Form form = new Form(Res.getStringLower(Res.tHELP));
        String text = Res.getString(Res.tHELP_1)+"\n";
        if (App.istouch){
            text = text + Res.getString(Res.tNAVIGATION_TOUCH);
        } else
            text = text + Res.getString(Res.tNAVIGATION_KEYS);
        form.append(text);
        form.addCommand(App.back);
        form.setCommandListener(this);
        App.show(form);
    }
    private String[] list_encodings;/// NEED TO SHOW changed encoding
    private Form aboutForm;
    private StringItem def_enc;
    public void initAbout(){
        Form form = new Form(Res.getStringLower(Res.tABOUT));
        form.append(Res.getString(App.LITE? Res.tLITETITLE : Res.tTITLE) +
                " "+ Res.getString(Res.tABOUT1) + " " + App.instance.getAppProperty("MIDlet-Version")+
                "\n(c)"+ Res.getString(Res.tABOUT2));
        form.append(Res.getString(Res.tPLATFORM) + " " +App.platform);
        form.append(Res.getString(Res.tTOTALMEMORY) + " " + Tools.shortNumber(Runtime.getRuntime().totalMemory()) +"\n"+
                Res.getString(Res.tAVAILABLE) + " " + Tools.shortNumber(Runtime.getRuntime().freeMemory()));
        Vector vct = FileBrowser.getDirsInfo();
        int i = 0;
        while (null!=vct && i<vct.size()){
            String s = (String)vct.elementAt(i++);
            String total = Tools.shortNumber(((Long)vct.elementAt(i++)).longValue());
            String free  = Tools.shortNumber(((Long)vct.elementAt(i++)).longValue());
            form.append(s+" "+total + "\n"+Res.getString(Res.tAVAILABLE)+" "+free);
        }
        String local = null;
        try { local = System.getProperty("microedition.locale"); }catch (Exception exc){};
        if (null==local || "".equals(local))
            local = "en";
        form.append(Res.getString(Res.tLOCAL_ABOUT) +  " " +local);
        StringItem si = null;
        if (null==list_encodings)
            list_encodings = Tools.getSubstrings(Res.getString(Res.tLIST_ENCODINGS), ',');
        si = new StringItem(Res.getString(Res.tDEF_ENCODING),
                list_encodings[App.getInt(Res.tDEF_ENCODING, Encoder.ENC_UTF8)], Item.PLAIN);
        si.setItemCommandListener(this);
        si.setDefaultCommand(App.change);
        form.append(si);
        def_enc = si;
        if (App.LITE){
            si = new StringItem(null, Res.getStringUpper(Res.tFULLVERSION), Item.BUTTON);
            si.setDefaultCommand(App.fullversion);
            si.setItemCommandListener(this);
            form.append(si);
        }
      /*si = new StringItem(null, Res.getString(Res.tBOARD), Item.BUTTON);
        si.setDefaultCommand(App.board);
        si.setItemCommandListener(this);
        form.append(si);   */
        if ("ru".equals(App.local) && App.LITE){
            si = new StringItem(null, Res.getStringUpper(Res.tRUPAY), Item.BUTTON);
            si.setDefaultCommand(App.buy);
            si.setItemCommandListener(this);
            form.append(si);
        }
//
        si = new StringItem(null, Res.getStringUpper(Res.tNEWS), Item.BUTTON);
        si.setDefaultCommand(App.news);
        si.setItemCommandListener(this);
        form.append(si);
///sms
        /*si = new StringItem(null, Res.getString(Res.tTELL_FRIEND), Item.BUTTON);
        si.setDefaultCommand(App.sms);
        si.setItemCommandListener(this);
        form.append(si);*/
        if (0!=HistoryForm.getHistorySize()){
            si = new StringItem(null, Res.getStringUpper(Res.tFEEDBACK), Item.BUTTON);
            si.setDefaultCommand(App.feedback);
            si.setItemCommandListener(this);
            form.append(si);
        }
        if (false==App.getBool(Res.tREGISTER)){
            si = new StringItem(null, Res.getStringUpper(Res.tREGISTER), Item.BUTTON);
            si.setDefaultCommand(App.register);
            si.setItemCommandListener(this);
            form.append(si);
        }
        form.append("key:"+App.get(333));
        form.addCommand(App.back);
        form.setCommandListener(this);
        aboutForm = form;
        App.show(form);
    }
    private ChoiceGroup choiceGroup;
    private Form ChangeDefEncodingForm;
    private void initChangeDefEncodingForm(){
        Form form = new Form(Res.getStringLower(Res.tDEF_ENCODING));
        choiceGroup = new ChoiceGroup(null, Choice.EXCLUSIVE);
        for(int i=0; i<list_encodings.length; i++)
            choiceGroup.append(list_encodings[i], null);
        choiceGroup.setSelectedIndex(App.getInt(Res.tDEF_ENCODING, Encoder.ENC_UTF8), true);

        form.append(choiceGroup);
        form.setCommandListener(this);
        form.addCommand(App.back);
        ChangeDefEncodingForm = form;
        App.show(form);
    }
    public void commandAction(Command c, Item item){
        if (App.buy==c){
            Form form = new Form(Res.getStringLower(Res.tRUPAY));
            form.append(Res.getString(Res.tRUPAY_TEXT));
            form.addCommand(App.back);
            form.setCommandListener(this);
            App.show(form);
        }else if (App.feedback == c){
            App.show(new FeedbackForm());
        }else if (App.register == c){
            App.show(new RegisterForm());
        }else if (App.fullversion == c){
            goOvi();
            App.show(this);
        }else if (App.news == c){
            Hashtable h = new Hashtable(5);
            if (0==App.getInt(Res.tCLEAR)){
                h.put("uid", Integer.toString(App.getInt(Res.tUID)));
                h.put("platform", App.get (Res.tABOUT));
                h.put("local", App.local);
            }
            Http.go(this, h, this, "getnews");
        }else if (App.sms == c){
           // App.show(new SmsForm());
        }else if (App.change==c){
            initChangeDefEncodingForm();
        }
    }
    public void process(Http http){   // show news
        App.put(Res.tCLEAR, 1);
        App.put(Res.tNEWS, new Long(System.currentTimeMillis()));
        Form form = new Form(Res.getStringLower(Res.tINFORMATION));
        String text = http.getResponce();
        form.append(text);
        form.addCommand(App.back);
        form.setCommandListener(this);
        App.show(form);
    }
    private void showQuit(){
        Alert alert = new Alert(Res.getString(Res.tINFORMATION));
        alert.setString(Res.getString(Res.tASK_QUIT));
        alert.setCommandListener(this);
        alert.addCommand(App.ok);
        alert.addCommand(App.LITE ? App.fullversion:App.moreapps);
        alert.addCommand(App.cancel);
        if (501==App.platform_id)
            alert.addCommand(App.back);
        App.show(alert);
    }
    private void goOvi(){
        try{ App.instance.platformRequest("http://www.store.ovi.com/publisher/Arcadiy%20Gobuzov/");}
        catch (Exception exc){exc.printStackTrace();}
    }
    public void commandAction(Command c, Displayable d){
        if (this!=d){
            if (d.equals(ChangeDefEncodingForm)){
                int id = choiceGroup.getSelectedIndex();
                App.put(Res.tDEF_ENCODING, id);
                def_enc.setText(list_encodings[id]);
                App.show(aboutForm);
                return;
            }else if (App.back==c || App.cancel==c){
                App.show(this);
            }else if (App.fullversion==c||App.moreapps==c){
                goOvi();
            }else if (App.ok==c){
                try{App.instance.destroyApp(true);}catch (Exception xp){};
            }
            return;
        }
        if (App.exit == c) {
            showQuit();
            return;
        }
        int selected = this.getSelectedIndex();
        if (0 == selected) {
            /*MyCanvas mc = MyCanvas.getInstance();
            mc.setView(new SettingsPanel(null));
            App.show(mc);//*/

            App.show(new HistoryForm());
        }else if (1 == selected){
            App.show(FileBrowser.getInstance());
        }else if (2 == selected){
            //App.show(new BMTest());
            //App.show(new BMMenu());
            App.show(new OnlineMenu());
        }else if (3 == selected){
            initHelp();
        }else if (4 == selected){
            initAbout();
        }
    }
}
