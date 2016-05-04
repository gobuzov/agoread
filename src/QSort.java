import java.util.Vector;
/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 28.01.14
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */
public class QSort {
    public static void qsort (Vector v){
        String[] mas = new String[v.size()];
        v.copyInto(mas);
        qsort (mas);
        for (int i=v.size()-1; i>=0; i--)
            v.setElementAt(mas[i], i);
    }
    public static void qsort(String[] strings) {
        if (strings.length>1){
            int len = strings.length;
            String[] low = new String[len];
            for (int i = 0; i < len; i++)
                low[i] = strings[i].toLowerCase();
            qsort(strings, low, 0, len - 1);
        }
    }
    public static void qsort(Object[]obj, String[] ids, int start, int end){
        if (end <= start)
            return;
        String dt = ids[start];
        int i = start, j = end + 1;
        for (; ; ) {
            do i++; while (i < end && ids[i].compareTo(dt) < 0);
            do j--; while (j > start && ids[j].compareTo(dt) > 0);
            if (j <= i)
                break;
            String tms = ids[i];
            ids[i] = ids[j];
            ids[j] = tms;
            Object ob = obj[i];
            obj[i] = obj[j];
            obj[j] = ob;
        }
        String tms = ids[start];
        ids[start] = ids[j];
        ids[j] = tms;
        Object ob = obj[start];
        obj[start] = obj[j];
        obj[j] = ob;

        qsort(obj, ids, start, j - 1);
        qsort(obj, ids, j + 1, end);
    }
/// Сортировка по массиву int
    public static void qsort(Object[] obj, int[] ids, int start, int end) {
        if (end <= start)
            return;
        int dt = ids[start];
        int i = start, j = end + 1;
        for (; ; ) {
            do i++; while (i < end && ids[i] > dt);
            do j--; while (j > start && ids[j] < dt);
            if (j <= i)
                break;
            int tmp = ids[i];
            ids[i] = ids[j];
            ids[j] = tmp;
            Object tms = obj[i];
            obj[i] = obj[j];
            obj[j] = tms;
        }
        int tmp = ids[start];
        ids[start] = ids[j];
        ids[j] = tmp;
        Object tms = obj[start];
        obj[start] = obj[j];
        obj[j] = tms;

        qsort(obj, ids, start, j - 1);
        qsort(obj, ids, j + 1, end);
    }//*/
}
