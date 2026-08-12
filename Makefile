SHELL := /bin/bash

# Microservicios Java (Spring Boot). El frontend no se dockeriza hasta más
# adelante (se corre con `npm run dev` en desarrollo).
SERVICES := match-service ai-analysis-service recommendation-engine \
            coaching-platform prediction-service api-gateway

# Uso: make logs SERVICE=postgres  (vacío = todos los contenedores de infra)
SERVICE ?=

# rebuild-% NO se declara aquí: en GNU Make 3.81 (la que trae macOS),
# listar nombres expandidos de un target de patrón en .PHONY hace que la
# regla de patrón deje de aplicarse ("Nothing to be done"). Al no marcarlo
# phony, sigue ejecutándose siempre igual, porque no existe un archivo
# literal llamado "rebuild-<servicio>".
.PHONY: help up down logs clean build test

help: ## Lista los targets disponibles
	@grep -E '^[a-zA-Z0-9_%-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-22s\033[0m %s\n", $$1, $$2}'

up: ## Levanta la infra (postgres/redis/kafka/zookeeper/mongo) con docker-compose
	docker compose up -d
	docker compose ps

down: ## Para y elimina los contenedores de infra (conserva los volúmenes)
	docker compose down

logs: ## Sigue los logs de infra. Filtra con: make logs SERVICE=postgres
	docker compose logs -f $(SERVICE)

clean: ## down -v (borra también volúmenes) + limpia builds locales (target/, dist/)
	docker compose down -v --remove-orphans
	@for s in $(SERVICES); do rm -rf $$s/target; done
	rm -rf frontend/dist

build: ## Compila los 6 microservicios (mvnw package) y el frontend (vite build)
	@for s in $(SERVICES); do \
		echo "==> build $$s"; \
		(cd $$s && ./mvnw -q clean package -DskipTests) || exit 1; \
	done
	cd frontend && npm run build

test: ## Corre los tests de los 6 microservicios (mvnw test)
	@for s in $(SERVICES); do \
		echo "==> test $$s"; \
		(cd $$s && ./mvnw -q clean test) || exit 1; \
	done

# rebuild-<servicio>: construye la imagen Docker standalone de ese servicio
# a partir de su propio Dockerfile. Cuando el servicio se incorpore a
# docker-compose.yml (a partir de Fase 2, servicio a servicio), este target
# pasará a apoyarse en `docker compose build/up --no-deps <servicio>`.
rebuild-%: ## Reconstruye la imagen Docker de <servicio>, ej. make rebuild-match-service
	docker build -t valoranthub/$*:dev ./$*
