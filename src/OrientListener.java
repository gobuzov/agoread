import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: a.gobuzov
 * Date: 27.11.13
 * Time: 20:02
 * To change this template use File | Settings | File Templates.
 */
public class OrientListener implements com.nokia.mid.ui.orientation.OrientationListener{
    private static OrientListener instance;
    //
    public static void start(){
        instance = new OrientListener();
        com.nokia.mid.ui.orientation.Orientation.addOrientationListener(instance);
    }
    public void displayOrientationChanged(int id) {
        if (com.nokia.mid.ui.orientation.Orientation.ORIENTATION_LANDSCAPE==id    ||
            com.nokia.mid.ui.orientation.Orientation.ORIENTATION_PORTRAIT==id){
            MyCanvas mc = MyCanvas.getInstance();
            if (null!=mc)
                mc.changeOrientation(id);
            com.nokia.mid.ui.orientation.Orientation.setAppOrientation(id);
        }
    }
}