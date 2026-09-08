package com.valoranthub.matchservice.service;

import com.valoranthub.matchservice.repository.MatchRepository;
import com.valoranthub.matchservice.riot.RiotApiClient;
import com.valoranthub.matchservice.riot.dto.MatchHistoryEntryDto;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Importa el historial de partidas de un jugador: resuelve puuid, separa las
 * partidas que ya tenemos (se reportan como DUPLICATE sin gastar llamadas a
 * Riot API en ellas) de las nuevas, y trae el detalle de estas últimas en
 * lotes de 5 en paralelo (concurrencia acotada sobre el Flux, no tandas
 * secuenciales discretas — el RateLimiter de RiotApiClient sigue siendo el
 * límite duro de 20 req/s).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchImportService {

    private static final int IMPORT_CONCURRENCY = 5;

    private final RiotApiClient riotApiClient;
    private final MatchRepository matchRepository;
    private final MatchPersistenceService matchPersistenceService;

    public Mono<MatchImportResult> importMatchesForRiotId(String gameName, String tagLine) {
        return riotApiClient
                .getAccountByRiotId(gameName, tagLine)
                .flatMap(account -> importMatchesForPuuid(account.puuid()));
    }

    public Mono<MatchImportResult> importMatchesForPuuid(String puuid) {
        return riotApiClient
                .getMatchIdsByPuuid(puuid)
                .map(matchlist -> matchlist.history() != null ? matchlist.history() : List.<MatchHistoryEntryDto>of())
                .map(history -> history.stream().map(MatchHistoryEntryDto::matchId).distinct().toList())
                .flatMap(this::partitionByExisting)
                .flatMapMany(partition -> Flux.merge(
                        Flux.fromIterable(partition.existingIds()).map(MatchImportOutcome::duplicate),
                        Flux.fromIterable(partition.newIds()).flatMap(this::importSingleMatch, IMPORT_CONCURRENCY)))
                .collectList()
                .map(MatchImportResult::from);
    }

    /** ids ya persistidos vs. nuevos, sin gastar llamadas a Riot API para saberlo. */
    private Mono<MatchIdPartition> partitionByExisting(List<String> matchIds) {
        if (matchIds.isEmpty()) {
            return Mono.just(new MatchIdPartition(List.of(), List.of()));
        }
        return Mono.fromCallable(() -> new HashSet<>(matchRepository.findExistingMatchIds(matchIds)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(existing -> new MatchIdPartition(
                        matchIds.stream().filter(id -> !existing.contains(id)).toList(),
                        matchIds.stream().filter(existing::contains).toList()));
    }

    private Mono<MatchImportOutcome> importSingleMatch(String matchId) {
        return riotApiClient
                .getMatchById(matchId)
                .flatMap(dto -> Mono.fromCallable(() -> matchPersistenceService.saveIfNew(dto))
                        .subscribeOn(Schedulers.boundedElastic()))
                .map(saved -> saved.isPresent() ? MatchImportOutcome.imported(matchId) : MatchImportOutcome.duplicate(matchId))
                .onErrorResume(e -> {
                    log.warn("No se pudo importar la partida {}: {}", matchId, e.getMessage());
                    return Mono.just(MatchImportOutcome.failed(matchId, e.getMessage()));
                });
    }

    private record MatchIdPartition(List<String> newIds, List<String> existingIds) {}
}
