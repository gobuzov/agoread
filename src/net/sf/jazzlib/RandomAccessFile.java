package net.sf.jazzlib;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ago
 * Date: 26.10.14
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */
public class RandomAccessFile {
    private FileConnection file;
    private DataInputStream in;
    private int pointer = 0x7fffffff;
//
    public RandomAccessFile(String url) throws IOException  {
        file = (FileConnection) Connector.open(url, Connector.READ);
        if(file.isDirectory() || !file.exists()){
            if(!file.isDirectory());
            throw new IOException("File not found:"+url);
        } else {
            seek(0);
            return;
        }
    }
    public final void seek(int position) throws IOException {
        if(position == pointer)
            return;
        if(position < 0 || position >= length())
            throw new IllegalArgumentException("Trying to seek outside file's contents");
        int pos;
        if(position < pointer)
        {
            if(in != null)
                in.close();
            in = file.openDataInputStream();
            pos = position;
        } else
        {
            pos = position - pointer;
        }
        skipBytes(pos);
        pointer = position;
    }
    public final long skip(long n) throws IOException {
        return (long)skipBytes((int)n);
    }
    public final int skipBytes(int n) throws IOException {
        int skipped = in.skipBytes(n);
        pointer += skipped;
        return skipped;
    }
    public final int length() throws IOException {
        return (int)file.fileSize();
    }
    public final int read(byte b[], int off, int len) throws IOException {
        int count = in.read(b, off, len);
        if(count > 0)
            pointer += count;
        return count;
    }
    public final int read() throws IOException {
        int read = in.read();
        if(read >= 0)
            pointer++;
        return read;
    }
    public final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }
    public final void readFully(byte b[], int off, int len) throws IOException {
        pointer += len;
        in.readFully(b, off, len);
    }
    public final void close() throws IOException {
        in.close();
        file.close();
    }
}