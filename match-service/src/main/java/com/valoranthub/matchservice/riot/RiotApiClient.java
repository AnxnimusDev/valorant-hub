package com.valoranthub.matchservice.riot;

import com.valoranthub.matchservice.riot.dto.AccountDto;
import com.valoranthub.matchservice.riot.dto.MatchDto;
import com.valoranthub.matchservice.riot.dto.MatchlistDto;
import com.valoranthub.matchservice.riot.exception.RiotApiException;
import com.valoranthub.matchservice.riot.exception.RiotApiForbiddenException;
import com.valoranthub.matchservice.riot.exception.RiotApiNotFoundException;
import com.valoranthub.matchservice.riot.exception.RiotApiRateLimitedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Cliente para ACCOUNT-V1 y VAL-MATCH-V1
 * (<a href="https://developer.riotgames.com/apis#val-match-v1">docs</a>).
 * Cada llamada pasa por un RateLimiter (20 req/s, igual que el límite por
 * defecto de una dev key de Riot) y un CircuitBreaker configurados en
 * application.yml bajo {@code resilience4j.*.instances.riotApi}.
 */
@Component
@Slf4j
public class RiotApiClient {

    private final WebClient accountWebClient;
    private final WebClient matchWebClient;
    private final RateLimiter rateLimiter;
    private final CircuitBreaker circuitBreaker;

    public RiotApiClient(
            @Qualifier("riotAccountWebClient") WebClient accountWebClient,
            @Qualifier("riotMatchWebClient") WebClient matchWebClient,
            RateLimiterRegistry rateLimiterRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry) {
        this.accountWebClient = accountWebClient;
        this.matchWebClient = matchWebClient;
        this.rateLimiter = rateLimiterRegistry.rateLimiter("riotApi");
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("riotApi");
    }

    /** ACCOUNT-V1: resuelve gameName#tagLine a puuid. */
    public Mono<AccountDto> getAccountByRiotId(String gameName, String tagLine) {
        return execute(
                accountWebClient
                        .get()
                        .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, this::mapError)
                        .bodyToMono(AccountDto.class));
    }

    /** VAL-MATCH-V1: historial de partidas (solo ids + metadatos mínimos) de un puuid. */
    public Mono<MatchlistDto> getMatchIdsByPuuid(String puuid) {
        return execute(
                matchWebClient
                        .get()
                        .uri("/val/match/v1/matchlists/by-puuid/{puuid}", puuid)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, this::mapError)
                        .bodyToMono(MatchlistDto.class));
    }

    /** VAL-MATCH-V1: detalle completo de una partida por su matchId. */
    public Mono<MatchDto> getMatchById(String matchId) {
        return execute(
                matchWebClient
                        .get()
                        .uri("/val/match/v1/matches/{matchId}", matchId)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, this::mapError)
                        .bodyToMono(MatchDto.class));
    }

    private <T> Mono<T> execute(Mono<T> call) {
        return call
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .doOnError(RiotApiException.class, e ->
                        log.warn("Riot API respondió {}: {}", e.getStatusCode(), e.getMessage()));
    }

    private Mono<? extends Throwable> mapError(ClientResponse response) {
        int status = response.statusCode().value();
        return response
                .bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> Mono.error(toException(status, body, response)));
    }

    private RiotApiException toException(int status, String body, ClientResponse response) {
        return switch (status) {
            case 404 -> new RiotApiNotFoundException("Recurso no encontrado en Riot API: " + body);
            case 403 -> new RiotApiForbiddenException("API key de Riot inválida o expirada (403): " + body);
            case 429 -> new RiotApiRateLimitedException(
                    "Rate limit de Riot API alcanzado (429): " + body, parseRetryAfter(response));
            default -> new RiotApiException("Riot API respondió %d: %s".formatted(status, body), status);
        };
    }

    private long parseRetryAfter(ClientResponse response) {
        String header = response.headers().asHttpHeaders().getFirst("Retry-After");
        if (header == null) {
            return 1L;
        }
        try {
            return Long.parseLong(header);
        } catch (NumberFormatException e) {
            return 1L;
        }
    }
}
