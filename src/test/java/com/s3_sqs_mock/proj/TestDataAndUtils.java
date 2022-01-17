package com.s3_sqs_mock.proj;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@UtilityClass
public class TestDataAndUtils {

    public static String getJsonFromResourceFile(String messageJsonPath) throws IOException {
        File file = new ClassPathResource(messageJsonPath).getFile();
        return new String(Files.readAllBytes(file.toPath()));
    }

    public static void mockCacheUpdatedAkamaiResponseForETagRequest(String s3ETag, RestTemplate restTemplate) {
        HttpHeaders matchingETagHeadersStub = createStubHttpHeaders(s3ETag);
        when(restTemplate.headForHeaders(anyString())).thenReturn(matchingETagHeadersStub);
    }

    public static HttpHeaders createStubHttpHeaders(String s3ETag) {
        HttpHeaders httpReturnHeadersStub = new HttpHeaders();
        httpReturnHeadersStub.add("ETag", s3ETag);
        return httpReturnHeadersStub;
    }
}
