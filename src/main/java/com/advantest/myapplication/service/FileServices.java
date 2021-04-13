package com.advantest.myapplication.service;

import com.advantest.myapplication.dto.FileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is class is responsible in creating a Directory and uploading the file locally
 *
 * @author darshan.rudresh
 */
@Service
public class FileServices {


    @Value("${buildLog.basePath}")
    private String basePath;

    public void uploadFile(MultipartFile file, FileInfo info) {
        File filePath = null;
        info.setResult(true);
        System.out.println(info.getName());
        System.out.println(info.getBuildId());
        String build = info.getBuildId();
        //docStorageService.saveFile(file,build);
        try {
            String firstSpilt = build.substring(0, 2);
            String secondSpilt = build.substring(2, build.length());
            File createDir = new File("C:\\Temp\\" + firstSpilt + "\\" + secondSpilt);
            createDir.mkdirs();
            filePath = new File(createDir + "\\" + file.getOriginalFilename());
            filePath.createNewFile();

            Files.write(Paths.get(filePath.toString()), file.getBytes());
            System.out.println(" Uploaded file is stored in: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Resource downloadFile(String buildId, String fileName) throws Exception {
        if (buildId.length() <= 2) {
            throw new Exception("Length of buildId is small ( less than two)");
        }
        String id = buildId.substring(0, 2);
        String id1 = buildId.substring(2, buildId.length());
        String refPath = basePath + id + "\\" + id1 + "\\" + fileName;
        Path path = Paths.get(refPath).toAbsolutePath();
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Issue in reading the file", e);
        }
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("the file doesn't exist or not readable");
        }
    }
}
