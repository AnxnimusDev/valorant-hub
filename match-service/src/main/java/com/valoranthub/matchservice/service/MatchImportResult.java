package com.valoranthub.matchservice.service;

import java.util.List;

/** Resumen de una operación de importación completa (todas las partidas nuevas del puuid). */
public record MatchImportResult(int imported, int duplicates, int failed, List<MatchImportOutcome> outcomes) {

    public static MatchImportResult from(List<MatchImportOutcome> outcomes) {
        int imported = 0;
        int duplicates = 0;
        int failed = 0;
        for (MatchImportOutcome outcome : outcomes) {
            switch (outcome.status()) {
                case IMPORTED -> imported++;
                case DUPLICATE -> duplicates++;
                case FAILED -> failed++;
            }
        }
        return new MatchImportResult(imported, duplicates, failed, outcomes);
    }
}
