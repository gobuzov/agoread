import javax.microedition.lcdui.Graphics;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 02.11.14
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class CharVector {
    public final static int ANCHOR = Graphics.BOTTOM | Graphics.LEFT;
    public final static int SECTION = 2048;// 2 ^ roll
    private Vector vector = new Vector();
    private char[] readBuff, writeBuff;
    private int readId =-1, writeId =-1, len, roll, mask; // mask & roll need for speedup

    public CharVector(){
        int i = 1;
        while (i!=SECTION){
            i = i<<1;
            ++roll;
        }
        mask = SECTION-1;
    }
    public char getChar(int pos){
        int ost = pos >> roll;
        if (ost!=readId){
            readBuff = (char[])vector.elementAt(ost);
            readId = ost;
        }
        return readBuff[pos & mask];
    }
    public void putChar(int pos, char ch){
        //System.out.print(ch);
        int ost = pos >> roll;
        if (ost!=writeId){
            if (ost==vector.size()){
                writeBuff = new char[SECTION];
                vector.addElement(writeBuff);
            }else
                writeBuff = (char[])vector.elementAt(ost);
            writeId = ost;
        }
        writeBuff[pos & mask] = ch;
    }
    public void setLen(int len){this.len = len;}
    public int getLen(){return len;}

    public void putString(int pos, String s){
        for (int i=0; i<s.length();)
            putChar(pos++, s.charAt(i++));
    }
    public String getString(int pos, int len){
        char[] buff = new char[len];
        for (int i=0; i < len;)
            buff[i++] = getChar(pos++);
        return new String(buff);
    }
    public void trim(int size){
        int sections = size >> roll;
        if (0!=(size & mask))
            ++sections;
        for (int i=vector.size()-1; i>=sections; i--)
            vector.removeElementAt(i);
    }
    public void drawChars(Graphics g, int pos, int len, int x, int y){
        int ost = pos >> roll;
        if (ost!=readId){
            readBuff = (char[])vector.elementAt(ost);
            readId = ost;
        }
        int localpos = pos & mask;
        if (localpos+len <= SECTION){ // ???? <=
            g.drawChars(readBuff, localpos, len, x, y, ANCHOR);
        }else{
            int len2 = localpos + len - SECTION;
            g.drawChars(readBuff, localpos, len-len2, x, y, ANCHOR);
            int w = g.getFont().charsWidth(readBuff, localpos, len-len2);
            readBuff = (char[])vector.elementAt(++readId);
            g.drawChars(readBuff, 0, len2, x+w, y, ANCHOR);
            Debug.log("************ CharVector.drawChars *************");
        }
    }
}
