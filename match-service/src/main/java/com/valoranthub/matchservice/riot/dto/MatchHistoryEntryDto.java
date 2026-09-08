package com.valoranthub.matchservice.riot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Entrada de matchlist.history[] — no trae el detalle de la partida, solo su id. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchHistoryEntryDto(String matchId, long gameStartTimeMillis, String teamId, String queueId) {}
