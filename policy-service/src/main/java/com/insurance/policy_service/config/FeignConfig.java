package com.insurance.policy_service.config;

import com.insurance.policy_service.client.PartyClient;
import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${party.service.url}")
    private String partyServiceUrl;

    @Bean
    public PartyClient partyClient() {

        return Feign.builder()
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(PartyClient.class))
                .logLevel(Logger.Level.FULL)
                .target(PartyClient.class, partyServiceUrl);
    }
}
