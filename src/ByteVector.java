import java.util.Vector;
import java.io.*;
/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 05.01.15
 * Time: 21:08
 * To change this template use File | Settings | File Templates.
 */
public class ByteVector {
    public final static int SECTION = 4096, ROLL = 12, MASK = 4095;// 2 ^ roll, mask & roll need for speedup
//    public final static int SECTION = 16384, ROLL = 14, MASK = 16383;// 2 ^ roll, mask & roll need for speedup
//    public final static int SECTION = 1024, ROLL = 10, MASK = 1023;// 2 ^ roll, mask & roll need for speedup
    protected Vector vector;
    protected byte[] readBuff;
    protected int readId =-1, len = 0;
    protected boolean freemem = true;

    /// по умолчанию создается объект-заглушка
    public ByteVector(){
        byte[] word = "E X A M P L  ".getBytes();
        int sz = word.length;
        byte[] buff = new byte[SECTION/sz * sz];
        for (int i=0; i<buff.length; i+=sz)
            System.arraycopy(word, 0, buff, i, sz);
        vector = new Vector(1);
        vector.addElement(buff);
        len = buff.length;
    }
    public ByteVector(InputStream is, int SZ) throws IOException {
        vector = new Vector(SZ/SECTION +1);
        while (true){
            byte[]buff = new byte[SZ<SECTION ? SZ : SECTION];
            int start = 0, sz = buff.length;
            boolean err = false;
            while (0!=sz){
                int readed = is.read(buff, start, sz);
                if (-1==readed){// почти невозможный случай
                    len+=start;
                    if (0!=start)
                        vector.addElement(buff);
                    err = true;
                    break;
                }else {
                    start+=readed;
                    sz-=readed;
                }
            }
            if (err)
                break;
            vector.addElement(buff);
            len+=buff.length;
            SZ-=buff.length;
            if (0==SZ)
                break;
        }
    }
    public ByteVector(byte[] buffer, int begin, int sz){
        vector = new Vector(sz/SECTION +1);
        while (true){
            byte[]buff = new byte[sz<SECTION ? sz : SECTION];
            System.arraycopy(buffer, begin, buff, 0, buff.length);
            vector.addElement(buff);
            len+=buff.length;
            sz-=buff.length;
            if (0==sz)
                break;
        }
    }
    public byte getByte(int pos){
        int ost = pos >> ROLL;
        if (ost!=readId){
            readBuff = (byte[])vector.elementAt(ost);
            readId = ost;
            if (ost>0 && freemem)
                vector.setElementAt(null, ost-1);// free memory
        }
        return readBuff[pos & MASK];
    }
    public void setFreemem(boolean b){freemem = b;}
    public int getLen(){return len;}
}
