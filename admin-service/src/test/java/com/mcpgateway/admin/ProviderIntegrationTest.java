package com.mcpgateway.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpgateway.admin.dto.BootstrapRequest;
import com.mcpgateway.admin.dto.CreateCredentialRequest;
import com.mcpgateway.admin.dto.CreateProviderRequest;
import com.mcpgateway.common.domain.CredentialType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ProviderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bootstrapLoginCreateProviderAndCredential() throws Exception {
        BootstrapRequest bootstrap = new BootstrapRequest(
                "demo-org", "Demo Organization", "admin@demo.com", "password123");
        mockMvc.perform(post("/api/v1/organizations/bootstrap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bootstrap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("demo-org"));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@demo.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("accessToken").asText();

        CreateProviderRequest provider = new CreateProviderRequest("weather-api", "Weather API", "Demo provider");
        mockMvc.perform(post("/api/v1/providers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(provider)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("weather-api"));

        CreateCredentialRequest credential = new CreateCredentialRequest(
                "upstream-key", CredentialType.API_KEY, "secret-api-key-value");
        mockMvc.perform(post("/api/v1/credentials")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credential)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("upstream-key"))
                .andExpect(jsonPath("$.credentialType").value("API_KEY"));

        mockMvc.perform(get("/api/v1/providers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("weather-api"));
    }
}
