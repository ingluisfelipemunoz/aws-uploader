package com.uploader.aws_uploader.filestore;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.uploader.aws_uploader.bucket.BucketName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {
    private final AmazonS3 s3;
    @Autowired
    public FileStore(AmazonS3 s3) {
        this.s3 = s3;
    }

    public void save(String path, String fileName, Optional<Map<String, String>> metadata, InputStream inputStream) {
     try {
         ObjectMetadata objectMetadata = new ObjectMetadata();
         metadata.ifPresent(map -> {
             if(!map.isEmpty()) {
                 map.forEach(objectMetadata::addUserMetadata);
             }
         });
         s3.putObject(path, fileName, inputStream, objectMetadata);
     } catch(Exception ex) {
         throw new IllegalStateException("Failed to store file to s3", ex);
     }
    }

    public void save2(String fullFileName, Optional<Map<String, String>> metadata, InputStream inputStream) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            metadata.ifPresent(map -> {
                if(!map.isEmpty()) {
                    map.forEach(objectMetadata::addUserMetadata);
                }
            });
            s3.putObject(BucketName.PROFILE_IMAGE.getBucketName(), fullFileName, inputStream, objectMetadata);

        } catch(Exception ex) {
            throw new IllegalStateException("Failed to store file to s3", ex);
        }
    }

    public byte[] download(String path, String key) {
        try {
            //S3ObjectInputStream stream =  s3.getObject(path).getObjectContent();
            GetObjectRequest objectRequest = new GetObjectRequest(path, key);
            S3ObjectInputStream stream = s3.getObject(objectRequest).getObjectContent();
            return IOUtils.toByteArray(stream);
        } catch(Exception ex) {
            throw new IllegalStateException("Failed to download file from s3", ex);
        }
    }
}
