import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 15.09.14
 * Time: 20:56
 * To change this template use File | Settings | File Templates.
 * класс, определ€ющий начала и размеры частей (section) fb2 файла, их заголовки.
 * <image l:href="#i_001.png"/>
 * <a l:href="#n_1" type="remark">1</a>
 */
public abstract class FileProc{
    public abstract void process(byte[] buffer, InputStream is, FileLink link, int length);
}