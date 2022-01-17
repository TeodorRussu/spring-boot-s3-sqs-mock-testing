package com.s3_sqs_mock.proj.config;


import com.s3_sqs_mock.proj.Application;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@Import(Application.class)
@Profile("test")
public class DataSourceTestConfiguration {

    @Bean(destroyMethod = "shutdown")
    public EmbeddedDatabase deDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScripts("db-schema.sql")
                .build();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {

        ClientHttpRequestFactory clientHttpRequestFactory = mock(ClientHttpRequestFactory.class);
        RestTemplateBuilder rtb = mock(RestTemplateBuilder.class);
        RestTemplate restTemplate = mock(RestTemplate.class);

        when(restTemplate.getRequestFactory()).thenReturn(clientHttpRequestFactory);

        when(rtb.build()).thenReturn(restTemplate);
        return rtb;
    }

}