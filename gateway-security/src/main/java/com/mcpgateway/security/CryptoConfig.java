package com.mcpgateway.security;

import com.mcpgateway.common.crypto.CredentialEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

    @Bean
    public CredentialEncryptor credentialEncryptor(@Value("${mcp.security.encryption.master-key}") String masterKey) {
        return new CredentialEncryptor(masterKey);
    }
}
