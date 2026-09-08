package com.valoranthub.matchservice.riot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** teams[] del payload de VAL-MATCH-V1 getMatch. teamId es "Red"/"Blue" en modos bomb. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamDto(String teamId, boolean won, int roundsPlayed, int roundsWon, Integer numPoints) {}
