package com.advantest.myapplication.service;


import com.advantest.myapplication.database.service.DocStorageService;
import com.advantest.myapplication.dto.FileInfo;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tukaani.xz.BasicArrayCache;
import org.tukaani.xz.XZInputStream;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * S3Services.class is used to upload and download files to Amazon S3 using aws-s3 SDK
 *
 * @author darshan.rudresh
 */
@Service
public class S3Services {


    @Value("${s3.bucket}")
    private String bucketName;


    @Autowired
    private AmazonS3 s3client;

    @Autowired
    private DocStorageService docStorageService;


    public void uploadWithS3Client(MultipartFile fileName, FileInfo fileInfo) {
        String bucketNameLow = bucketName.toLowerCase();
        boolean bucketExists = s3client.doesBucketExistV2(bucketNameLow);
        if (!bucketExists) {
            s3client.createBucket(bucketNameLow);
            System.out.println("Bucket created");
        } else {
            System.out.println("bucket is present");
        }
        String build = fileInfo.getBuildId();
        String key = build.substring(0, 2) + "/" + build.substring(2, build.length()) + "/" + fileName.getOriginalFilename();
        System.out.println("Key name is:" + key);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileName.getSize());
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketNameLow, key, fileName.getInputStream(), metadata);

            s3client.putObject(putObjectRequest);
            listS3Objects();
        } catch (AmazonClientException | IOException ace) {
            System.out.println("Error Message:   " + ace.getMessage());
        }
    }


    public void listS3Objects() {
        List<String> blobList = new ArrayList<String>();
        System.out.format("Objects(files) in S3 bucket %s:\n", bucketName);
        ListObjectsV2Result result = s3client.listObjectsV2(bucketName);
        List<S3ObjectSummary> blobs = result.getObjectSummaries();
        for (S3ObjectSummary blob : blobs) {
            blobList.add(blob.getKey());
            // System.out.println("* " + blob.getKey());
            docStorageService.saveFile(blob.getKey());
        }
    }

    public byte[] downloadS3File(String buildId, String fileName) {

        String key = buildId.substring(0, 2) + "/" + buildId.substring(2, buildId.length()) + "/" + fileName;
        S3Object obj = s3client.getObject(bucketName.toLowerCase(), key);
        try (S3ObjectInputStream stream = obj.getObjectContent();) {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource uncompressS3File(String buildId, String fileName, HttpServletResponse response) {

        String key = buildId.substring(0, 2) + "/" + buildId.substring(2, buildId.length()) + "/" + fileName;
        System.out.println("key is :" + key);
        S3Object obj = s3client.getObject(bucketName.toLowerCase(), key);
        Resource resource = null;
        try (S3ObjectInputStream stream = obj.getObjectContent();) {
            XZInputStream xzStream = new XZInputStream(stream, BasicArrayCache.getInstance());
            resource = new ByteArrayResource(xzStream.readAllBytes());
            response.setContentType("txt/plain");
            IOUtils.copy(xzStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resource;
    }
}
