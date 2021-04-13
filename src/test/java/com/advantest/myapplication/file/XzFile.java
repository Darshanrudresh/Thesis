package com.advantest.myapplication.file;

import org.tukaani.xz.BasicArrayCache;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author darshan.rudresh
 */
public class XzFile {
    public void compress(String path) {
        String destination = path + ".xz";
        try (FileOutputStream fileStream = new FileOutputStream(destination);
             XZOutputStream xzStream = new XZOutputStream(
                     fileStream, new LZMA2Options(LZMA2Options.PRESET_MAX), BasicArrayCache.getInstance())) {
            Files.copy(Paths.get(path), xzStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
