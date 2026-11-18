# 🍔 BurguerCloud — Arquitectura de Microservicios Orientada a Eventos (Spring Boot 4 + Spring Cloud + JWT + Kafka DLT + Docker)

Sistema distribuido para la gestión, validación, procesamiento asíncrono y persistencia de órdenes de hamburguesas en tiempo real. Implementa **seguridad stateless con JWT** en el borde, **tolerancia a fallos con Circuit Breaker**, **reintentos exponenciales y Dead Letter Topic (DLT)** en Apache Kafka, y orquestación completa mediante **Docker Compose**.

---

## 🏗️ Arquitectura del Sistema

```text
                       ┌────────────────────────┐
                       │   Spring Cloud Config  │
                       │      (Puerto 8888)     │
                       └───────────┬────────────┘
                                   │ Configuración centralizada
                                   ▼
┌─────────┐   HTTP +   ┌──────────────────────┐    Discovery    ┌──────────────────────┐
│ Cliente │ ──JWT────► │     API Gateway      │ ◄─────────────► │    Eureka Server     │
└─────────┘            │  + Spring Security   │                 │     (Puerto 8761)    │
                       │  + Circuit Breaker   │                 └──────────┬───────────┘
                       │     (Puerto 8080)    │                            │
                       └──────────┬───────────┘                            │
                                  │ Balanceo de carga (lb://)              │
                                  ▼                                        │
                       ┌──────────────────────┐                            │
                       │  Productor Pedidos   │ ◄──────────────────────────┘
                       │     (Puerto 8081)    │
                       └──────────┬───────────┘
                                  │ Evento JSON (Tópico: pedidos-hamburguesas)
                                  ▼
                       ┌──────────────────────┐
                       │     Apache Kafka     │
                       │     (Puerto 9092)    │
                       └────┬─────────────┬───┘
                            │             │ Falla tras 3 reintentos
           Consumo normal   │             ▼
                            │    ┌────────────────────────────┐
                            │    │    Dead Letter Topic       │
                            │    │ (pedidos-hamburguesas-dlt) │
                            │    └─────────────┬──────────────┘
                            ▼                  ▼
                       ┌───────────────────────────┐       JPA        ┌──────────────────┐
                       │    Consumidor Pedidos     │ ───────────────► │    PostgreSQL    │
                       │  (@RetryableTopic + DLT)  │                  │   (Puerto 5433)  │
                       │       (Puerto 8082)       │                  └──────────────────┘
                       └───────────────────────────┘
```

---

## 🚀 Stack Tecnológico y Patrones Implementados

* **Lenguaje y Framework:** Java 21, Spring Boot 4
* **Seguridad en el Borde:** Spring Security + JSON Web Tokens (`jjwt` con firma criptográfica `HMAC-SHA256`).
* **Ecosistema Spring Cloud:**
  * **Spring Cloud Gateway (WebMVC):** Punto único de entrada, validación de tokens JWT y enrutamiento dinámico.
  * **Netflix Eureka Server:** Registro y descubrimiento dinámico de microservicios.
  * **Spring Cloud Config Server:** Administración centralizada de archivos `.yml` (`config-repo`).
  * **Resilience4j (Circuit Breaker):** Protección ante caídas del productor con respuestas controladas (*Fallback*).
* **Mensajería y Resiliencia en Eventos:**
  * **Apache Kafka (KRaft Mode):** Comunicación asíncrona desacoplada sin Zookeeper.
  * **Exponential Backoff & Dead Letter Topic (`@RetryableTopic` + `@DltHandler`):** 3 intentos automáticos con espera progresiva (`2s -> 4s`) y aislamiento de mensajes inválidos hacia `pedidos-hamburguesas-dlt` sin detener el flujo principal.
* **Persistencia:** Spring Data JPA, Hibernate 7 y PostgreSQL 16.
* **Contenedores:** Docker y Docker Compose con *Multi-Stage Builds*.

---

## 📦 Servicios y Puertos

| Servicio | Contenedor Docker | Puerto Local | Responsabilidad |
| :--- | :--- | :---: | :--- |
| **API Gateway** | `burger-gateway` | `8080` | Login (`/auth/login`), filtro JWT, enrutamiento y Circuit Breaker |
| **Productor de Pedidos** | `burger-productor` | `8081` | API REST que publica las órdenes en Kafka |
| **Consumidor de Pedidos** | `burger-consumidor` | `8082` | Valida reglas de negocio, gestiona reintentos/DLT y persiste en BD |
| **Eureka Server** | `eureka-server` | `8761` | Servidor de descubrimiento de servicios |
| **Config Server** | `config-server` | `8888` | Servidor de configuración externalizada |
| **Apache Kafka** | `kafka-broker` | `9092` | Broker de eventos y tópicos de reintento/DLT |
| **PostgreSQL** | `postgres-burger` | `5433` | Base de datos relacional (`burger_db`) |

---

## 🛠️ Cómo ejecutar el proyecto

### Prerrequisitos
* [Docker Desktop](https://docker.com) en ejecución.

### 1. Clonar el repositorio
```bash
git clone https://github.com
cd HamburguerkafkaEurekaJwt
```

### 2. Construir y levantar los 7 contenedores
```bash
docker compose up -d --build
```
*(Espera ~30 segundos a que los microservicios completen su registro en Eureka: `http://localhost:8761`).*

---

## 🧪 Guía de Pruebas (End-to-End)

### 1. Verificar bloqueo sin autenticación (`403 Forbidden`)
```bash
curl -i -X POST http://localhost:8080/api/pedidos \
-H "Content-Type: application/json" \
-d '{"id": "ORD-HACK", "cliente": "Intruso", "tipoHamburguesa": "Doble", "ingredienteExtra": "Tocino", "precio": 180.0}'
```

### 2. Obtener Token JWT (`POST /auth/login`)
```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"username": "admin", "password": "burger123"}'
```

### 3. Enviar un pedido válido con Token JWT
*(Reemplaza `TU_TOKEN_JWT` por el token obtenido en el paso anterior).*
```bash
curl -X POST http://localhost:8080/api/pedidos \
-H "Content-Type: application/json" \
-H "Authorization: Bearer TU_TOKEN_JWT" \
-d '{"id": "ORD-01", "cliente": "Catalina", "tipoHamburguesa": "Triple Bacon", "ingredienteExtra": "Queso Cheddar", "precio": 230.0}'
```

### 4. Probar Reintentos y Dead Letter Topic (Orden inválida con piña)
```bash
curl -X POST http://localhost:8080/api/pedidos \
-H "Content-Type: application/json" \
-H "Authorization: Bearer TU_TOKEN_JWT" \
-d '{"id": "ORD-MALA", "cliente": "Villano", "tipoHamburguesa": "Hawaiana", "ingredienteExtra": "piña", "precio": 150.0}'
```
Revisa los logs del consumidor para observar los 3 reintentos y el envío al `@DltHandler`:
```bash
docker logs burger-consumidor --tail 30
```

### 5. Consultar pedidos guardados en PostgreSQL
```bash
docker exec -it postgres-burger psql -U admin -d burger_db -c "SELECT * FROM pedidos;"
```

---

## 🧹 Detener el entorno
```bash
docker compose down
```
