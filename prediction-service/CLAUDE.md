# prediction-service

Contexto local para Claude Code al trabajar en este microservicio. Ver también el `CLAUDE.md` raíz y `docs/GUIA-IMPLEMENTACION.md` (Fase 6).

## Responsabilidad

Expone bajo demanda una predicción de rango del jugador, delegando la inferencia real a `python-predictor` (servidor gRPC Python, puerto 50051) vía `PredictionGrpcClient`.

## Stack

- Java 21, Spring Boot 3.3.4 (Web — se añade `grpc-spring-boot-starter` en Fase 6)
- **python-predictor/** (submódulo Python, Fase 6): `RandomForestClassifier` entrenado con `train_model.py`, servido vía gRPC
- Contrato `prediction.proto`: `PredictRank(PlayerFeatures) → RankPrediction`
- Puerto: **8085** (REST) — el servidor gRPC Python corre en **50051**

## Estado actual

Esqueleto de Fase 1: solo `PredictionServiceApplication` + actuator health. La Fase 6 empieza por el `.proto` y el feature engineering, antes de entrenar el modelo. Meta de accuracy: ≥60% (±1 división). Ver `docs/GUIA-IMPLEMENTACION.md`.

## Convenciones

- Paquete base: `com.valoranthub.predictionservice`
- Verificar latencia gRPC (<50ms) con tests dedicados, no solo funcionalidad
- El modelo se sirve desde `python-predictor/`, entrenado con dataset Kaggle (mínimo 10.000 partidas) — ver tabla de recursos externos en la guía
