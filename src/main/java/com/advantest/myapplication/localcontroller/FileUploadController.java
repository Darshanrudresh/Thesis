package com.advantest.myapplication.localcontroller;

import com.advantest.myapplication.database.service.DocStorageService;
import com.advantest.myapplication.dto.FileInfo;
import com.advantest.myapplication.service.FileServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tukaani.xz.BasicArrayCache;
import org.tukaani.xz.XZInputStream;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a Controller it send a POST request with necessary parameters
 * We Use ObjectMapper to convert to String into Java Objects
 *
 * @author darshan.rudresh
 */
@RestController
public class FileUploadController {

    @Autowired
    FileServices fileService;

    @Value("${buildLog.basePath}")
    private String basePath;


    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileInfo requestFile(@RequestParam(required = true, value = "file") MultipartFile file,
                                @RequestParam(required = true, value = "fileInfo") String fileInfo) {

        FileInfo info = null;
        if (file.isEmpty()) {
            log.info(" Please upload a new file which has contents");
            System.exit(1);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            info = objectMapper.readValue(fileInfo, FileInfo.class);
            fileService.uploadFile(file, info);
        } catch (IOException e) {
            log.error("Execution failed" + e.getMessage());
        }
        return info;
    }


    @GetMapping("/log/{buildId}/{fileName}")
    public void getData(@PathVariable String buildId,
                        @PathVariable String fileName,
                        @RequestParam int line,
                        HttpServletResponse response) throws Exception {

        if (buildId.length() <= 2) {
            throw new Exception("Length of buildId is small (less than two)");
        }
        String id = buildId.substring(0, 2);
        String id1 = buildId.substring(2, buildId.length());
        String refPath = basePath + id + "\\" + id1 + "\\" + fileName + ".xz";
        Path tempFile = Files.createTempFile("temp-", ".txt");
        System.out.println("Temp file : " + tempFile);
        try (FileInputStream fileStream = new FileInputStream(refPath);
             XZInputStream xzStream = new XZInputStream(fileStream, BasicArrayCache.getInstance())) {
            Files.copy(xzStream, Paths.get(String.valueOf(tempFile)), StandardCopyOption.REPLACE_EXISTING);

        }
        Stream<String> stream = Files.lines(Paths.get(String.valueOf(tempFile)));
        long lowerRange = line - 21;
        List<String> contents;
        if (lowerRange < 0) {
            contents = stream.skip(0).limit(40).collect(Collectors.toList());
            lowerRange = 0;
        } else {
            contents = stream.skip(lowerRange).limit(40).collect(Collectors.toList());
        }
        long lineNumber = lowerRange + 1;
        for (String content : contents) {

            response.getWriter().println(lineNumber + ": " + content);
            //response.getOutputStream().println(lineNumber + ": " + s);
            lineNumber++;
        }
        Files.deleteIfExists(tempFile);
    }
}
