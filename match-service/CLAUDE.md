# match-service

Contexto local para Claude Code al trabajar en este microservicio. Ver también el `CLAUDE.md` raíz y `docs/GUIA-IMPLEMENTACION.md` (Fase 2).

## Responsabilidad

Importa y persiste partidas de VALORANT desde la Riot Games API, expone el histórico y estadísticas agregadas al resto del sistema. Es la puerta de entrada de datos de todo el pipeline: al importar una partida publica `match.imported` en Kafka, que dispara `ai-analysis-service`.

## Stack

- Java 21, Spring Boot 3.3.4 (Web, Data JPA — se añade en Fase 2)
- PostgreSQL (persistencia), Redis (cache de summoner/match-ids)
- Flyway para migraciones
- Resilience4j (RateLimiter + CircuitBreaker) para las llamadas a Riot API
- Puerto: **8081**

## Estado actual

Esqueleto de Fase 1: solo `MatchServiceApplication` + actuator health. La lógica de negocio (entidades JPA, `RiotApiClient`, `MatchImportService`, cache Redis, endpoints REST) se implementa en **Fase 2** — ver `docs/GUIA-IMPLEMENTACION.md`.

## Convenciones

- Paquete base: `com.valoranthub.matchservice`
- Perfiles: `dev` (docker-compose local) y `prod` — activados vía `SPRING_PROFILES_ACTIVE`
- Tests: JUnit 5 + Mockito para unitarios, Testcontainers (Postgres/Redis) para integración
- No mezclar lógica de CORS/JWT aquí — eso vive únicamente en `api-gateway`
