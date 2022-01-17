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

    private String publisherBucket;
    private String cmsBucketDe;
    private String cmsBucketFr;

    public void postCmsFileToS3(String key, File file, String market) {

        String bucket = market.equals("DE") ? cmsBucketDe : cmsBucketFr;
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
            lastModifiedDate = amazonS3.getObjectMetadata(publisherBucket, key).getLastModified();
        } catch (Exception exception) {
            throw exception;
        }
        return lastModifiedDate;
    }

    public String getImageEtag(String key) {
        String eTag;
        try {
            eTag = amazonS3.getObjectMetadata(publisherBucket, key).getETag();
        } catch (Exception exception) {
            throw exception;
        }
        return eTag;
    }

}
