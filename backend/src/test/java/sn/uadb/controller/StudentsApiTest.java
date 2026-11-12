package sn.uadb.controller;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentsApiTest {

    @Autowired
    MockMvc mockMvc;

    private ResultActions call(String matricule, String ip) throws Exception {
        return mockMvc.perform(get("/api/v1/students/{m}", matricule)
                .with(fromIp(ip)).header(API_KEY_HEADER, API_KEY));
    }

    @Test
    void housedStudentIsReturnedWithAccommodation() throws Exception {
        call("2300001", "10.7.0.1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastname").value("Diop"))
                .andExpect(jsonPath("$.ufr").value("SATIC"))
                .andExpect(jsonPath("$.logementCrous.pavillon").value("B"))
                .andExpect(jsonPath("$.logementCrous.numeroChambre").value("102"))
                .andExpect(jsonPath("$.logementCrous.lit").value("A"));
    }

    @Test
    void studentWithoutAccommodationHasNoLogement() throws Exception {
        call("2200003", "10.7.0.2")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("2200003"))
                .andExpect(jsonPath("$.logementCrous").doesNotExist());
    }

    @Test
    void unknownStudentGives404InUniformFormat() throws Exception {
        call("9999999", "10.7.0.3")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(404))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void invalidMatriculeGives400InUniformFormat() throws Exception {
        call("abc", "10.7.0.4")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void sqlInjectionAttemptIsRejectedAsInvalidFormat() throws Exception {
        call("1' OR '1'='1", "10.7.0.5")
                .andExpect(status().isBadRequest());
    }
}
