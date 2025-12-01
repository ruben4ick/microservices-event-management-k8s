# Microservices Event Management System

This is a Spring Boot microservices system with a separate `auth-service` for username/password authentication and JWT token issuance, plus `user-service` and `event-management-service` for business operations. All requests go through the API gateway at `http://localhost:8080`.

## Prerequisites

- All services are running (Eureka, gateway, auth-service, user-service, event-management-service)
- Postman (or similar HTTP client) is installed
- All example requests use the gateway base URL: `http://localhost:8080`

## Postman / Token Setup

Store the JWT token in a Postman environment or collection variable named `JWT`. Use this header for all protected operations:

```http
Authorization: Bearer {{JWT}}
```

## Scenario 1 — User Registration (auth-service)

Register user credentials in `auth-service`. This creates an authentication account separate from the domain user in `user-service`.

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "demo-user",
  "password": "DemoPassword123!"
}
```

Expected response: `200 OK` with `{"message": "User registered successfully"}`

## Scenario 2 — Login and Get JWT (auth-service)

Authenticate with username/password to receive a JWT token.

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "demo-user",
  "password": "DemoPassword123!"
}
```

Expected response: `200 OK` with JSON containing the token:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Action:** Copy the `token` value and set it as the Postman variable `JWT` (environment or collection variable).

## Scenario 3 — Business Operation #1: Create Application User (user-service)

Create a domain user in `user-service`. This is separate from the auth credentials and requires authentication (non-GET request).

```http
POST http://localhost:8080/api/users
Content-Type: application/json
Authorization: Bearer {{JWT}}

{
  "username": "demo-user",
  "firstName": "Demo",
  "lastName": "User",
  "email": "demo.user@example.com",
  "password": "DemoPassword123!",
  "phoneNumber": "+380501112233",
  "dateOfBirth": "1995-01-01"
}
```

On success, the response includes the created user with an `id` field (e.g., `id: 1`). Use this `id` as `creatorId` and/or `userId` in subsequent operations.

## Scenario 4 — Business Operation #2: Create Event (event-management-service)

Create a new event using `event-management-service`. Requires JWT authentication.

```http
POST http://localhost:8080/api/events
Content-Type: application/json
Authorization: Bearer {{JWT}}

{
  "eventTitle": "Demo Concert",
  "dateTimeStart": "2030-01-01T19:00:00",
  "dateTimeEnd": "2030-01-01T22:00:00",
  "building": 1,
  "description": "Demo event created via Postman",
  "numberOfTickets": 100,
  "minAgeRestriction": 18,
  "userIds": [1],
  "creatorId": 1,
  "price": 250.0
}
```

**Note:** The `building` field must reference an existing building ID. You can create a building first via `POST /api/buildings` or use an existing one. The response includes the `id` of the new event (e.g., `id: 1`), which you'll use in the next operation.

## Scenario 5 — Business Operation #3: Buy Ticket for Event (event-management-service)

Create a ticket for the authenticated user for the previously created event.

```http
POST http://localhost:8080/api/tickets
Content-Type: application/json
Authorization: Bearer {{JWT}}

{
  "userId": 1,
  "eventId": 1,
  "username": "demo-user"
}
```

On success, the response returns a `TicketDto` with an assigned `id`.

## Optional Verification Requests (GET, No Auth Required)

Verify the results with these GET requests (no authentication required):

```http
GET http://localhost:8080/api/users
```

```http
GET http://localhost:8080/api/events
```

```http
GET http://localhost:8080/api/tickets
```

**Note:** All GET endpoints are publicly accessible. POST, PUT, PATCH, and DELETE requests require the `Authorization: Bearer {{JWT}}` header.

