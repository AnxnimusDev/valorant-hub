# Guía de implementación — ValorantHub

Adaptación del plan original de ValorantHub a la metodología de **fases aprobadas una a una** que usamos en order-system, con Claude Design sustituyendo a Google Stitch para el diseño de UI. Es un proyecto de mucha mayor envergadura (6 meses / 12 sprints), así que aquí cada "fase" equivale a un sprint del plan original, y cada sprint se subdivide en checkpoints que apruebas antes de avanzar — igual que hicimos servicio a servicio en order-system.

---

## 0. Qué cambia respecto al plan original

| Antes (plan original) | Ahora |
|---|---|
| Diseño en Google Stitch, cuenta externa, exportar código HTML/React | **Claude Design**, wireframes generados directamente en el chat con Claude, sin salir de tu flujo de trabajo con Claude Code |
| Diseño de pantallas repartido en Sprint 9 (semana 17) | Diseño completo **antes de Sprint 1** (Fase 0), como especificación visual desde el minuto uno |
| Ejecutar sprint completo y verificar al final | Cada sprint se trabaja por **checkpoints aprobables**, como en order-system: no se avanza al siguiente bloque de tareas sin tu confirmación |
| Prompts de Claude Code sueltos por tarea | Un **prompt de arranque por sprint**, con las tareas del sprint agrupadas, para pegar en Claude Code al iniciar cada bloque |

El resto de la arquitectura, stack y checklist de prerrequisitos del plan original se mantiene íntegro — es sólido y no necesita cambios.

---

## Fase 0 — Wireframes con Claude Design (antes de Sprint 1)

**Objetivo**: tener el sistema de diseño y las 8 pantallas principales definidas antes de escribir código, para que los prompts de Claude Code en Sprint 9 (frontend) sean directos: "implementa esta pantalla ya diseñada", no "diseña e implementa a la vez".

**Cómo trabajarlo, pantalla a pantalla, en una conversación con Claude:**

1. Empieza pidiendo el **sistema de diseño** antes que ninguna pantalla:
   > "Quiero diseñar el sistema de diseño de ValorantHub antes de las pantallas. Paleta inspirada en VALORANT (rojos, negros, acentos neón: primary #E94560, bg #1A1A2E, accent #0F3460), tipografía Inter/DM Sans, componentes base: stat-card, match-card, tip-card, coach-card, badge-item, nav-bar. Muéstrame un mockup con estos componentes juntos."

2. Después, pide cada pantalla por separado, referenciando el sistema de diseño ya aprobado:
   - Login / Register
   - Dashboard principal (KDA, winrate, últimas partidas, predicción, top recomendaciones)
   - Análisis de partida (heatmap, economía por ronda, timeline de habilidades)
   - Recomendaciones (tip-cards categorizadas por prioridad)
   - Coaching (listado de coaches + chat)
   - Perfil de jugador (evolución de rango, badges, agentes más jugados)
   - Predicción de rango
   - Gamificación / Badges

3. Guarda cada mockup aprobado (captura o exporta el código del artifact) en `design/wireframes/<nombre-pantalla>.html` dentro del repo — es la referencia que usarás después en los prompts de Claude Code de Sprint 9.

4. Cuando tengas las 8 pantallas aprobadas, pasa a Sprint 1. No hace falta que el frontend se programe ahora — el objetivo de esta fase es solo la especificación visual.

**Checkpoint de salida de esta fase**: 8 wireframes + sistema de diseño guardados en `design/wireframes/`, y un primer commit `docs: wireframes iniciales con Claude Design`.

---

## 1. Checklist de prerrequisitos (resumen)

Se mantiene el checklist original casi íntegro. Cambios:

- ~~Crear cuenta en Google Stitch~~ → no necesaria, el diseño se hace en el chat con Claude
- Añade: revisar que tienes acceso a Claude en el plan que permite generar mockups/artifacts (ya lo tienes en tu interfaz de chat)

Resto sin cambios respecto al original:

- [ ] Riot Developer Portal → Production API Key (5-10 días, **pídela el día 1**)
- [ ] OpenAI o proveedor LLM equivalente + facturación con límite de gasto
- [ ] Anthropic Console (Claude Code)
- [ ] Docker Hub, GitHub (repo privado)
- [ ] Proveedor cloud / VPS decidido
- [ ] Java 21, Maven/Gradle, Python 3.11, Node 20, Docker Desktop, docker-compose v2
- [ ] Claude Code CLI instalado y autenticado
- [ ] `CLAUDE.md` raíz + `CLAUDE.md` por microservicio (ver estructura en README)
- [ ] `.claudeignore` configurado

---

## 2. Metodología de fases (aplicada a los 12 sprints)

Igual que en order-system: **un prompt de arranque por fase, apruebas el resultado, avanzas**. Aquí cada fase corresponde a un sprint de 2 semanas del plan original. No pegues los 12 prompts de golpe — trabaja uno detrás de otro.

### Fase 1 (Sprint 1) — Infraestructura base

```
Vamos a levantar la infraestructura base de ValorantHub. Necesito, en este orden,
que confirmes conmigo después de cada bloque:

1. docker-compose.yml con: postgres 16-alpine, redis 7-alpine, kafka+zookeeper
   (confluentinc), mongo 7. Healthchecks en todos. Redes valorant-backend-net y
   valorant-ml-net. Genera también .env.example con todas las variables
   (POSTGRES_*, REDIS_PASSWORD, KAFKA_BROKER, MONGO_URI, RIOT_API_KEY,
   OPENAI_API_KEY, ANTHROPIC_API_KEY, JWT_SECRET).

2. Esqueletos Spring Boot 3.x (Java 21) de: match-service, ai-analysis-service
   (luego migraremos su lógica pesada a Python/FastAPI en un submódulo),
   recommendation-engine, coaching-platform, prediction-service, api-gateway.
   Cada uno con su application.yml (perfiles dev/prod) y un CLAUDE.md local con
   contexto del servicio.

3. Proyecto frontend con Vite + React + TypeScript + Tailwind, dependencias:
   @tanstack/react-query, axios, react-router-dom, @stomp/stompjs, chart.js,
   react-chartjs-2, d3, framer-motion.

4. Makefile con targets up/down/logs/rebuild-<servicio>/clean.

5. README.md raíz (ya tengo uno preparado, dime si lo integramos o lo generamos
   juntos) y .github/workflows/ci.yml básico (lint + build de cada módulo).

Muéstrame primero la estructura de carpetas completa antes de generar código.
```

**Checkpoint de salida**: `docker compose up --build` sin errores, los 6 esqueletos compilan, CI en verde.

### Fase 2 (Sprint 2) — match-service + Riot API

```
Ahora implementamos match-service completo:

1. Migración Flyway V1__create_match_tables.sql (matches, participants, teams)
   con índices en match_id, puuid, game_start.
2. Entidades JPA Match/Participant/Team con relaciones y Lombok.
3. RiotApiClient con WebClient: getAccountByRiotId, getMatchIdsByPuuid,
   getMatchById. Manejo de 429/403/404. Resilience4j RateLimiter
   (20 req/s) + CircuitBreaker.
4. MatchImportService: importa en lotes de 5, evita duplicados, mapea a JPA.
5. Redis cache en summoner (TTL 300s) y match-ids (TTL 600s).
6. MatchController: POST import, GET listado paginado, GET stats agregadas.
7. Tests unitarios (Mockito) + integración (Testcontainers Postgres/Redis).

Ve mostrándome cada punto antes de pasar al siguiente, empezando por la
migración Flyway y las entidades.
```

**Checkpoint de salida**: importación real desde Riot API funcionando, segunda llamada usa caché (verificar en logs), tests con ≥80% cobertura.

### Fase 3 (Sprint 3-4) — ai-analysis-service (ML) + Kafka + MongoDB

```
Trabajamos ai-analysis-service en dos bloques:

BLOQUE A (Python/FastAPI):
1. Proyecto FastAPI en ai-analysis-service/python-ml/ (routers, services,
   models, core). Dockerfile multistage (<500MB).
2. DTOs Pydantic v2: MatchData, AnalysisResult.
3. Cuatro analizadores: PositioningAnalyzer (KMeans + heatmap matplotlib),
   EconomyAnalyzer, AbilityTimingAnalyzer, KDRatioAnalyzer.
4. Endpoint POST /analyze/full que ejecuta los 4 en paralelo con asyncio.gather.
5. Tests pytest (≥15 tests, casos normales + edge cases).

BLOQUE B (integración Kafka/Mongo, lado Java y Python):
6. Consumer Kafka en el servicio que escucha match.imported, llama a
   POST /analyze/full, persiste el resultado en MongoDB.
7. Productor del evento match.analysis.completed al finalizar.
8. Tests de integración con Testcontainers para Kafka y Mongo.

Empieza por el Bloque A completo, lo reviso, y luego pasamos al Bloque B.
```

**Checkpoint de salida**: partida importada dispara automáticamente su análisis vía Kafka, resultado visible en MongoDB.

### Fase 4 (Sprint 5-6) — recommendation-engine (LLM) + testing E2E

```
1. recommendation-engine consume match.analysis.completed, construye un
   prompt estructurado con el análisis (posicionamiento, economía, timing,
   K/D) y llama al LLM configurado para generar recomendaciones categorizadas
   (Posicionamiento, Economía, Habilidades, General) con prioridad.
2. Persiste recomendaciones en PostgreSQL, expone GET con filtros y paginación.
3. Tests: mock del cliente LLM, casos de fallo/timeout con fallback.
4. Testing E2E de todo el flujo Mes 1-3: importar partida → análisis →
   recomendación, verificando cada paso con datos reales de una cuenta demo.

Empieza por el diseño del prompt template para el LLM antes de escribir
el cliente HTTP.
```

**Checkpoint de salida**: flujo completo importar→analizar→recomendar verificado end-to-end con una cuenta real.

### Fase 5 (Sprint 7) — coaching-platform (auth + matchmaking + WebSocket)

```
1. Migración Flyway: users, coaches, coaching_sessions, messages, reviews.
2. Spring Security 6 + JWT (JwtTokenProvider, JwtAuthenticationFilter),
   BCrypt, expiración 24h + refresh token.
3. AuthController: register, login, refresh.
4. MatchmakingService: filtra coaches por rank similar, agentes en común,
   idioma, disponibilidad; ordena por rating.
5. CoachingSessionController: crear/aceptar/completar sesión.
6. WebSocket STOMP: ChatController, persistencia de mensajes, Redis pub/sub
   para multi-instancia.
7. Sistema de reviews con recalculo automático de rating_avg.
8. Tests de seguridad (401/403) y test de WebSocket con StompClient.

Empieza por Auth + JWT, lo verificamos con Postman, y seguimos con
matchmaking y WebSocket después.
```

**Checkpoint de salida**: login/registro funcionando, chat en tiempo real entre dos clientes de prueba.

### Fase 6 (Sprint 8) — prediction-service (ML + gRPC)

```
1. Contrato prediction.proto: PredictRank(PlayerFeatures) → RankPrediction.
2. feature_engineering.py: extrae KDA/winrate/HS%/economía/agente/tendencia
   desde PostgreSQL, exporta CSV.
3. train_model.py: RandomForestClassifier + GridSearchCV, cross-validation,
   serializa con joblib. Meta: accuracy ≥60% (±1 división).
4. Servidor gRPC Python (puerto 50051) que carga el modelo y expone predict.
5. PredictionGrpcClient en Java (grpc-spring-boot-starter) + PredictionController.
6. Integración en docker-compose, healthchecks, tests de latencia (<50ms).

Empieza generando el .proto y el feature engineering antes que el modelo.
```

**Checkpoint de salida**: predicción de rango accesible vía REST, con latencia gRPC verificada.

### Fase 7 (Sprint 9) — API Gateway + Frontend (desde wireframes de Claude Design)

```
1. Spring Cloud Gateway: rutas a los 5 microservicios, JwtAuthGatewayFilter
   (excepto /auth/** y /swagger-ui/**), rate limiting Redis (100 req/min/IP),
   CORS centralizado solo aquí.
2. Para el frontend, voy a pasarte los wireframes que ya aprobamos en Claude
   Design (carpeta design/wireframes/). Tradúcelos a componentes React con
   TypeScript y Tailwind, organizados en components/ui/ (atómicos) y
   components/features/ (compuestos), y conéctalos a los endpoints reales
   con TanStack Query: LoginPage/RegisterPage, DashboardPage,
   MatchAnalysisPage (heatmap D3, gráfico economía Chart.js),
   RecommendationsPage, CoachingPage (+ chat WebSocket), PlayerProfilePage.

Empieza por el Gateway, lo verificamos, y luego vamos pantalla a pantalla
en el frontend usando los wireframes como referencia.
```

**Checkpoint de salida**: navegación completa desde login hasta cada pantalla, con datos reales del backend, fiel a los wireframes aprobados en la Fase 0.

### Fase 8 (Sprint 10) — Testing de carga, seguridad y CI/CD

```
1. Scripts k6: import-load.js, analysis-load.js, websocket-load.js
   (metas: p99 <5s, error rate <0.1%).
2. OWASP ZAP contra el Gateway, resolver hallazgos ALTA severidad.
3. Pipeline .github/workflows/ci-cd.yml completo: lint, test (mvn+pytest+npm
   en paralelo), build-docker, security-scan (Trivy), deploy-staging.
4. Lighthouse CI en frontend (Performance ≥80, Accessibility ≥90).
5. Cobertura JaCoCo ≥75% en todos los servicios Java.

Empieza por los scripts k6 y documentamos resultados antes de pasar a CI/CD.
```

**Checkpoint de salida**: pipeline en verde de punta a punta, métricas de carga y seguridad documentadas en `docs/load-testing/` y `docs/security/`.

### Fase 9 (Sprint 11) — Gamificación + Observabilidad

```
1. Migración badge_definitions + user_badges, seed de 20 badges.
2. BadgeEvaluationService: consume eventos Kafka, evalúa criterios,
   publica badge.unlocked.
3. Notificación WebSocket en tiempo real al desbloquear.
4. Frontend: badge-grid + animación de desbloqueo (Framer Motion),
   diseño previo en Claude Design si aún no está en los wireframes de Fase 0.
5. OpenTelemetry + Jaeger (trazas end-to-end), Loki+Promtail+Grafana (logs).
6. Feature flags para funcionalidades premium.

Empieza por el catálogo de badges y el servicio de evaluación.
```

**Checkpoint de salida**: badge desbloqueado en tiempo real visible en el frontend, traza completa visible en Jaeger.

### Fase 10 (Sprint 12) — Despliegue en producción + Portfolio

```
1. VPS con Docker, SSH key auth, Nginx reverse proxy + SSL (Certbot).
2. GitHub Actions deploy job: SSH al VPS, git pull, docker compose up -d,
   health check.
3. Variables de producción fuera del repo, backup automático diario
   (pg_dump + mongodump, rotación 7 días).
4. Dominio + Cloudflare (DNS/CDN gratuito), UptimeRobot.
5. seed-demo-data.sh: 3 cuentas demo con partidas, análisis, recomendaciones,
   badges y sesiones de coaching realistas.
6. README.md de portfolio: hero, arquitectura con Mermaid, stack, challenges
   & solutions, instalación, demo en vivo.

Empieza por la configuración del VPS y Nginx+SSL antes que el resto.
```

**Checkpoint de salida**: sistema accesible públicamente vía HTTPS, con datos demo cargados y README de portfolio publicado.

---

## 3. Mapa de dependencias entre fases

Igual que en el plan original — no inicies una fase sin que su dependencia esté cerrada:

| Fase | Depende de | Habilita |
|---|---|---|
| 0 — Wireframes Claude Design | Ninguna | Fase 7 (frontend) |
| 1 — Infraestructura | Ninguna | Todas |
| 2 — match-service | 1 | 3, 6 |
| 3 — ai-analysis + Kafka | 1, 2 | 4 |
| 4 — recommendation-engine | 3 | 7 |
| 5 — coaching-platform | 1 | 7 |
| 6 — prediction-service | 2 | 7 |
| 7 — Gateway + Frontend | 0, 2-6 | 8 |
| 8 — Carga + CI/CD | 7 | 9 |
| 9 — Gamificación + Observabilidad | 8 | 10 |
| 10 — Despliegue + Portfolio | 9 | Entrega final |

---

## 4. Recursos externos (tabla actualizada)

| Recurso | Cuándo | URL |
|---|---|---|
| Riot Developer Portal (Production Key) | **Día 1** (aprobación 5-10 días) | developer.riotgames.com |
| Anthropic Console (Claude Code + Claude Design) | **Día 1** | console.anthropic.com |
| Proveedor LLM (recomendaciones) | Antes de Fase 4 | según elección |
| Docker Hub | Antes de Fase 8 | hub.docker.com |
| Dataset Valorant (Kaggle) | Antes de Fase 6 | kaggle.com |
| Dominio | Fase 10 | namecheap.com / cloudflare.com |
| Cloudflare (DNS + CDN gratis) | Fase 10 | cloudflare.com |
| VPS (Hetzner CX22 u equivalente) | Fase 10 | hetzner.com — ~€4-10/mes |
| Let's Encrypt (SSL gratis) | Fase 10 | certbot.eff.org |
| UptimeRobot | Fase 10 | uptimerobot.com |
| GitHub Actions | Desde Fase 8 | incluido en plan gratuito |

*(Se elimina la entrada de Google Stitch del plan original — sustituida por Claude Design, ya integrado en tu flujo de chat, sin cuenta ni coste adicional.)*

*(Nota sobre la Production Key, 2026-09-12: Riot no la concede para uso personal/testing — la solicitud debe dejar claro un plan de despliegue público. La aprobación no incluye RSO: el acceso a RSO se solicita aparte, solo después de aprobada la Production Key, con 30 días para implementarlo antes de perderla. Ver sección 5.)*

---

## 5. Riesgos técnicos clave (condensado)

| Riesgo | Mitigación |
|---|---|
| Riot API Dev Key expira cada 24h | Solicitar Production Key el día 1; script de renovación mientras tanto |
| VAL-MATCH-V1 (historial/detalle de partidas) exige Production Key — confirmado 2026-09-09: la Dev Key da 403 solo ahí, 200 en account-v1/val-content-v1 | Mientras no se apruebe: verificar match-service con RiotApiClient mockeado (Mockito) contra Postgres real, no bloquear el resto de bloques por esto |
| Riot no concede Production Key para "uso personal/testing"; la revisión pide demo pública o credenciales de acceso | En la solicitud, dejar claro el plan de despliegue público; tener un staging accesible listo si Riot lo pide antes de Fase 10 |
| RSO (Riot Sign-On) es obligatorio para mostrar stats de jugadores una vez hay Production Key — solo se puede solicitar después de aprobada, con 30 días para implementarlo o se pierde la key | Diseñar coaching-platform (Fase 5) con dos capas de auth desde el principio: login propio (JWT) + conexión de cuenta Riot vía RSO; el import por Riot ID libre de match-service (Fase 2) es interino, no el diseño final |
| Rate limits de Riot API | Resilience4j RateLimiter + caché Redis agresiva desde Fase 2 |
| Modelo de predicción con accuracy baja | Dataset mínimo 10.000 partidas (Kaggle), iterar features antes que complejidad del modelo |
| WebSocket no escala sin pub/sub | Redis pub/sub configurado desde el día 1 de Fase 5, no como parche posterior |
| CORS mal configurado en producción | Centralizado únicamente en el Gateway, nunca en microservicios individuales |
| Coste de infraestructura | VPS económico, capas gratuitas (Cloudflare, UptimeRobot, Let's Encrypt), apagar cuando no se use |
| Secretos expuestos en el repo | git-secrets/trufflehog en pre-commit + GitHub Secret Scanning |

---

## 6. GitHub — flujo de trabajo

Igual que en order-system pero con más disciplina de ramas dado el tamaño del proyecto:

```bash
git init
git add .
git commit -m "docs: wireframes iniciales con Claude Design"

gh repo create valoranthub --private --source=. --remote=origin
git branch -M main
git checkout -b develop
git push -u origin main develop
```

- Rama `main` protegida (require PR review), desarrollo en `develop`, features en `feature/<fase>-<nombre>`
- Un commit significativo por checkpoint de fase, no por sprint completo: `feat(match-service): importación desde Riot API + cache Redis`, `feat(ai-analysis): pipeline de 4 analizadores`, etc.
- CI (`ci.yml`) verde desde la Fase 1; CI/CD completo (`ci-cd.yml`) desde la Fase 8

---

## 7. Checklist final antes de mostrarlo en entrevista

- [ ] Los 6 microservicios + frontend levantan con `make up` sin errores
- [ ] Wireframes de Claude Design visibles en `design/wireframes/`, coherentes con el frontend final
- [ ] README de portfolio con diagrama Mermaid, stack y sección "Challenges & Solutions"
- [ ] Cuenta demo con datos realistas cargada (`seed-demo-data.sh`)
- [ ] Métricas de carga y cobertura documentadas (`docs/load-testing/`)
- [ ] Puedes explicar: por qué Kafka para el pipeline de análisis, por qué gRPC solo entre prediction-service y el modelo Python, y qué trade-offs tomaste en el diseño (CAP, consistencia eventual, poliglota persistence)
- [ ] Video demo de 3-5 min grabado, por si el despliegue no está accesible en el momento de la entrevista
