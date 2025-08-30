# 🪪 iTicket — Spring Boot Event Ticketing Platform API

An Event Ticketing REST API built with Spring Boot, Supabase(as DB and AuthProvider), and Spring Security.
It supports event publishing, ticket purchasing, QR code generation, and validation — all accessible via Swagger UI.

> 📌 This project is based on a solution originally developed by [Devtiro](https://www.youtube.com/@devtiro). I've implemented my own version with some changes and improvements.

---

## 🚀 Features

- 🎟️ Event and Ticket Management
- 🛒 Purchase tickets
- 🔐 Role-based access control (Attendee, Organizer, Staff) via Supabase
- 📲 QR Code generation for purchased tickets
- ✅ Validate ticket entry using QR codes
- 📦 DTO-based API layer
- 📖 API documentation with Swagger UI

---

## 📂 Project Structure

```bash
├── config/
├── controller/
├── domain/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── entities/
│   └── enums/
├── exceptions/
├── filters/
├── mappers/
├── repository/
└── service/
```

## 📸 API Preview

After starting backend go to http://localhost:8080/swagger-ui/index.html

SupabaseAuth API Structure:

#### POST https://<PROJECT_REF>.supabase.co/auth/v1/token?grant_type=password

```json
headers:
"apikey":"SUPABSE_ANON_KEY"
"Content-Type": "application/json"
{
  "email": "mike.organizer@gmail.com",
  "password": "Organizer@123"
}
```

#### POST https://<PROJECT_REF>.supabase.co/auth/v1/signup
```json
headers:
"apikey":"SUPABSE_ANON_KEY",
"Content-Type": "application/json"
{
  "email": "mike.organizer@gmail.com",
  "password": "Organizer@123",
    "data"{
      "name": "Mike Brown",
      "role": "organizer"
  }
}
```

#### POST https://<PROJECT_REF>.supabase.co/auth/v1/logout
```json
headers:
"apikey":"SUPABSE_ANON_KEY",
"Authorization": "Bearer ${access_token}"
```

#### application.properties structure
```.env
spring.application.name=iTicket
# Data Base
spring.datasource.url=jdbc:postgresql://db.<SUPABSE_PROJECT_REF>.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=DB_PSWD
spring.datasource.driver-class-name=org.postgresql.Driver
# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

springdoc.swagger-ui.oauth.client-id=swagger-client

logging.level.web=DEBUG
logging.level.org.springframework.security=DEBUG
```
