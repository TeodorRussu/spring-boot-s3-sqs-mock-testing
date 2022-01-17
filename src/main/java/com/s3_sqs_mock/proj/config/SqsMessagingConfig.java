package com.s3_sqs_mock.proj.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.support.VisibilityHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;

import java.util.Arrays;

import static java.util.Collections.singletonList;

@Configuration
public class SqsMessagingConfig {

    @Bean
    @Autowired
    public QueueMessagingTemplate getQueueMessagingTemplate(AmazonSQSAsync amazonSqs) {
        return new QueueMessagingTemplate(amazonSqs);
    }

    /**
     * Provides a deserialization template for incoming SQS messages
     */
    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory(MessageConverter messageConverter) {

        var factory = new QueueMessageHandlerFactory();
        factory.setArgumentResolvers(singletonList(new PayloadArgumentResolver(messageConverter)));
        return factory;
    }

    /**
     * Provides JSON converter for SQS messages
     */
    @Bean
    protected MessageConverter messageConverter(ObjectMapper objectMapper) {
        var converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);

        // Serialization support:
        converter.setSerializedPayloadClass(String.class);

        // Deserialization support: (suppress "contentType=application/json" header requirement)
        converter.setStrictContentTypeMatch(false);

        return converter;
    }

    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory(
            @Autowired AmazonSQSAsync sqsClient, @Autowired MessageConverter messageConverter) {

        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
        factory.setAmazonSqs(sqsClient);

        // Uses the MappingJackson2MessageConverter object to map the payload to POJO.
        PayloadArgumentResolver payloadResolver = new PayloadArgumentResolver(messageConverter);

        // Extract the visibility data from the SQS message then gets deserialized into the Visibility object.
        VisibilityHandlerMethodArgumentResolver visibilityHandlerMethodArgumentResolver
                = new VisibilityHandlerMethodArgumentResolver("Visibility");

        factory.setArgumentResolvers(Arrays.asList(visibilityHandlerMethodArgumentResolver, payloadResolver));

        return factory;
    }
}
