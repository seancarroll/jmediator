package com.seanthomascarroll.jmediator.sample.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SampleSpringControllerTest {

    @Inject
    private MockMvc mvc;

    @Test
    void helloShouldReturnMessage() throws Exception {
        mvc.perform(post("/").param("name", "Sean"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello Sean"));
    }

}
