package com.s3_sqs_mock.proj;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.s3_sqs_mock.proj.config.DataSourceTestConfiguration;
import com.s3_sqs_mock.proj.config.S3TestingConfiguration;
import com.s3_sqs_mock.proj.config.S3Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {Application.class, DataSourceTestConfiguration.class, S3TestingConfiguration.class})
@Slf4j
@ActiveProfiles("test")
public abstract class BaseTest {
    @Autowired
    protected S3Utils s3Utils;

    @Autowired
    protected S3Mock s3Mock;

    @Autowired
    protected AmazonS3 amazonS3;

    @Autowired
    protected AmazonSQS sqsClient;

    @Value("${s3.bucket}")
    protected String publisherBucket;

    @Value("${sqs.changes-queue}")
    protected String sqsQueueName;

    @Value("${sqs.changes-queue-dlq}")
    protected String sqsQueueDlqName;

    @Autowired
    protected ObjectMapper mapper;

    protected void sendMessageWithAwsSqsClient(String messageBody) {
        var messageAttribute = new MessageAttributeValue()
                .withStringValue("application/json")
                .withDataType("String");

        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("contentType", messageAttribute);
        var sendMessageStandardQueue = new SendMessageRequest()
                .withQueueUrl(sqsClient.getQueueUrl(sqsQueueName).getQueueUrl())
                .withMessageBody(messageBody)
                .withDelaySeconds(0)
                .withMessageAttributes(attributes);

        sqsClient.sendMessage(sendMessageStandardQueue);
    }

    protected void cleanUpMockS3Buckets() {
        Stream.of(publisherBucket)
                .forEach(this::cleanupBucket);
    }

    protected void cleanupBucket(String bucket) {
        List<String> keys = amazonS3.listObjects(bucket).getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
        for (String key : keys) {
            amazonS3.deleteObject(bucket, key);
        }
    }

}
