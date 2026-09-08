package com.valoranthub.matchservice.riot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Respuesta de ACCOUNT-V1 GET /riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountDto(String puuid, String gameName, String tagLine) {}
