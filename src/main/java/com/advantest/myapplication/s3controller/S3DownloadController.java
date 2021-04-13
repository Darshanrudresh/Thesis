package com.advantest.myapplication.s3controller;

import com.advantest.myapplication.service.S3Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * S3DownloadController.class downloads compressed and decompressed .xz file extensions referring Amazon S3
 *
 * @author darshan.rudresh
 */
@RestController
@CacheConfig(cacheNames = {"buildLogXCache"})
public class S3DownloadController {
    @Autowired
    private S3Services s3Services;

    @GetMapping("/download/S3/log/{buildId}/{fileName}")
    @Cacheable
    public ResponseEntity<Resource> downLoadFile(@PathVariable String buildId,
                                                 @PathVariable String fileName,
                                                 HttpServletResponse response) throws Exception {
        if (fileName.endsWith(".xz")) {
            byte[] data = s3Services.downloadS3File(buildId, fileName);
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } else {
            if (buildId.length() <= 2) {
                throw new Exception("Length of buildId is small (less than two)");
            }
            String refFile = fileName + ".xz";
            Resource resource = s3Services.uncompressS3File(buildId, refFile, response);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .body(resource);

        }

    }

}
