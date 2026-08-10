# recommendation-engine

Contexto local para Claude Code al trabajar en este microservicio. Ver también el `CLAUDE.md` raíz y `docs/GUIA-IMPLEMENTACION.md` (Fase 4).

## Responsabilidad

Consume `match.analysis.completed` de Kafka, construye un prompt estructurado a partir del análisis (posicionamiento, economía, timing, K/D) y llama al LLM configurado para generar recomendaciones categorizadas (Posicionamiento, Economía, Habilidades, General) con prioridad. Persiste en PostgreSQL y expone `GET` con filtros y paginación.

## Stack

- Java 21, Spring Boot 3.3.4 (Web, Data JPA — se añade en Fase 4)
- PostgreSQL, Kafka (consumer)
- Cliente HTTP hacia el proveedor LLM configurado (OpenAI o equivalente)
- Puerto: **8083**

## Estado actual

Esqueleto de Fase 1: solo `RecommendationEngineApplication` + actuator health. La lógica de negocio se implementa en **Fase 4** — empezar por el diseño del prompt template antes del cliente HTTP del LLM. Ver `docs/GUIA-IMPLEMENTACION.md`.

## Convenciones

- Paquete base: `com.valoranthub.recommendationengine`
- El cliente LLM debe mockearse en tests unitarios; cubrir explícitamente casos de fallo/timeout con fallback
- No hardcodear el prompt template en el cliente HTTP — extraerlo a un componente/plantilla propio, reutilizable y testeable de forma aislada
