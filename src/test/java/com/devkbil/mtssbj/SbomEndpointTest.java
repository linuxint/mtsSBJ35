package com.devkbil.mtssbj;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SbomEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSbomEndpoint() throws Exception {
        mockMvc
            .perform(get("/actuator/sbom"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ids").isArray())
            .andExpect(
                jsonPath("$.ids", org.hamcrest.Matchers.containsInAnyOrder("jvm", "application")));

        System.out.println("[DEBUG_LOG] SBOM 엔드포인트 테스트 실행");
    }

    @Test
    public void testSbomJvmEndpoint() throws Exception {
        mockMvc
            .perform(get("/actuator/sbom/jvm"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.components[0].name").value("OpenJDK"))
            .andExpect(jsonPath("$.components[1].name").value("spring-boot"));

        System.out.println("[DEBUG_LOG] SBOM JVM 엔드포인트 테스트 실행");
    }

    @Test
    public void testSbomApplicationEndpoint() throws Exception {
        mockMvc
            .perform(get("/actuator/sbom/application"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.components[0].name").value("aniso8601"))
            .andExpect(jsonPath("$.components[1].name").value("click"));

        System.out.println("[DEBUG_LOG] SBOM Application 엔드포인트 테스트 실행");
    }
}
