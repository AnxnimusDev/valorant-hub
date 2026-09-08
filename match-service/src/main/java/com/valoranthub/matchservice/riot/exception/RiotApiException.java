package com.valoranthub.matchservice.riot.exception;

import lombok.Getter;

/** Excepción base para cualquier respuesta de error de la Riot API. */
@Getter
public class RiotApiException extends RuntimeException {

    private final int statusCode;

    public RiotApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
