package com.valoranthub.matchservice.riot.exception;

/** 404: la cuenta o la partida solicitada no existe en Riot API. */
public class RiotApiNotFoundException extends RiotApiException {

    public RiotApiNotFoundException(String message) {
        super(message, 404);
    }
}
