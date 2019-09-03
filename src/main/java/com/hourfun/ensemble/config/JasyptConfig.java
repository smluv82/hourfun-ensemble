package com.hourfun.ensemble.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimplePBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableEncryptableProperties
@Configuration
public class JasyptConfig {
    @Bean(name="jasyptStringEncryptor")
    public StandardPBEStringEncryptor jasyptStringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimplePBEConfig config = new SimplePBEConfig();
        config.setPassword("4567rtyu");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPoolSize(1);
        encryptor.setConfig(config);

        return encryptor;
    }
}
