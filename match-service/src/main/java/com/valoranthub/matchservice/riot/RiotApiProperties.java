package com.valoranthub.matchservice.riot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración del cliente Riot API.
 *
 * @param key             API key (dev key: expira cada 24h, ver README/CLAUDE.md)
 * @param regionalRouting shard cross-game de ACCOUNT-V1: americas | europe | asia
 * @param platformRouting shard de VAL-MATCH-V1/VAL-CONTENT-V1: na | eu | ap | kr | latam | br
 */
@ConfigurationProperties(prefix = "riot-api")
public record RiotApiProperties(String key, String regionalRouting, String platformRouting) {}
