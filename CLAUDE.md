# ValorantHub — contexto global para Claude Code

Plataforma de análisis de rendimiento, coaching y predicción de rango para jugadores de VALORANT. Ver `README.md` para arquitectura y stack completo.

## Metodología de trabajo

Este proyecto se construye **por fases aprobadas una a una**, siguiendo `docs/GUIA-IMPLEMENTACION.md`. Cada fase equivale a un sprint del plan original y se subdivide en bloques/checkpoints que el usuario aprueba antes de avanzar al siguiente — no generar varios bloques sin confirmación intermedia, y no saltar de fase sin cerrar el checkpoint de salida de la anterior (ver la tabla de dependencias entre fases en la guía, sección 3).

Los wireframes del sistema de diseño (Fase 0, Claude Design) viven en `design/wireframes/*.dc.html` y son la especificación visual vinculante para el frontend — cualquier pantalla implementada en Fase 7 debe ser fiel a su wireframe correspondiente.

## Estructura

Cada microservicio Java tiene su propio `CLAUDE.md` con contexto específico (responsabilidad, stack, puerto, estado actual, convenciones): `match-service/CLAUDE.md`, `ai-analysis-service/CLAUDE.md`, `recommendation-engine/CLAUDE.md`, `coaching-platform/CLAUDE.md`, `prediction-service/CLAUDE.md`, `api-gateway/CLAUDE.md`. Léelo antes de trabajar dentro de un servicio concreto.

## Convenciones de stack

- Java 21, Spring Boot 3.3.x, Maven (con wrapper `./mvnw`), groupId `com.valoranthub`, paquete `com.valoranthub.<nombreservicio>` (sin guiones)
- Frontend: Vite + React + TypeScript + Tailwind v4, alias `@/` → `frontend/src/`
- **CORS y JWT solo se configuran en `api-gateway`** — nunca en un microservicio individual (riesgo documentado en la guía)
- Redis pub/sub para WebSocket se configura desde el primer commit de esa función, no como parche posterior

## Git y commits

- Ramas: `main` (protegida) ← `develop` ← `feature/<fase>-<nombre>` (ej. `feature/fase1-infraestructura`)
- Un commit por bloque/checkpoint significativo, no uno gigante por fase completa
- Mensajes en formato `feat(<scope>): ...` / `docs: ...` / `fix: ...`, en español, describiendo qué se verificó
- **No añadir trailer de coautoría de Claude/Anthropic a los commits** — el único autor que debe constar es el usuario (su identidad de git ya está configurada correctamente en este repo)

## Antes de dar un bloque por cerrado

Verificar de forma real, no solo revisar código:
- Java: `./mvnw clean test` (o `make test` desde la raíz) en verde
- Frontend: `npm run build` + `npm run lint` en verde
- Infra: `make up` deja los contenedores en estado `healthy`
- Cuando aplique, arrancar el servicio/contenedor real y comprobar el comportamiento (p. ej. `/actuator/health`), no darlo por bueno solo porque compila
