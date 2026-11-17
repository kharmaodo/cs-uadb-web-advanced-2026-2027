package sn.uadb.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static sn.uadb.TestSupport.fromIp;

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
class SecurityHeadersFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void addsSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.3.0.1")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Cache-Control", "no-store"));
    }

    @Test
    void hstsIsAbsentOverPlainHttp() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.3.0.2")))
                .andExpect(header().doesNotExist("Strict-Transport-Security"));
    }

    @Test
    void hstsIsPresentOverHttps() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.3.0.3")).secure(true))
                .andExpect(header().exists("Strict-Transport-Security"));
    }
}
