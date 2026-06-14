# 📚 Bookify

> A reading tracker and booklist web service — log the books you read, rate and review them, and organize them into custom lists.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue)

---

## Overview

**Bookify** is a RESTful backend service that lets users keep track of their reading life. Users can add books, mark their reading status (want to read / reading / finished), leave ratings and reviews, track reading progress, and group books into custom booklists that can be kept private or shared publicly.

The project was built to demonstrate a clean, layered Spring Boot architecture backed by a Supabase (PostgreSQL) database, with JWT-based authentication and database migrations managed through Flyway.

## Features

- 🔐 **Authentication** — secure sign-up and login via Supabase Auth; the API validates JWTs as an OAuth2 resource server
- 📖 **Book management** — add books manually or import them from the Google Books API
- 🏷️ **Reading status & reviews** — mark books as *want to read*, *reading*, or *finished*, with ratings and written reviews
- 📊 **Progress tracking** — record the current page and see completion percentage
- 🗂️ **Custom booklists** — create multiple lists, add books to them, and optionally make them public
- 📈 **Reading statistics** — books finished per year and other aggregate stats *(planned)*

## Tech Stack

| Layer            | Technology                                  |
|------------------|---------------------------------------------|
| Language         | Java 21                                     |
| Framework        | Spring Boot 3.1.0 (Web, Data JPA, Security) |
| Database         | PostgreSQL (hosted on Supabase)             |
| Migrations       | Flyway                                      |
| Authentication   | Supabase Auth + JWT validation              |
| Build tool       | Maven                                       |
| Testing          | JUnit 5, Testcontainers                     |
| External API     | Google Books API *(optional)*               |

## Project Structure

```
src/main/java/com/yourname/bookify/
├── config/        # security, CORS, beans
├── auth/          # JWT validation
├── book/          # controller, service, repository, entity, dto
├── booklist/
└── reading/
src/main/resources/
├── db/migration/  # Flyway scripts (V1__init.sql ...)
└── application.yml
```

---

*Built as a portfolio project to showcase Spring Boot, REST API design, and database integration with Supabase.*
