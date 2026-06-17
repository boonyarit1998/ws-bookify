# 📚 Bookify

> A reading tracker and booklist REST API — log the books you read, track reading progress, and organize them into custom lists.

[![CI](https://github.com/Boonyarit1998/ws-bookify/actions/workflows/ci.yml/badge.svg)](https://github.com/Boonyarit1998/ws-bookify/actions/workflows/ci.yml)
[![Live Demo](https://img.shields.io/badge/demo-online-success)](https://ws-bookify-latest.onrender.com/actuator/health)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue)
![Docker](https://img.shields.io/badge/Docker-GHCR-2496ED)

---

## 🔗 Live Demo

The API is deployed on Render (free tier) with a Supabase PostgreSQL database:

**Base URL:** https://ws-bookify-latest.onrender.com

```bash
curl https://ws-bookify-latest.onrender.com/actuator/health
# {"status":"UP"}
```

> ⏳ The free instance sleeps when idle — the first request after a while may take ~30–60s to wake up.

## Overview

**Bookify** is a RESTful backend that lets users keep track of their reading life. Users register, sign in, add books, track reading status and progress, and group books into custom booklists.

The project demonstrates a clean, layered Spring Boot architecture backed by Supabase (PostgreSQL), with self-issued JWT authentication, Flyway-managed database migrations, and a fully automated **build → test → containerize → deploy** pipeline.

## Features

- 🔐 **Authentication** — self-issued JWT (HS256); the API issues tokens on register/login and validates them as an OAuth2 resource server (stateless)
- 📖 **Book management** — full CRUD for books, scoped per user
- 🏷️ **Reading status & progress** — mark books as reading/finished and record progress
- 🗂️ **Custom booklists** — create lists and add/remove books
- 📈 **Reading statistics** — aggregate reading stats per user
- 📥 **Book import from Google Books API** *(planned)*

## Tech Stack

| Layer          | Technology                                       |
|----------------|--------------------------------------------------|
| Language       | Java 21                                           |
| Framework      | Spring Boot 4.1 (Web MVC, Data JPA, Security)     |
| Database       | PostgreSQL (hosted on Supabase)                   |
| Migrations     | Flyway                                            |
| Auth           | Self-issued JWT (HS256), OAuth2 Resource Server   |
| Build tool     | Maven (wrapper included)                          |
| Testing        | JUnit 5, Testcontainers (real PostgreSQL)         |
| Container      | Docker (multi-stage), published to GHCR           |
| CI/CD          | GitHub Actions → GHCR → Render                    |

## API Endpoints

All endpoints return a consistent envelope: `{ success, message, data, errors, path, timestamp }`.

| Method | Endpoint | Auth | Description |
|--------|----------|:----:|-------------|
| `POST` | `/api/auth/register` | — | Register and receive a JWT |
| `POST` | `/api/auth/login` | — | Log in and receive a JWT |
| `GET`  | `/api/auth/me` | ✅ | Current user profile |
| `GET`  | `/api/books` | ✅ | List books (paginated) |
| `POST` | `/api/books` | ✅ | Create a book |
| `GET`  | `/api/books/{id}` | ✅ | Get a book |
| `PUT`  | `/api/books/{id}` | ✅ | Update a book |
| `DELETE` | `/api/books/{id}` | ✅ | Delete a book |
| `GET`  | `/api/booklists` | ✅ | List booklists |
| `POST` | `/api/booklists` | ✅ | Create a booklist |
| `GET`/`PUT`/`DELETE` | `/api/booklists/{id}` | ✅ | Get / update / delete a booklist |
| `GET`/`POST` | `/api/booklists/{id}/books` | ✅ | List / add books in a booklist |
| `DELETE` | `/api/booklists/{id}/books/{bookId}` | ✅ | Remove a book from a booklist |
| `PUT`/`GET`/`DELETE` | `/api/books/{bookId}/reading` | ✅ | Set / get / clear reading status |
| `GET`  | `/api/stats/reading` | ✅ | Reading statistics |
| `GET`  | `/actuator/health` | — | Health check |

### Quick example

```bash
BASE=https://ws-bookify-latest.onrender.com

# 1. Register -> returns an accessToken
curl -X POST $BASE/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@example.com","password":"password123"}'

# 2. Call a protected endpoint with the token
curl $BASE/api/auth/me -H "Authorization: Bearer <accessToken>"
```

## Running Locally

**Prerequisites:** JDK 21, Docker (for Testcontainers and/or running the image).

```bash
# 1. Configure environment
cp .env.example .env        # then edit values (DB_*, JWT_SECRET)

# 2. Run the app (uses the Maven wrapper — no Maven install needed)
./mvnw spring-boot:run

# 3. Run the tests (spins up a real PostgreSQL via Testcontainers)
./mvnw verify
```

### Run with Docker

```bash
docker build -t bookify .
docker run --rm -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/bookify \
  -e DB_USERNAME=postgres -e DB_PASSWORD=postgres \
  -e JWT_SECRET=your-long-random-secret-at-least-32-bytes \
  bookify
```

## Deployment & CI/CD

Every push to `main` runs a fully automated pipeline in [GitHub Actions](.github/workflows/ci.yml):

```
push main ─▶ build-test ─▶ build-push-image ─▶ deploy
            (Testcontainers)   (Docker → GHCR)    (Render deploy hook)
```

1. **build-test** — compiles and runs the full test suite against a real PostgreSQL container (Testcontainers).
2. **build-push-image** — builds a multi-stage Docker image and pushes it to **GitHub Container Registry (GHCR)**, tagged with `latest` and the commit SHA.
3. **deploy** — triggers Render to pull the new image and roll out the deployment.

The image runs as a **non-root user**, uses Spring Boot's layered jars for efficient Docker caching, and tunes the JVM heap to the container's memory — suited to small free-tier instances.

## Project Structure

```
src/main/java/com/ws/bookify/
├── config/        # security, JWT, bean configuration
├── controller/    # REST endpoints — handle HTTP requests/responses
├── service/       # business logic — orchestrates use cases
├── repository/    # data access — Spring Data JPA interfaces
├── entity/        # JPA entities — database table mappings
├── dto/           # request/response data transfer objects
└── exception/     # custom exceptions & global handler
src/main/resources/
├── db/migration/  # Flyway scripts (V1__init.sql ...)
└── application.properties
```

**Request flow:** `Controller → Service → Repository → Database`

| Layer      | Responsibility                                                |
|------------|---------------------------------------------------------------|
| Controller | Expose REST endpoints, validate input, map to/from DTOs       |
| Service    | Hold business rules and transactions; coordinate repositories |
| Repository | Persist and query entities via Spring Data JPA                |

---

*Built as a portfolio project to showcase Spring Boot, REST API design, database integration with Supabase, and a containerized CI/CD deployment pipeline.*
