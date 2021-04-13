package com.advantest.myapplication.localcontroller;

import com.advantest.myapplication.service.FileServices;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.tukaani.xz.BasicArrayCache;
import org.tukaani.xz.XZInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * FileDownloadController.class downloads locally compressed and decompressed .xz file extensions
 *
 * @author darshan.rudresh
 */
@RestController
@CacheConfig(cacheNames = {"buildLogXCache"})
public class FileDownloadController {

    @Autowired
    FileServices fileService;

    @Value("${buildLog.basePath}")
    private String basePath;


    @GetMapping("/download/log/{buildId}/{fileName}")
    @Cacheable
    public ResponseEntity<Resource> downLoadFile(@PathVariable String buildId,
                                                 @PathVariable String fileName,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {



        if (fileName.endsWith(".xz")) {
            Resource resource = fileService.downloadFile(buildId, fileName);
            String mimeType;
            try {
                mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException e) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            mimeType = mimeType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mimeType;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + resource.getFilename())
                    .body(resource);

        } else {

            if (buildId.length() <= 2) {
                throw new Exception("Length of buildId is small (less than two)");

            }
            String id = buildId.substring(0, 2);
            String id1 = buildId.substring(2, buildId.length());
            String refPath = basePath + id + "\\" + id1 + "\\" + fileName + ".xz";
            ByteArrayResource resource = null;

            try (FileInputStream fileStream = new FileInputStream(refPath)) {
                XZInputStream xzStream = new XZInputStream(fileStream, BasicArrayCache.getInstance());
                resource = new ByteArrayResource(xzStream.readAllBytes());
                response.setContentType("txt/plain");
                IOUtils.copy(xzStream, response.getOutputStream());
                response.flushBuffer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .body(resource);
        }

    }
}
