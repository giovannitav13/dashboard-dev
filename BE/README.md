# Dashboard Backend - Microservices Architecture

Architettura a microservizi per il dashboard con Spring Boot.

## Struttura

- **api-gateway** (porta 8080): Gateway principale che riceve tutte le richieste dal FE, valida i token e instrada alle richieste ai microservizi
- **auth-service** (porta 8081): Servizio per autenticazione, registrazione e recupero password
- **projects-service** (porta 8082): Servizio per la gestione dei progetti

## Prerequisiti

- Java 17+
- Maven 3.6+
- Docker e Docker Compose

## Setup

### 1. Avvia il database PostgreSQL

```bash
cd BE
docker-compose up -d
```

Questo creerà:
- Database `authdb` per auth-service
- Database `projectsdb` per projects-service
- Porta: 5413

### 2. Avvia i microservizi

In ordine:

#### Auth Service
```bash
cd auth-service
mvn spring-boot:run
```

#### Projects Service
```bash
cd projects-service
mvn spring-boot:run
```

#### API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

## API Endpoints

### Tramite API Gateway (porta 8080)

Tutte le richieste dal frontend devono essere fatte alla porta 8080.

#### Autenticazione
- `POST /api/auth/register` - Registrazione (pubblica)
- `POST /api/auth/login` - Login (pubblica)
- `POST /api/auth/password-recovery` - Richiesta recupero password (pubblica)
- `POST /api/auth/reset-password` - Reset password (pubblica)
- `POST /api/auth/validate-token` - Validazione token (interna)

#### Progetti (richiede autenticazione)
- `POST /api/projects` - Crea progetto
- `PUT /api/projects/{id}` - Aggiorna progetto (solo owner)
- `GET /api/projects` - Lista progetti (solo quelli di cui l'utente è owner o collaborator)
- `GET /api/projects/{id}` - Dettaglio progetto (solo se l'utente è owner o collaborator)

### Headers richiesti per progetti

Tutte le richieste ai progetti richiedono:
```
Authorization: Bearer <token>
```

Il gateway valida il token e aggiunge automaticamente:
```
X-User-Email: <email>
```

## Note

- Il gateway valida automaticamente i token chiamando auth-service
- Solo gli endpoint di autenticazione sono pubblici
- Tutti gli altri endpoint richiedono un token valido
- I progetti vengono filtrati automaticamente per mostrare solo quelli accessibili all'utente
