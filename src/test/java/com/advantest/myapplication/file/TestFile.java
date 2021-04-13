package com.advantest.myapplication.file;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author darshan.rudresh
 */
public class TestFile {
    public void fileCreator()
            throws IOException {
        String path = "C:\\Thesis\\JerryD1.txt";
        File file = new File(path);
        XzFile xzfile = new XzFile();
        //InputStream inputStream = new URL("https://en.wikipedia.org/wiki/Germany").openStream();
        InputStream inputStream = new URL("https://www.gnu.org/licenses/gpl-3.0.txt").openStream();
        FileOutputStream outputStream = new FileOutputStream(file);
        int i = IOUtils.copy(inputStream, outputStream);
        //long j= IOUtils.copyLarge(inputStream,outputStream);
        xzfile.compress(path);
    }
}
