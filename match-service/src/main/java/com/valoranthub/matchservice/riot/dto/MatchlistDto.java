package com.valoranthub.matchservice.riot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Respuesta de VAL-MATCH-V1 GET /val/match/v1/matchlists/by-puuid/{puuid}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchlistDto(String puuid, List<MatchHistoryEntryDto> history) {}
