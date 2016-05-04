import javax.microedition.rms.RecordStore;

public class RMS {
    private static void deleteAllRMS(){
        try {
            String[] list = RecordStore.listRecordStores();
            if (null!=list)
                for (int i=list.length-1;i>=0;i--){
                    RecordStore.deleteRecordStore(list[i]);
                }
        } catch (Exception ecx){ecx.printStackTrace();}
    }
    public static void deleteRecordStore(String rms){
        try {
            RecordStore.deleteRecordStore(rms);
        } catch (Exception ecx){ecx.printStackTrace();}
    }
    public static final byte[] getBytesFromRms(String rms) {
        byte mas[] = null;
        try {
            RecordStore rs = RecordStore.openRecordStore(rms, false);
            mas = rs.getRecord(1);
            rs.closeRecordStore();
        } catch (Exception exc) {
            exc.printStackTrace();
            deleteAllRMS();
        }
        return mas;
    }
    public static final void setBytesToRms(String rms, byte[] mas){
        try {
            RecordStore rs = RecordStore.openRecordStore(rms, true);
            if (0 == rs.getNumRecords())
                rs.addRecord(mas, 0, mas.length);
            else
                rs.setRecord(1, mas, 0, mas.length);
            rs.closeRecordStore();
        } catch (Exception exc) {
            if (App.DEBUG) {
                exc.printStackTrace();
            }
        }
    }
}
