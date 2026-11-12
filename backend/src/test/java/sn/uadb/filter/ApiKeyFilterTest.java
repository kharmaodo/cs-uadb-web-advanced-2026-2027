package sn.uadb.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.uadb.TestSupport.API_KEY;
import static sn.uadb.TestSupport.API_KEY_HEADER;
import static sn.uadb.TestSupport.fromIp;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import sn.uadb.repository.ApiKeyRepository;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiKeyFilterTest {

    private static final String URL = "/api/v1/students/2300001";

    @Autowired MockMvc mockMvc;
    @Autowired ApiKeyRepository apiKeyRepository;
    @Autowired JdbcClient jdbc;

    @Test
    void missingKeyGives401() throws Exception {
        mockMvc.perform(get(URL).with(fromIp("10.5.0.1")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void wrongKeyGives401() throws Exception {
        mockMvc.perform(get(URL).with(fromIp("10.5.0.2")).header(API_KEY_HEADER, "mauvaise"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(401));
    }

    @Test
    void validKeyGives200() throws Exception {
        mockMvc.perform(get(URL).with(fromIp("10.5.0.3")).header(API_KEY_HEADER, API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("2300001"));
    }

    @Test
    void helloEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.5.0.4")))
                .andExpect(status().isOk());
    }

    @Test
    void revokedKeyIsRejectedImmediately() throws Exception {
        apiKeyRepository.insertIfAbsent("cle-a-revoquer", "revoked-owner");

        mockMvc.perform(get(URL).with(fromIp("10.5.0.5")).header(API_KEY_HEADER, "cle-a-revoquer"))
                .andExpect(status().isOk());

        jdbc.sql("UPDATE api_key SET active = 0 WHERE owner = :o").param("o", "revoked-owner").update();

        mockMvc.perform(get(URL).with(fromIp("10.5.0.5")).header(API_KEY_HEADER, "cle-a-revoquer"))
                .andExpect(status().isUnauthorized());
    }
}
