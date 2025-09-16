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

## 🔐 Authentication

### Supabase Auth API Structure:

#### **Login - POST** *https://<PROJECT_REF>.supabase.co/auth/v1/token?grant_type=password*
```json
Headers:
{
  "apikey": "SUPABASE_PUBLISHABLE_KEY",
  "Content-Type": "application/json"
}

Request Body (All Required):
{
  "email": "user.organizer@gmail.com",        // Required: Valid email format
  "password": "Organizer@123"                 // Required: Minimum 8 characters
}
```

**Success Response (200):**
```json
{
  "access_token": "eyJhbGciOiJFUzI1NiIs...",  // JWT token for API authentication
  "token_type": "bearer",
  "expires_in": 3600,                         // Token expiry in seconds
  "expires_at": 1758012705,                   // Unix timestamp of expiry
  "refresh_token": "voqhpuwbj4ag",           // Token for refreshing access_token
  "user": {
    "id": "7759aeee-eb03-483d-95e5-dba9c31399f4",
    "aud": "authenticated",
    "role": "authenticated",
    "email": "mike.organizer@gmail.com",
    "email_confirmed_at": "2025-09-02T11:22:37.016695Z",
    "phone": "",
    "confirmed_at": "2025-09-02T11:22:37.016695Z",
    "last_sign_in_at": "2025-09-16T07:51:45.359186887Z",
    "app_metadata": {
      "provider": "email",
      "providers": ["email"]
    },
    "user_metadata": {
      "email": "mike.organizer@gmail.com",
      "email_verified": true,
      "name": "Mike Brown",                   // Full name from signup
      "phone_verified": false,
      "role": "Organizer",                    // User role for authorization
      "sub": "7759aeee-eb03-483d-95e5-dba9c31399f4"
    },
    "identities": [
      {
        "identity_id": "8bed2a5b-9b97-4015-bfa3-c1c180a58fb4",
        "id": "7759aeee-eb03-483d-95e5-dba9c31399f4",
        "user_id": "7759aeee-eb03-483d-95e5-dba9c31399f4",
        "identity_data": {
          "email": "mike.organizer@gmail.com",
          "email_verified": false,
          "name": "Mike Brown",
          "phone_verified": false,
          "role": "Organizer",
          "sub": "7759aeee-eb03-483d-95e5-dba9c31399f4"
        },
        "provider": "email",
        "last_sign_in_at": "2025-09-02T11:22:37.013228Z",
        "created_at": "2025-09-02T11:22:37.013284Z",
        "updated_at": "2025-09-02T11:22:37.013284Z",
        "email": "mike.organizer@gmail.com"
      }
    ],
    "created_at": "2025-09-02T11:22:37.008387Z",
    "updated_at": "2025-09-16T07:51:45.417167Z",
    "is_anonymous": false
  },
  "weak_password": null
}
```

#### **Signup - POST** *https://<PROJECT_REF>.supabase.co/auth/v1/signup*
```json
Headers:
{
  "apikey": "SUPABASE_PUBLISHABLE_KEY",
  "Content-Type": "application/json"
}

Request Body:
{
  "email": "mike.organizer@gmail.com",        // Required: Valid email format
  "password": "Organizer@123",                // Required: Minimum 8 characters
  "data": {                                   // Required: User metadata
    "name": "Mike Brown",                     // Required: Full name
    "role": "organizer"                       // Required: "attendee" | "organizer" | "staff"
  }
}
```

**Success Response (200):** Same structure as Login response above

#### **Logout - POST** *https://<PROJECT_REF>.supabase.co/auth/v1/logout*
```json
Headers:
{
  "apikey": "SUPABASE_PUBLISHABLE_KEY",
  "Authorization": "Bearer ${access_token}"   // Required: Valid JWT token
}
```

**Success Response (204):** No content

### Required Headers for All API Requests:
```json
{
  "Authorization": "Bearer ${access_token}",  // Required: JWT from Supabase auth
  "Content-Type": "application/json"         // Required for POST/PUT requests
}
```

---

## 📋 API Endpoints


### 🎪 Event Management (/api/v1/events)

#### **1. POST /api/v1/events**
**Description:** Create a new event  
**Access:** ORGANIZER  

**Request Body:**
```json
{
  "name": "Spring Fest",                      // Required: 1-255 characters
  "venue": "Main Hall",                       // Required: 1-255 characters
  "description": "Amazing spring event",      // Optional: Max 1000 characters
  "starts_at": "2024-09-01T10:00:00",        // Required: ISO 8601 datetime, future date
  "ends_at": "2024-09-01T18:00:00",          // Required: ISO 8601 datetime, after starts_at
  "sales_starts_at": "2024-08-01T00:00:00",  // Required: ISO 8601 datetime, before starts_at
  "sales_ends_at": "2024-08-31T23:59:59",    // Required: ISO 8601 datetime, before starts_at
  "event_status": "DRAFT",                    // Required: "DRAFT" | "PUBLISHED" | "CANCELLED"
  "ticket_types": [                           // Required: Array with at least 1 ticket type
    {
      "name": "VIP",                          // Required: 1-100 characters
      "description": "Front row seats",       // Optional: Max 500 characters
      "price": 100.0,                        // Required: Positive decimal (min 0.01)
      "totalAvailable": 50                    // Required: Positive integer (min 1)
    }
  ]
}
```

**Success Response (201):**
```json
{
  "id": "event-uuid",
  "name": "Spring Fest",
  "venue": "Main Hall",
  "description": "Amazing spring event",
  "starts_at": "2024-09-01T10:00:00",
  "ends_at": "2024-09-01T18:00:00",
  "sales_starts_at": "2024-08-01T00:00:00",
  "sales_ends_at": "2024-08-31T23:59:59",
  "event_status": "DRAFT",
  "organizer_id": "user-uuid",
  "created_at": "2024-01-15T12:00:00",
  "ticket_types": [
    {
      "id": "ticket-type-uuid",
      "name": "VIP",
      "description": "Front row seats",
      "price": 100.0,
      "totalAvailable": 50,
      "availableCount": 50
    }
  ]
}
```

#### **2. GET /api/v1/events**
**Description:** Get all events for authenticated organizer (paginated)  
**Access:** ORGANIZER  

**Query Parameters:**
```
page: 0           // Optional: Page number (default: 0)
size: 25          // Optional: Page size (default: 25, max: 100)
```

**Success Response (200):**
```json
{
  "content": [
    {
      "id": "event-uuid",
      "name": "Spring Fest",
      "venue": "Main Hall",
      "starts_at": "2024-09-01T10:00:00",
      "ends_at": "2024-09-01T18:00:00",
      "event_status": "DRAFT",
      "ticket_types_count": 2
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 25
  },
  "totalElements": 5,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

#### **3. GET /api/v1/events/{eventId}**
**Description:** Get specific event by ID for authenticated organizer  
**Access:** ORGANIZER  

**Path Parameters:**
```
eventId: UUID     // Required: Valid UUID of the event
```

**Success Response (200):** Same as POST response structure

#### **4. PUT /api/v1/events/{eventId}**
**Description:** Update an event and its ticket types  
**Access:** ORGANIZER  

**Path Parameters:**
```
eventId: UUID     // Required: Valid UUID of the event
```

**Request Body:**
```json
{
  "name": "Spring Fest Updated",              // Required: 1-255 characters
  "venue": "Main Hall",                       // Required: 1-255 characters
  "description": "Updated description",       // Optional: Max 1000 characters
  "starts_at": "2024-09-01T10:00:00",        // Required: ISO 8601 datetime
  "ends_at": "2024-09-01T18:00:00",          // Required: ISO 8601 datetime
  "sales_starts_at": "2024-08-01T00:00:00",  // Required: ISO 8601 datetime
  "sales_ends_at": "2024-08-31T23:59:59",    // Required: ISO 8601 datetime
  "event_status": "PUBLISHED",               // Required: "DRAFT" | "PUBLISHED" | "CANCELLED"
  "ticket_types": [                           // Required: Array with at least 1 ticket type
    {
      "ticket_type_id": "ticket-type-uuid",   // Optional: Include for existing ticket types
      "name": "VIP",                          // Required: 1-100 characters
      "description": "Updated description",   // Optional: Max 500 characters
      "price": 120.0,                        // Required: Positive decimal
      "totalAvailable": 60                    // Required: Positive integer
    }
  ]
}
```

#### **5. DELETE /api/v1/events/{eventId}**
**Description:** Delete an event  
**Access:** ORGANIZER  

**Path Parameters:**
```
eventId: UUID     // Required: Valid UUID of the event
```

**Success Response (204):** No content

#### **6. GET /api/v1/events/{eventId}/staff**
**Description:** Get staff assigned to an event  
**Access:** ORGANIZER  

**Success Response (200):**
```json
{
  "user_id": "user-uuid",
  "name": "John Staff",
  "email": "john.staff@example.com",
  "assigned_at": "2024-01-15T12:00:00"
}
```

#### **7. POST /api/v1/events/{eventId}/staff**
**Description:** Assign staff to an event  
**Access:** ORGANIZER  

**Request Body:**
```json
{
  "userIds": ["user-uuid-1", "user-uuid-2"]  // Required: Array of valid user UUIDs with "staff" role
}
```

#### **8. GET /api/v1/events/published**
**Description:** Get all published events (paginated, public)  
**Access:** STAFF, ORGANIZER, ATTENDEE  

**Query Parameters:**
```
page: 0           // Optional: Page number (default: 0)
size: 25          // Optional: Page size (default: 25, max: 100)
q: "search term"  // Optional: Search query for event name/description
```

#### **9. GET /api/v1/events/published/{id}**
**Description:** Get details of a published event  
**Access:** STAFF, ORGANIZER, ATTENDEE  

---

### 🎫 Ticket Management (/api/v1/tickets)

#### **10. POST /api/v1/tickets/purchase**
**Description:** Purchase a ticket for a ticket type  
**Access:** ATTENDEE  

**Request Body:**
```json
{
  "ticketTypeId": "ticket-type-uuid"          // Required: Valid UUID of available ticket type
}
```

**Success Response (201):**
```json
{
  "id": "ticket-uuid",
  "ticket_type": {
    "id": "ticket-type-uuid",
    "name": "VIP",
    "price": 100.0,
    "event": {
      "id": "event-uuid",
      "name": "Spring Fest",
      "venue": "Main Hall",
      "starts_at": "2024-09-01T10:00:00"
    }
  },
  "purchaser_id": "user-uuid",
  "purchased_at": "2024-01-15T12:00:00",
  "status": "ACTIVE",
  "qr_code_id": "qr-code-uuid"
}
```

#### **11. GET /api/v1/tickets**
**Description:** Get all tickets for authenticated user (paginated)  
**Access:** ATTENDEE  

**Query Parameters:**
```
page: 0           // Optional: Page number (default: 0)
size: 25          // Optional: Page size (default: 25, max: 100)
```

#### **12. GET /api/v1/tickets/{id}**
**Description:** Get specific ticket for authenticated user  
**Access:** ATTENDEE  

**Path Parameters:**
```
id: UUID          // Required: Valid UUID of user's ticket
```

#### **13. GET /api/v1/tickets/{id}/qr-codes**
**Description:** Get QR code image (PNG) for a ticket  
**Access:** ATTENDEE  

**Response:** Binary PNG image data  
**Content-Type:** image/png

---

### ✅ Ticket Validation (/api/v1/ticket-validations)

#### **14. POST /api/v1/ticket-validations**
**Description:** Validate a ticket (by QR code or manually)  
**Access:** STAFF  

**Request Body:**
```json
{
  "targetId": "ticket-or-qrcode-uuid",        // Required: UUID of ticket or QR code
  "method": "QR_SCAN"                         // Required: "QR_SCAN" | "MANUAL"
}
```

**Success Response (200):**
```json
{
  "id": "validation-uuid",
  "ticket_id": "ticket-uuid",
  "staff_id": "user-uuid",
  "method": "QR_SCAN",
  "validated_at": "2024-01-15T12:00:00",
  "status": "VALID",
  "ticket": {
    "id": "ticket-uuid",
    "status": "USED",
    "event": {
      "name": "Spring Fest",
      "venue": "Main Hall"
    }
  }
}
```

---

## 🚨 Error Response Format

All API endpoints return errors in the following format:

```json
{
  "timestamp": "2024-01-15T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'name'",
  "path": "/api/v1/events"
}
```

**Common HTTP Status Codes:**
- `200` - Success
- `201` - Created
- `204` - No Content
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (invalid/missing token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `409` - Conflict (e.g., duplicate email)
- `500` - Internal Server Error

---

**Access Roles:**  
- **ORGANIZER**: Can manage events and assign staff
- **ATTENDEE**: Can purchase and view their tickets  
- **STAFF**: Can validate tickets for assigned events

**Data Validation Rules:**
- All datetime fields must be in ISO 8601 format
- UUIDs must be valid UUID v4 format
- Email addresses must be valid format
- Prices must be positive decimals with max 2 decimal places
- Pagination: max size is 100, default is 25
