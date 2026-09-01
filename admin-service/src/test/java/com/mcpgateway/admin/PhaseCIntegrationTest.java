package com.mcpgateway.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpgateway.admin.dto.BootstrapRequest;
import com.mcpgateway.admin.dto.CreateEndpointRequest;
import com.mcpgateway.admin.dto.CreateProviderRequest;
import com.mcpgateway.admin.dto.CreateToolContractRequest;
import com.mcpgateway.admin.dto.CreateVersionRequest;
import com.mcpgateway.common.domain.ContractSource;
import com.mcpgateway.common.domain.TransportType;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class PhaseCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String orgSlug;

    @BeforeEach
    void bootstrapAndLogin() throws Exception {
        orgSlug = "phase-c-" + UUID.randomUUID().toString().substring(0, 8);
        String email = orgSlug + "@test.com";
        BootstrapRequest bootstrap = new BootstrapRequest(
                orgSlug, "Phase C Org", email, "password123");
        mockMvc.perform(post("/api/v1/organizations/bootstrap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bootstrap)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    @Test
    void publishWithValidationUsageAndFederationPeers() throws Exception {
        CreateProviderRequest provider = new CreateProviderRequest("api-one", "API One", "Test");
        MvcResult providerResult = mockMvc.perform(post("/api/v1/providers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(provider)))
                .andExpect(status().isOk())
                .andReturn();
        String providerId = objectMapper.readTree(providerResult.getResponse().getContentAsString())
                .get("id").asText();

        CreateVersionRequest versionReq = new CreateVersionRequest("1.0.0", "2025-11-25", "Initial");
        MvcResult versionResult = mockMvc.perform(post("/api/v1/providers/" + providerId + "/versions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(versionReq)))
                .andExpect(status().isOk())
                .andReturn();
        String versionId = objectMapper.readTree(versionResult.getResponse().getContentAsString())
                .get("id").asText();

        CreateEndpointRequest endpoint = new CreateEndpointRequest(
                TransportType.STREAMABLE_HTTP, "http://localhost:9999/mcp", "/health", 5000, true);
        mockMvc.perform(post("/api/v1/providers/" + providerId + "/versions/" + versionId + "/endpoints")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpoint)))
                .andExpect(status().isOk());

        CreateToolContractRequest tool = new CreateToolContractRequest(
                "get_weather",
                "Weather tool",
                "{\"type\":\"object\",\"properties\":{\"city\":{\"type\":\"string\"}}}",
                "{}",
                ContractSource.MANUAL);
        mockMvc.perform(post("/api/v1/providers/" + providerId + "/versions/" + versionId + "/tools")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tool)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/providers/" + providerId + "/versions/" + versionId + "/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        mockMvc.perform(post("/api/v1/providers/" + providerId + "/versions/" + versionId + "/publish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        mockMvc.perform(get("/api/v1/usage/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(1));

        String peerBody = "{\"slug\":\"peer-hub\",\"displayName\":\"Peer Hub\",\"peerUrl\":\"http://localhost:8081\"}";
        mockMvc.perform(post("/api/v1/federation-peers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(peerBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("peer-hub"));

        mockMvc.perform(get("/api/v1/federation-peers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("peer-hub"));
    }

    @Test
    void publishBlockedWhenValidationFails() throws Exception {
        CreateProviderRequest provider = new CreateProviderRequest("api-two", "API Two", "Test");
        MvcResult providerResult = mockMvc.perform(post("/api/v1/providers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(provider)))
                .andExpect(status().isOk())
                .andReturn();
        String providerId = objectMapper.readTree(providerResult.getResponse().getContentAsString())
                .get("id").asText();

        MvcResult versionResult = mockMvc.perform(post("/api/v1/providers/" + providerId + "/versions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateVersionRequest("2.0.0", null, null))))
                .andExpect(status().isOk())
                .andReturn();
        String versionId = objectMapper.readTree(versionResult.getResponse().getContentAsString())
                .get("id").asText();

        CreateEndpointRequest endpoint = new CreateEndpointRequest(
                TransportType.STREAMABLE_HTTP, "http://localhost:9999/mcp", "/health", 5000, true);
        mockMvc.perform(post("/api/v1/providers/" + providerId + "/versions/" + versionId + "/endpoints")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpoint)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/providers/" + providerId + "/versions/" + versionId + "/publish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());
    }
}
