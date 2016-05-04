import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.wireless.messaging.*;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 09.10.14
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class SmsForm extends Form implements CommandListener, Runnable{
    TextField tf;
    String text = "Look! Nice bookreader: http://www.store.ovi.com/publisher/Arcadiy%20Gobuzov/";

    public SmsForm(){
        super("SMS");
        this.addCommand(App.back);
        //this.addCommand(App.sms);
        this.setCommandListener(this);
        tf = new TextField("Select phone", "", 20, TextField.PHONENUMBER);
        this.append(tf);
        this.append(text);
    }
    public void run(){
//        for (int i=0; i<1; i++){
        String s = tf.getString().trim();
        StringBuffer sb = new StringBuffer("sms://");
        for(int j = 0; j < s.length(); j++)
            if(Character.isDigit(s.charAt(j)) || s.charAt(j) == '+' && j == 0)
                    sb.append(s.charAt(j));
        //sb.append(":5000");
        s = sb.toString();
        MessageConnection messageconnection = null;
        try
        {
            TextMessage tm = (TextMessage)(messageconnection = (MessageConnection) Connector.open(s)).newMessage("text");
            tm.setPayloadText(text);
            //tm.setPayloadText("misha "+i);
            messageconnection.send(tm);
            if(messageconnection != null)
                try {
                    messageconnection.close();
                }
                catch(Exception _ex) { }
        }
        catch(Exception _ex)
        {
            if(messageconnection != null)
                try
                {
                    messageconnection.close();
                }
                catch(Exception _ex2) { }
        }
//        }
    }
    public void commandAction(Command c, Displayable d){
        if (App.sms==c && null!=tf.getString())
            new Thread(this).start();
        App.show(MainMenu.getInstance());

    }
}
