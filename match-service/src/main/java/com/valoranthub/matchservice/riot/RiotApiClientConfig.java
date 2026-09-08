package com.valoranthub.matchservice.riot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(RiotApiProperties.class)
public class RiotApiClientConfig {

    /** Cliente para ACCOUNT-V1 (cross-game): shard americas/europe/asia. */
    @Bean
    public WebClient riotAccountWebClient(WebClient.Builder builder, RiotApiProperties properties) {
        return builder
                .baseUrl("https://%s.api.riotgames.com".formatted(properties.regionalRouting()))
                .defaultHeader("X-Riot-Token", properties.key())
                .build();
    }

    /** Cliente para VAL-MATCH-V1: shard na/eu/ap/kr/latam/br. */
    @Bean
    public WebClient riotMatchWebClient(WebClient.Builder builder, RiotApiProperties properties) {
        return builder
                .baseUrl("https://%s.api.riotgames.com".formatted(properties.platformRouting()))
                .defaultHeader("X-Riot-Token", properties.key())
                .build();
    }
}
