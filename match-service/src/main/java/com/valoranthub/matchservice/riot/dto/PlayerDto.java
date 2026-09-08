package com.valoranthub.matchservice.riot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * players[] del payload de VAL-MATCH-V1 getMatch. {@code characterId} es el
 * uuid del agente, no su nombre legible (se resuelve vía VAL-CONTENT-V1 más
 * adelante, no en este bloque).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerDto(
        String puuid,
        String gameName,
        String tagLine,
        String teamId,
        String partyId,
        String characterId,
        PlayerStatsDto stats,
        Integer competitiveTier) {}
