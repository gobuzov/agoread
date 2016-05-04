import java.util.Vector;
/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 30.12.14
 * Time: 18:54
 * We have array of 2 Vectors: [0] Strings - paths, [1] - Integers - ids at RMS (PREFIX+)
 */
public class LinkManager {
    private String rms_prefix;
    private Vector paths, rmsids;

    public LinkManager(int var_id, String rms_prefix){
        this.rms_prefix = rms_prefix;
        Object[] arr = (Object[])App.get(var_id);// [0] Vector of String (paths), [1] - Vector of Integer (RMS ids for FileLinks)
        if (null==arr){
            arr = new Object[]{new Vector(), new Vector()};
            App.put(var_id, arr);
        }
        paths = (Vector)arr[0];
        rmsids = (Vector)arr[1];
    }
    public Vector getCaptions(){
        return paths;
    }
    public void removeAll(){
        int i = paths.size()-1;
        while (false==paths.isEmpty()){
            String rmsName = rms_prefix.concat(Integer.toString(rmsids.elementAt(i).hashCode()));
            RMS.deleteRecordStore(rmsName);
            rmsids.removeElementAt(i);
            paths.removeElementAt(i);
            i--;
        }
    }
    public void updateLink(String path, boolean remove){
        int id = paths.indexOf(path);
        if (-1!=id){
            paths.removeElementAt(id); // String path
            Object rmsId = rmsids.elementAt(id);
            rmsids.removeElementAt(id);
            if (false==remove){
                paths.insertElementAt(path, 0);
                rmsids.insertElementAt(rmsId, 0);
            }else{
                String rmsName = rms_prefix.concat(Integer.toString(rmsId.hashCode()));
                RMS.deleteRecordStore(rmsName);
            }
        }else {
            Debug.log("********* LinkManager.updateLink");
        }
    }
    public void updatePath(String oldpath, String newpath){
        int id = paths.indexOf(oldpath);
        if (-1!=id)
            paths.setElementAt(newpath, id);
    }
    public int addLink(String path){
        int id = 0;
        while (true){
            boolean found = false;
            for (int i=rmsids.size()-1; i>=0; i--)
                found |= rmsids.elementAt(i).hashCode()==id;
            if (found)
                id++;
            else
                break;
        }
        paths.insertElementAt(path, 0);
        rmsids.insertElementAt(new Integer(id), 0);
        return id;
    }
    public Object getObjectByName(String name){
        int id = paths.indexOf(name);
        return -1==id ? null: getObject(id);
    }
    public Object getObject(int id){
        String rmsName = rms_prefix.concat(Integer.toString(rmsids.elementAt(id).hashCode()));
        return Tools.bytes2obj(RMS.getBytesFromRms(rmsName));
    }
}