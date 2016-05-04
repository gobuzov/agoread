import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 17.10.14
 * Time: 20:36
 * To change this template use File | Settings | File Templates.
 * Класс для статистики текста - какие символы встречаются чаще, итд. Пока никак не используется, а выбрасывать жалко
 */
public class TextStaat {
    public void staat(char[] arr, String text){
        Hashtable table = new Hashtable();
        int sz = text.length();
        char[] stor = new char[128];
        int[]mas = new int[256];
        //Integer stub = new Integer(0);
        for (int i=0; i<sz; i++){
            char ch = text.charAt(i);
            if (ch<128){
                mas[ch]++;
            }else{
                int ci = ch&127;
                if (stor[ci]==ch){
                    mas[128|ci]++;
                }else if (0==stor[ci]){
                    stor[ci] = ch;
                    mas[128|ci] = 1;
                }else{
                    Character chr = new Character(ch);
                    Object obj = table.get(chr);
                    if (null==obj){
                        table.put(chr, new MyInt());
                        System.out.println("double put :"+ch+" "+(int)ch);
                    }else
                        ((MyInt)obj).incr();
                }
            }
        }
        int cnt = 0;
        for (int i=0; i<256; i++){
            if (0!=mas[i])
                cnt++;
        }
        sz = cnt + table.size();
        Character[] carr = new Character[sz];
        int[] iarr = new int[sz];
        for (int i=0,j=0; i<256; i++){
            if (0!=mas[i]){
                carr[j] = new Character((i<128)? (char)i : stor[i-128]);
                iarr[j++] = mas[i];
            }
        }
        int i = cnt;
        Enumeration e = table.keys();
        Enumeration e2 = table.elements();
        while (e.hasMoreElements()){
            carr[i] = (Character)e.nextElement();
            iarr[i++] = e2.nextElement().hashCode();
        }
        table = null;
        QSort.qsort(carr, iarr, 0, sz-1);
        System.out.println("sz="+sz);
        int s1 = 0, s2 = 0;
        for (i=0; i<sz; i++){
            System.out.println(carr[i].charValue()+" "+iarr[i]+" "+carr[i].hashCode());
            if (i<15)
                s1+=iarr[i];
            else
                s2+=iarr[i];
        }
        System.out.println("s1="+s1+" s2="+s2);
        System.out.println("full="+(s1+s2)+" hexpack="+(s1/2+s2*3/2));
    }
    public void staat1(char[] arr, String text){
        Hashtable table = new Hashtable(255);
        int sz = (null!=arr)? arr.length : text.length();
        //Integer stub = new Integer(0);
        for (int i=0; i<sz; i++){
            Character ch = new Character(null!=arr ? arr[i]: text.charAt(i));
            Object obj = table.get(ch);
            if (null==obj)
                table.put(ch, new MyInt());
            else
                ((MyInt)obj).incr();
//                table.put(ch, new Integer(obj.hashCode()+1));
        }
        sz = table.size();
        Character[] carr = new Character[sz];
        int[] iarr = new int[sz];
        int i = 0;
        Enumeration e = table.keys();
        Enumeration e2 = table.elements();
        while (e.hasMoreElements()){
            carr[i] = (Character)e.nextElement();
            iarr[i++] = e2.nextElement().hashCode();
        }
        table = null;
        QSort.qsort(carr, iarr, 0, sz-1);
        System.out.println("sz="+sz);
        int s1 = 0, s2 = 0;
        for (i=0; i<sz; i++){
            System.out.println(carr[i].charValue()+" "+iarr[i]+" "+carr[i].hashCode());
            if (i<15)
                s1+=iarr[i];
            else
                s2+=iarr[i];
        }
        System.out.println("s1="+s1+" s2="+s2);
        System.out.println("full="+(s1+s2)+" hexpack="+(s1/2+s2*3/2));
    }
    public void staat2(char[] arr, String text){
        int[] mas = new int[65536];// todo: сделать через hashtable
        int sz = (null!=arr)? arr.length : text.length();

        for (int i=0; i<sz; i++)
            mas[null!=arr ? arr[i]: text.charAt(i)]++;
        Vector v = new Vector();
        for (int i=0; i<65536; i++)
            if (0!=mas[i])
                v.addElement(new Character((char)i));
        sz = v.size();
        Character[] carr = new Character[sz];
        v.copyInto(carr);
        int[] iarr = new int[sz];
        for (int i=0; i<sz; i++)
            iarr[i] = mas[carr[i].hashCode()];
        mas = null;
        QSort.qsort(carr, iarr, 0, sz-1);
        System.out.println("sz="+sz);
        int s1 = 0, s2 = 0;
        for (int i=0; i<sz; i++){
            //System.out.println(carr[i].charValue()+" "+iarr[i]+" "+carr[i].hashCode());
            if (i<15)
                s1+=iarr[i];
            else
                s2+=iarr[i];
        }
        System.out.println("s1="+s1+" s2="+s2);
        System.out.println("full="+(s1+s2)+" hexpack="+(s1/2+s2*3/2));
    }

}
