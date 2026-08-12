# ai-analysis-service

Contexto local para Claude Code al trabajar en este microservicio. Ver también el `CLAUDE.md` raíz y `docs/GUIA-IMPLEMENTACION.md` (Fase 3).

## Responsabilidad

Analiza partidas importadas por `match-service`: posicionamiento (heatmap), economía por ronda, timing de habilidades y K/D ratio. Consume `match.imported` de Kafka, persiste el resultado en MongoDB y publica `match.analysis.completed`, que dispara `recommendation-engine`.

## Stack

- Java 21, Spring Boot 3.3.4 — capa fina de integración (consumer/producer Kafka, cliente HTTP hacia el submódulo Python)
- **python-ml/** (submódulo FastAPI, Fase 3 Bloque A): los 4 analizadores pesados (PositioningAnalyzer con KMeans, EconomyAnalyzer, AbilityTimingAnalyzer, KDRatioAnalyzer) viven aquí, no en Java
- MongoDB (resultados de análisis), Kafka (consumer/producer)
- Puerto: **8082**

## Estado actual

Esqueleto de Fase 1: solo `AiAnalysisServiceApplication` + actuator health. La Fase 3 se trabaja en dos bloques: A) FastAPI + los 4 analizadores + tests pytest, B) consumer/producer Kafka del lado Java/Python + persistencia Mongo — ver `docs/GUIA-IMPLEMENTACION.md`.

## Convenciones

- Paquete base: `com.valoranthub.aianalysisservice`
- El submódulo Python vivirá en `ai-analysis-service/python-ml/` con su propio Dockerfile multistage (<500MB)
- DTOs Pydantic v2 en el lado Python; no dupliques esos modelos en Java, consúmelos vía HTTP/JSON
