package com.valoranthub.matchservice.riot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** players[].stats del payload de VAL-MATCH-V1 getMatch. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerStatsDto(int score, int roundsPlayed, int kills, int deaths, int assists, long playtimeMillis) {}
