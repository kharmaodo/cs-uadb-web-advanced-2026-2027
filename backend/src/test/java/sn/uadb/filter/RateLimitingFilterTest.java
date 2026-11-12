package sn.uadb.filter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.uadb.TestSupport.fromIp;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = BackendApplication.class, properties = "app.rate-limit.max-requests-per-minute=4")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitingFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void fifthRequestInTheSameMinuteIsRejected() throws Exception {
        for (int i = 1; i <= 4; i++) {
            mockMvc.perform(get("/api/hello").with(fromIp("10.6.0.1")))
                    .andExpect(status().isOk());
        }

        String retryAfter = mockMvc.perform(get("/api/hello").with(fromIp("10.6.0.1")))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.error").value(429))
                .andExpect(jsonPath("$.message").value("Rate limit exceeded. Please try again later."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn().getResponse().getHeader("Retry-After");

        long seconds = Long.parseLong(retryAfter);
        assertTrue(seconds >= 1 && seconds <= 60, "Retry-After doit être entre 1 et 60 s, reçu " + seconds);
    }

    @Test
    void limitIsPerIpAddress() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/hello").with(fromIp("10.6.0.2")));
        }
        // La première IP est bloquée, une autre ne l'est pas
        mockMvc.perform(get("/api/hello").with(fromIp("10.6.0.3")))
                .andExpect(status().isOk());
    }
}
