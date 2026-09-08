package com.valoranthub.matchservice.riot.exception;

/** 403: API key inválida, expirada o sin permisos para el recurso pedido. */
public class RiotApiForbiddenException extends RiotApiException {

    public RiotApiForbiddenException(String message) {
        super(message, 403);
    }
}
