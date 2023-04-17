package com.s3_sqs_mock.proj.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

@Data
@Service
@ConfigurationProperties(prefix = "s3")
@Slf4j
public class S3Utils {

    @Autowired
    private AmazonS3 amazonS3;

    private String bucketMarket;
    private String bucketBe;
    private String bucketAt;

    public void postFileToS3(String key, File file, String country) {

        String bucket = country.equals("BE") ? bucketBe : bucketAt;
        log.info("Post file: {}, to S3 bucket: {}", key, bucket);

        try {
            amazonS3.putObject(new PutObjectRequest(bucket, key, file));
        } catch (Exception exception) {
            throw exception;
        }
    }

    public Date getPublisherS3FileLastModifiedDate(String key) {
        Date lastModifiedDate;
        try {
            lastModifiedDate = amazonS3.getObjectMetadata(bucketMarket, key).getLastModified();
        } catch (Exception exception) {
            throw exception;
        }
        return lastModifiedDate;
    }

    public String getImageEtag(String key) {
        String eTag;
        try {
            eTag = amazonS3.getObjectMetadata(bucketMarket, key).getETag();
        } catch (Exception exception) {
            throw exception;
        }
        return eTag;
    }

}
