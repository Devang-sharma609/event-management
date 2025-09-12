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

#### SupabaseAuth API Structure:
**POST** *https://<PROJECT_REF>.supabase.co/auth/v1/token?grant_type=password**
```json
headers:
"apikey":"SUPABSE_PUBLISHABLE_KEY"
"Content-Type": "application/json"

JSONBody:
{
  "email": "mike.organizer@gmail.com",
  "password": "Organizer@123"
}
```

**POST** *https://<PROJECT_REF>.supabase.co/auth/v1/signup*

```json
headers:
"apikey":"SUPABSE_PUBLISHABLE_KEY"
"Content-Type": "application/json"

JSONBody:
{
  "email": "mike.organizer@gmail.com",
  "password": "Organizer@123",
"data"{
    "name": "Mike Brown",
    "role": "organizer"
    }
}
```

**POST** *https://<PROJECT_REF>.supabase.co/auth/v1/logout*
```json
headers:
"apikey":"SUPABSE_PUBLISHABLE_KEY"
"Authorization": "Bearer ${access_token}"
```

### Headers(to be sent with every REST-API Request):

```json
"Authorization":"Bearer ${access_token}",
"Content-Type" : "application/json"
```

### /api/v1/events — Event Management

1. **POST /api/v1/events**  
   Description: Create a new event  
   Access: **ORGANIZER**  
   Request Body:  
   ```json
   {
   "name": "Spring Fest",
   "venue": "Main Hall",
   "starts_at": "2024-09-01T10:00:00",
   "ends_at": "2024-09-01T18:00:00",
   "sales_starts_at": "2024-08-01T00:00:00",
   "sales_ends_at": "2024-08-31T23:59:59",
   "event_status": "DRAFT",
   "ticket_types": [
    {
      "name": "VIP",
      "description": "Front row seats",
      "price": 100.0,
      "totalAvailable": 50
    }
   ]
   }
   ```

2. **GET /api/v1/events**  
Description: Get all events for the authenticated organizer (paginated)  
Access: **ORGANIZER**  
Query Params:  
> page (default: 0)  
> size (default: 25)  
3. **GET /api/v1/events/{eventId}**  
   Description: Get a specific event by ID for the authenticated organizer  
   Access: **ORGANIZER**  
4. **PUT /api/v1/events/{eventId}**  
   Description: Update an event and its ticket types    
   Access: **ORGANIZER**   
   Request Body:  

   ```json
   {
   "name": "Spring Fest Updated",
   "venue": "Main Hall",
   "starts_at": "2024-09-01T10:00:00",
   "ends_at": "2024-09-01T18:00:00",
   "sales_starts_at": "2024-08-01T00:00:00",
   "sales_ends_at": "2024-08-31T23:59:59",
   "event_status": "PUBLISHED",
   "ticket_types": [
    {
      "id": "TICKET_TYPE_UUID",
      "name": "VIP",
      "price": 120.0,
      "totalAvailable": 60
    }
   ]
   }
   ```

5. **DELETE /api/v1/events/{eventId}**  
   Description: Delete an event  
   Access: **ORGANIZER**  

7. **POST /api/v1/events/{eventId}/staff**  
Description: Assign staff to an event  
Access: **ORGANIZER**  
Request Body:  

```json
{
  "userIds": ["USER_UUID_1", "USER_UUID_2"]
}
```

7. **GET /api/v1/events/published**  
   Description: Get all published events (paginated, public)  
   Access: **STAFF**, **ORGANIZER**, **ATTENDEE**  
   Query Params:  
> page (default: 0)  
> size (default: 25)  
> q (optional, search query)

8. **GET /api/v1/events/published/{id}**  
   Description: Get details of a published event  
   Access: **STAFF**, **ORGANIZER**, **ATTENDEE**  

### /api/v1/tickets — Ticket Management

9. **POST /api/v1/tickets/purchase**  
Description: Purchase a ticket for a ticket type  
Access: **ATTENDEE**  
Request Body:  
```json
{
  "ticketTypeId": "TICKET_TYPE_UUID"
}
```

10. **GET /api/v1/tickets**  
    Description: Get all tickets for the authenticated user (paginated)  
    Access: **ATTENDEE**  
    Query Params:  
> page (default: 0)  
> size (default: 25)  

11. **GET /api/v1/tickets/{id}**  
    Description: Get a specific ticket for the authenticated user  
    Access: **ATTENDEE**  

12. **GET /api/v1/tickets/{id}/qr-codes**  
    Description: Get the QR code image (PNG) for a ticket  
    Access: **ATTENDEE**  
    Response:  
    Content-Type: image/png (binary image data)

### /api/v1/ticket-validations — TicketValidation

13. **POST /api/v1/ticket-validations**  
    Description: Validate a ticket (by QR code or manually)  
    Access: **STAFF**  
    Request Body:  
```json
{
  "targetId": "TICKET_OR_QRCODE_UUID",
  "method": "QR_SCAN" // or "MANUAL"
}
```

**_Access Roles:_**  
**ORGANIZER**: Can manage events and assign staff.  
**ATTENDEE:** Can purchase and view their tickets.  
**STAFF:** Can validate tickets.  
