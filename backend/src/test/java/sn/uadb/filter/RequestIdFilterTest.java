package sn.uadb.filter;

import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.uadb.TestSupport.fromIp;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RequestIdFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void generatesUuidWhenHeaderIsAbsent() throws Exception {
        String id = mockMvc.perform(get("/api/hello").with(fromIp("10.1.0.1")))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andReturn().getResponse().getHeader("X-Request-Id");

        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    void keepsValidClientProvidedId() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.1.0.2")).header("X-Request-Id", "test-123"))
                .andExpect(header().string("X-Request-Id", "test-123"));
    }

    @Test
    void replacesInvalidClientProvidedId() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.1.0.3")).header("X-Request-Id", "bad id!"))
                .andExpect(header().string("X-Request-Id", not("bad id!")));
    }
}
