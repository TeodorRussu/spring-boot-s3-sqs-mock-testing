package com.s3_sqs_mock.proj.messaging;

import com.s3_sqs_mock.proj.BaseTest;
import com.s3_sqs_mock.proj.TestDataAndUtils;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class SQSListenerTest extends BaseTest {

    public static final String TESTING_S3_KEY = "images/1389f907.png";
    public static final String PATH_TO_TESTING_IMAGE = "src/test/resources/images/picture.png";

    @Autowired
    private QueueMessagingTemplate messagingTemplate;


    @BeforeEach
    void setUp() {
        amazonS3.putObject(publisherBucket, TESTING_S3_KEY, new File(PATH_TO_TESTING_IMAGE));
    }

    @AfterEach
    void tearDown() {
        cleanUpMockS3Buckets();
        s3Mock.stop();
    }



    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void message_notSupportedEntity_exceptionAndMessageToDlq() throws IOException, JSONException, ExecutionException, InterruptedException {

        String messageBodyNotSupportedEntity = TestDataAndUtils.getJsonFromResourceFile("json/messages/message_body_for_not_supported_entity__district.json");
        sendMessageWithAwsSqsClient(messageBodyNotSupportedEntity);

        //the message will produce an exception,
        // hence the message will be automatically moved to dlq,
        // after 5 attempts(can be configured in deadLettersQueue.maxReceiveCount property
        Future<Message> futures = Executors.newSingleThreadScheduledExecutor()
                .schedule(() ->
                        messagingTemplate.receive(sqsQueueName), 5, TimeUnit.SECONDS);
        Message<String> dlqMessage = futures.get();
        Assertions.assertNotNull(dlqMessage);
        JSONAssert.assertEquals(dlqMessage.getPayload(), messageBodyNotSupportedEntity, JSONCompareMode.NON_EXTENSIBLE);
    }

}