# 🪪 iTicket — Spring Boot Event Ticketing Platform API

An Event Ticketing REST API built with Spring Boot, Keycloak, and Spring Security.  
It supports event publishing, ticket purchasing, QR code generation, and validation — all accessible via Swagger UI.
> 📌 **Note**: This project is based on a solution originally developed by [Devtiro](https://www.youtube.com/@devtiro). I've implemented my own version with some changes and improvements.
---

## 🚀 Features

- 🎟️ Event and Ticket Management  
- 🛒 Purchase tickets
- 🔐 Role-based access control (Attendee, Organizer, Staff) via Keycloak  
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

Here are the screenshots from the Swagger UI for quick reference:

<img width="1820" height="569" alt="1" src="https://github.com/user-attachments/assets/5d84063e-fde9-423e-a9e9-ec90ac2fe8f8" />
<img width="1833" height="489" alt="2" src="https://github.com/user-attachments/assets/65b66994-b3b6-4367-b21d-51566b6b6056" />
