package com.valoranthub.matchservice.riot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Respuesta de VAL-MATCH-V1 GET /val/match/v1/matches/{matchId}. El payload
 * real incluye también {@code coaches} y {@code roundResults} (detalle
 * round-by-round + abilityCasts) — no se modelan aquí porque match-service
 * no los persiste (ver nota en V1__create_match_tables.sql); Jackson los
 * ignora gracias a @JsonIgnoreProperties.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchDto(MatchInfoDto matchInfo, List<PlayerDto> players, List<TeamDto> teams) {}
