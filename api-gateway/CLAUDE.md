# api-gateway

Contexto local para Claude Code al trabajar en este microservicio. Ver también el `CLAUDE.md` raíz y `docs/GUIA-IMPLEMENTACION.md` (Fase 7).

## Responsabilidad

Punto de entrada único al sistema: enrutamiento a los 5 microservicios de negocio, `JwtAuthGatewayFilter` (excepto `/auth/**` y `/swagger-ui/**`), rate limiting con Redis (100 req/min/IP) y **CORS centralizado únicamente aquí** — nunca en microservicios individuales (riesgo documentado en la guía).

## Stack

- Java 21, Spring Boot 3.3.4
- **Spring Cloud Gateway** (reactivo, sobre WebFlux/Netty) — se añade en Fase 7
- Redis (rate limiting)
- Puerto: **8080**

## ⚠️ Nota importante para Fase 7

Este esqueleto usa `spring-boot-starter-web` (servlet/Tomcat) solo para tener un arranque mínimo homogéneo con el resto de servicios en Fase 1. **Spring Cloud Gateway es reactivo** y requiere `spring-cloud-starter-gateway` (que trae WebFlux/Netty) — ambos modelos de servidor no coexisten en la misma app. Al empezar la Fase 7:

1. Quitar `spring-boot-starter-web` del `pom.xml`
2. Añadir `spring-cloud-starter-gateway` + el BOM de Spring Cloud correspondiente a Spring Boot 3.3.4
3. Reescribir `application.yml` con las rutas (`spring.cloud.gateway.routes`)

## Estado actual

Esqueleto de Fase 1: solo `ApiGatewayApplication` + actuator health, sobre Spring MVC (ver nota arriba). Ver `docs/GUIA-IMPLEMENTACION.md`.

## Convenciones

- Paquete base: `com.valoranthub.apigateway`
- Todas las rutas de negocio pasan por aquí; ningún microservicio debe exponerse directamente al frontend en producción
