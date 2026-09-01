package com.mcpgateway.proxy;

import com.mcpgateway.common.crypto.CredentialEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyCryptoConfig {

    @Bean
    public CredentialEncryptor credentialEncryptor(@Value("${mcp.security.encryption.master-key}") String masterKey) {
        return new CredentialEncryptor(masterKey);
    }
}
