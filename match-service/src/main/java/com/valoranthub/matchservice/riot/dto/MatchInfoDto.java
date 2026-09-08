package com.valoranthub.matchservice.riot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * matchInfo del payload de VAL-MATCH-V1 GET /val/match/v1/matches/{matchId}.
 * {@code isCompleted}/{@code isRanked} conservan el nombre exacto del JSON de
 * Riot para que el binding de Jackson en el record no necesite {@code
 * @JsonProperty}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchInfoDto(
        String matchId,
        String mapId,
        String gameVersion,
        String region,
        long gameLengthMillis,
        long gameStartMillis,
        String provisioningFlowId,
        boolean isCompleted,
        String customGameName,
        String queueId,
        String gameMode,
        boolean isRanked,
        String seasonId) {}
