package com.valoranthub.matchservice.riot.exception;

import lombok.Getter;

/**
 * 429: Riot API ha rechazado la llamada por rate limit propio, a pesar del
 * RateLimiter local. Lleva el {@code Retry-After} (segundos) para que el
 * llamante (MatchImportService, Fase 2 Bloque 3) decida si reintenta.
 */
@Getter
public class RiotApiRateLimitedException extends RiotApiException {

    private final long retryAfterSeconds;

    public RiotApiRateLimitedException(String message, long retryAfterSeconds) {
        super(message, 429);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
