# coaching-platform

Contexto local para Claude Code al trabajar en este microservicio. Ver también el `CLAUDE.md` raíz y `docs/GUIA-IMPLEMENTACION.md` (Fase 5).

## Responsabilidad

Autenticación (JWT), matchmaking de coaches (rank similar, agentes en común, idioma, disponibilidad), sesiones de coaching y chat en tiempo real vía WebSocket (STOMP). Opera en paralelo al pipeline de análisis, sin depender de él.

## Stack

- Java 21, Spring Boot 3.3.4 (Web, Data JPA, Security, WebSocket — se añaden en Fase 5)
- Spring Security 6 + JWT (access 24h + refresh token), BCrypt
- PostgreSQL, Redis (pub/sub para WebSocket multi-instancia)
- Puerto: **8084**

## Estado actual

Esqueleto de Fase 1: solo `CoachingPlatformApplication` + actuator health. La Fase 5 se implementa empezando por Auth + JWT (verificado con Postman) antes de matchmaking y WebSocket. Ver `docs/GUIA-IMPLEMENTACION.md`.

## Convenciones

- Paquete base: `com.valoranthub.coachingplatform`
- Redis pub/sub para el chat se configura desde el primer commit de WebSocket, no como parche posterior (riesgo documentado en la guía: "WebSocket no escala sin pub/sub")
- Tests de seguridad explícitos para 401/403, y test de WebSocket con `StompClient`
- Nunca configurar CORS aquí — vive únicamente en `api-gateway`
