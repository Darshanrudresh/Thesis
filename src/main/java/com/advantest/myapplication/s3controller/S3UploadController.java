package com.advantest.myapplication.s3controller;

import com.advantest.myapplication.database.model.Doc;
import com.advantest.myapplication.database.service.DocStorageService;
import com.advantest.myapplication.dto.FileInfo;
import com.advantest.myapplication.service.S3Services;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a Controller it send a POST request with necessary parameters
 * We Use ObjectMapper to convert to String into Java Objects data is stored in Amazon S3
 *
 * @author darshan.rudresh
 */
@RestController
public class S3UploadController {
    @Autowired
    private S3Services s3Services;

    @Autowired
    private AmazonS3 s3client;
    @Autowired
    private DocStorageService docStorageService;

    @Value("${s3.bucket}")
    private String bucketName;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostMapping(value = "/upload/S3", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileInfo requestFile(@RequestParam(required = true, value = "file") MultipartFile file,
                                @RequestParam(required = true, value = "fileInfo") String fileInfo) {
        if (file.isEmpty()) {
            log.info(" Please upload a new file which has contents");
            System.exit(1);
        }
        FileInfo info = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            info = objectMapper.readValue(fileInfo, FileInfo.class);
            s3Services.uploadWithS3Client(file, info);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return info;
    }

    @GetMapping("/log/S3/{buildId}/{fileName}")
    public void getData(@PathVariable String buildId,
                        @PathVariable String fileName,
                        @RequestParam int line,
                        HttpServletResponse response) throws Exception {
        if (buildId.length() <= 2) {
            throw new Exception("Length of buildId is small (less than two)");

        }
        String key = buildId.substring(0, 2) + "/" + buildId.substring(2, buildId.length()) + "/" + fileName + ".xz";
        Path tempFile = Files.createTempFile("temp-", ".txt");
        System.out.println("Temp files are in the directory" + tempFile);
        S3Object obj = s3client.getObject(bucketName.toLowerCase(), key);
        try (S3ObjectInputStream stream = obj.getObjectContent();) {
            XZInputStream xzStream = new XZInputStream(stream, BasicArrayCache.getInstance());
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
            lineNumber++;
        }
        Files.deleteIfExists(tempFile);
    }

    @GetMapping("/json")
    private List<Doc> getJson() {
        return docStorageService.getAllStudent();
    }
}