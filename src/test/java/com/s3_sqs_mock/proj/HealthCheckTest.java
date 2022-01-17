package com.s3_sqs_mock.proj;


import com.s3_sqs_mock.proj.config.DataSourceTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {DataSourceTestConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HealthCheckTest {

    @Autowired
    protected MockMvc mvc;

    @Test
    void healthCheck_exists() throws Exception {
        mvc.perform(get("/app/health")).andExpect(status().isOk());
    }

    @Test
    void info_exists() throws Exception {
        mvc.perform(get("/app/info")).andExpect(status().isOk());
    }
}
