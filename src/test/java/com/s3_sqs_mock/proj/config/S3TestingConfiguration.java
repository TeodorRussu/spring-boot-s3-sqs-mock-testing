package com.s3_sqs_mock.proj.config;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.s3_sqs_mock.proj.Application;
import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Import(Application.class)
@Profile("test")
@Slf4j
public class S3TestingConfiguration {

    public static final String S3_LOCALHOST_URL_TEMPLATE = "http://localhost:%d";
    @Value("${s3.bucket}")
    private String publisherBucket;

    @Value("${AWS_REGION}")
    private String awsRegion;
    @Value("${s3.mock-server-port}")
    private int port;


    @Bean
    AmazonS3 amazonS3() {
        /* AWS S3 client setup.
         *  withPathStyleAccessEnabled(true) trick is required to overcome S3 default
         *  DNS-based bucket access scheme
         *  resulting in attempts to connect to addresses like "bucketname.localhost"
         *  which requires specific DNS setup.
         */
        var endpoint = new EndpointConfiguration(String.format(S3_LOCALHOST_URL_TEMPLATE, port), awsRegion);
        return AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .build();
    }

    @Bean
    @Autowired
    S3Mock api(AmazonS3 amazonS3) {
        S3Mock api = new S3Mock.Builder().withPort(port).withInMemoryBackend().build();
        api.start();
        amazonS3.createBucket(publisherBucket);
        return api;
    }

}