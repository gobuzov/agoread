import javax.microedition.lcdui.Font;
import java.util.Vector;
/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 19.11.14
 * Time: 20:31
 * To change this template use File | Settings | File Templates.
 */
public class Flyweight {
    private int[] offsets;
    private int[] ids;
    private int start, sz;
    private Vector temp = new Vector();
    //
    public Font getFont(){     /// FID 16,8, 4,2,1
        int fid = ids[start];  //      size  style
        int size = (fid>>3)&3; // Style : Font.STYLE_PLAIN = 0; STYLE_BOLD = 1; STYLE_ITALIC = 2; STYLE_UNDERLINED = 4;
        if (3==size)           // Size  : 3 - default, 0 - Small, 1 - Medium, 2 - Large
            size = App.getInt(Res.tFONT_SIZE);
        return Font.getFont(Font.FACE_PROPORTIONAL, fid&7, 0==size? Font.SIZE_SMALL: 1==size? Font.SIZE_MEDIUM : Font.SIZE_LARGE);
    }
    public int getColor(){
        int cid = ids[start];
        return cid<=c.COLOR_MAX ? App.getColor(cid) : cid;
    }
    //public int getId(){ return ids[start]; }
    public void nextPosition(){++start;}
    public int getOffset(){return start<sz ? offsets[start] : Integer.MAX_VALUE;}
    public void addRecord(int offset, int value){
        temp.addElement(new Integer(offset)); temp.addElement(new Integer(value));
    }
    public void init(){ offsets = null; ids = null; temp = new Vector();}
    public void trim(){
        sz = temp.size()>>1;
        offsets = new int[sz];
        ids = new int[sz];
        for (int i=0, j=0; i<sz; i++){
            offsets[i] = temp.elementAt(j++).hashCode();
            ids[i] = temp.elementAt(j++).hashCode();
        }
        temp = null;
    }
    public void setStart(int offset){
        for (int i=0; i<sz; i++)
            if (offset>= offsets[i] && (i==sz-1 || offset<offsets[i+1])){
                start = i;
                return;
            }
    }
}