package com.valoranthub.matchservice.service;

/** Resultado de intentar importar una partida concreta. */
public record MatchImportOutcome(String matchId, Status status, String errorMessage) {

    public enum Status {
        IMPORTED,
        DUPLICATE,
        FAILED
    }

    public static MatchImportOutcome imported(String matchId) {
        return new MatchImportOutcome(matchId, Status.IMPORTED, null);
    }

    public static MatchImportOutcome duplicate(String matchId) {
        return new MatchImportOutcome(matchId, Status.DUPLICATE, null);
    }

    public static MatchImportOutcome failed(String matchId, String errorMessage) {
        return new MatchImportOutcome(matchId, Status.FAILED, errorMessage);
    }
}
