# AuraBloom

![AuraBloom banner](docs/aurabloom-banner.svg)

[![Live Site](https://img.shields.io/badge/live-sithu.minadadehiwala.com-18332C?style=for-the-badge)](https://sithu.minadadehiwala.com/)
![Java 21](https://img.shields.io/badge/Java-21-D76B4D?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-2D6F5F?style=for-the-badge)
![MySQL 8](https://img.shields.io/badge/MySQL-8-496E9C?style=for-the-badge)
![Frontend](https://img.shields.io/badge/Frontend-HTML%20%7C%20CSS%20%7C%20JS-C89D55?style=for-the-badge)

AuraBloom is a mental wellness platform built around mood tracking, journaling, meditation, time capsules, anonymous community support, personalized recommendations, and admin moderation. The project uses a `Java 21` Spring Boot backend and a handcrafted `HTML`, `CSS`, and `JavaScript` frontend served through `nginx`.

## Live Project

- Live app: [https://sithu.minadadehiwala.com/](https://sithu.minadadehiwala.com/)
- Health check: [https://sithu.minadadehiwala.com/actuator/health](https://sithu.minadadehiwala.com/actuator/health)
- GitHub repo: [https://github.com/MinadaDehiwala/sithumini-project](https://github.com/MinadaDehiwala/sithumini-project)

## Why This Repo Exists

This repository contains the production implementation of the AuraBloom proposal with:

- a fresh `backend/` built in `Java 21`
- a fresh `frontend/` built in plain `HTML`, `CSS`, and `JavaScript`
- a `Code/` directory preserved as the original reference implementation
- deployment support for a lean ARM EC2 setup

## What It Does

- User registration and login with JWT auth
- Profile management and level progression
- Mood tracking with weekly and monthly trend views
- Journal entries with rule-based sentiment and keyword extraction
- Meditation session tracking and streak statistics
- Daily wellness challenges with XP rewards
- Time capsule messages that unlock in-app later
- Anonymous community posting, comments, reactions, and abuse reports
- Risk scoring with support messages and admin-visible flags
- Admin tools for moderation, users, and recommendation content

## Stack

### Frontend

- `HTML`
- `CSS`
- `JavaScript`
- Single-page behavior handled in [frontend/app.js](frontend/app.js)

### Backend

- `Java 21`
- `Spring Boot 3.4.4`
- `Spring Security`
- `Spring Data JPA`
- `MySQL 8`
- `JWT`

### Deployment

- `AWS EC2 ARM`
- `nginx`
- `systemd`
- `Let's Encrypt`

## Architecture

```text
Browser
  -> nginx
     -> /            Static frontend (HTML, CSS, JavaScript)
     -> /api         Spring Boot API (Java 21)
                        -> MySQL 8
                        -> Risk scoring and insights
                        -> Admin and moderation workflows
```

This keeps the request flow explicit without relying on a wide Mermaid layout that can render awkwardly on GitHub.

## Repository Layout

```text
.
|-- backend/     Java 21 Spring Boot API
|-- frontend/    Static HTML, CSS, and JavaScript client
|-- scripts/     Deployment helpers and placeholders
|-- Code/        Original reference code kept for comparison
|-- docs/        GitHub-facing assets
```

## Product Areas

### Core User Experience

- Dashboard with risk score and recommendations
- Mood logging and trend summaries
- Journaling with automatic sentiment analysis
- Meditation session logging
- Daily challenge completion tracking
- Time capsule creation and unlock flow

### Community and Safety

- Anonymous community feed
- Supportive comments and reactions
- Abuse reporting
- High-risk flag generation
- Admin moderation views

### Progress and Motivation

- XP accumulation
- Levels
- Badge unlocks
- Streak tracking

## Local Development

### Prerequisites

- `Java 21`
- `Node` is not required for the frontend
- `MySQL 8`

### Run the backend

```bash
cd backend
export JAVA_HOME=/path/to/java-21
./mvnw spring-boot:run
```

The backend expects these environment variables when you want non-default values:

```bash
DB_URL=jdbc:mysql://localhost:3306/aurabloom?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=change-me
FRONTEND_URL=http://localhost:5500
```

### Run the frontend

Because the frontend is static, any simple file server works:

```bash
cd frontend
python3 -m http.server 5500
```

Then open:

```text
http://localhost:5500
```

## Key Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users/profile`
- `POST /api/moods`
- `POST /api/journals`
- `POST /api/meditations`
- `GET /api/challenges/today`
- `POST /api/time-capsules`
- `GET /api/community/posts`
- `GET /api/insights/summary`
- `GET /api/admin/overview`

## Current Status

- Live site deployed
- HTTPS enabled
- Java 21 backend running on ARM EC2
- MySQL-backed persistence enabled
- Signup and login verified against production

## Notes

- The production frontend is intentionally not React, Astro, or Vite.
- The Git-tracked frontend is just [frontend/index.html](frontend/index.html), [frontend/styles.css](frontend/styles.css), and [frontend/app.js](frontend/app.js).
- Sensitive deployment credentials are kept out of Git. The local `SERVER_ACCESS_AND_CREDENTIALS.txt` file is intentionally ignored.

## Next Steps

- Add backend test coverage for auth, moderation, and insights
- Expand UI testing across all major flows
- Add password reset delivery once email infrastructure is configured
- Improve seed data and challenge recommendation variety
