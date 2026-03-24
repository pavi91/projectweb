# Ocean View Resort – Hotel Reservation System

A full-featured hotel reservation web application built with **Java 21**, **Jakarta Servlet/JSP**, and **MySQL**. The system supports four distinct user roles (Admin, Receptionist, Guest, Maintenance) and handles both online and walk-in reservations.

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Design Patterns](#design-patterns)
- [User Roles & Workflows](#user-roles--workflows)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running Tests](#running-tests)

---

## Features

- **Guest portal** – search available rooms, make online reservations, view/cancel bookings, process payments
- **Front desk portal** – manage walk-in reservations, check-in/check-out guests, print bills
- **Admin portal** – staff management, room maintenance scheduling, seasonal pricing, revenue/occupancy/cancellation reports
- **Authentication & authorization** – BCrypt-hashed passwords, session-based auth, role-based access control via servlet filter
- **Payment processing** – pluggable adapter supporting POS terminals and online payment gateways
- **Dynamic pricing** – Strategy pattern-based pricing (standard rate vs. seasonal multiplier)
- **Connection pooling** – HikariCP for efficient database connections
- **Structured logging** – SLF4J + Logback

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Web framework | Jakarta Servlet 6.0 + JSP 3.1 + JSTL 3.0 |
| Application server | Apache Tomcat (Jakarta EE 10+) |
| Database | MySQL 8 |
| Connection pool | HikariCP 5.1 |
| Password hashing | jBCrypt 0.4 |
| Logging | SLF4J 2.0 + Logback 1.4 |
| Build tool | Maven 3 (packaged as WAR) |
| Testing | JUnit 4 + Mockito 5 + JaCoCo |

---

## Architecture Overview

The application follows a classic **layered (n-tier) MVC** architecture:

```
Browser
  │
  ▼
AuthFilter  ──────────────────────────────── (applies to all requests)
  │
  ▼
Servlet (Controller entry point)
  │  LoginServlet  /login
  │  LogoutServlet /logout
  │  SignUpServlet /signup
  │  ReservationServlet /reservation/*
  │  FrontDeskServlet   /frontdesk/*
  │  AdminServlet       /admin/*
  │
  ▼
Controller (business orchestration)
  │  SystemController      – login/logout/signup
  │  ReservationController – guest reservation flow
  │  FrontDeskController   – receptionist workflows
  │  AdminController       – admin operations
  │
  ▼
Service layer
  │  UserService / UserServiceImpl
  │  ReservationService / BookingService (online) / WalkInResService
  │  RoomService / RoomServiceImpl
  │  PaymentService / PaymentServiceImpl
  │  SeasonalPricingService / SeasonalPricingServiceImpl
  │  ReportService / ReportServiceImpl
  │
  ▼
Repository (DAO) layer
  │  UserRepository / UserDAOImpl
  │  GuestRepository / GuestRepositoryImpl
  │  ReservationRepository / ReservationDAOImpl
  │  RoomRepository / RoomDAOImpl
  │  SeasonalPricingRepository / SeasonalPricingDAOImpl
  │
  ▼
DatabaseConnection (HikariCP)
  │
  ▼
MySQL – hotel_reservation
```

**DTOs** (`GuestDTO`, `RoomDTO`, `ReservationDTO`, `UserDTO`) carry data between layers.  
**Mappers** (`GuestMapper`, `RoomMapper`, `UserMapper`) convert between entities and DTOs.

---

## Project Structure

```
projectweb/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/hotelreservation/
    │   │   ├── adapter/          # Payment adapters (POS, Online Gateway)
    │   │   ├── controller/       # Business orchestration controllers
    │   │   ├── dto/              # Data Transfer Objects
    │   │   ├── entity/           # Domain entities (User, Guest, Room, Reservation…)
    │   │   ├── exception/        # Custom exceptions
    │   │   ├── filter/           # AuthFilter (authentication/authorization)
    │   │   ├── mapper/           # Entity ↔ DTO mappers
    │   │   ├── persistence/      # DatabaseConnection (HikariCP setup)
    │   │   ├── repository/       # Repository interfaces + impl/ (DAOs)
    │   │   ├── service/          # Service interfaces + impl/ (business logic)
    │   │   ├── servlet/          # HTTP entry points (Servlets)
    │   │   ├── strategy/         # Pricing strategies
    │   │   └── util/             # QueryLogger, GenerateSeedData
    │   ├── resources/
    │   │   ├── application.properties  # App & DB configuration
    │   │   ├── logback.xml             # Logging configuration
    │   │   └── database/
    │   │       ├── schema.sql          # DDL – create all tables
    │   │       └── seed.sql            # Initial data
    │   └── webapp/
    │       ├── index.jsp               # Landing / welcome page
    │       ├── WEB-INF/web.xml         # Servlet mappings, filters, error pages
    │       └── jsp/
    │           ├── login.jsp / signup.jsp
    │           ├── admin/              # Admin dashboard, reports, staff, pricing…
    │           ├── guest/              # Room search, reservation, payment, cancellation…
    │           └── receptionist/       # Walk-in form, check-in/out, bill…
    └── test/
        └── java/com/hotelreservation/
            ├── adapter/          # PaymentAdapterTest
            ├── controller/       # AdminControllerTest, FrontDeskControllerTest…
            ├── entity/           # Entity unit tests
            ├── exception/        # ExceptionTest
            ├── mapper/           # MapperTest
            ├── service/          # PaymentServiceTest, UserServiceTest
            └── strategy/         # PricingStrategyTest
```

---

## Database Schema

Six tables in the `hotel_reservation` database:

| Table | Purpose |
|---|---|
| `users` | All system accounts; roles: `ADMIN`, `RECEPTIONIST`, `GUEST`, `MAINTENANCE` |
| `guests` | Guest profile linked to a user account (name, NIC, phone, email, address) |
| `rooms` | Room inventory; types: `SINGLE`, `DOUBLE`, `SUITE`; statuses: `AVAILABLE`, `OCCUPIED`, `RESERVED`, `UNDER_MAINTENANCE` |
| `reservations` | Polymorphic reservations; types: `ONLINE`, `WALK_IN`; statuses: `PENDING` → `CONFIRMED` → `CHECKED_IN` → `CHECKED_OUT` / `CANCELLED` |
| `maintenance_tasks` | Room maintenance jobs assigned to maintenance staff |
| `payment_transactions` | Payment records linked to reservations |
| `seasonal_pricing` | Date-range multipliers used by `SeasonalRateStrategy` |

Initialize the database by running the scripts in order:

```sql
source src/main/resources/database/schema.sql
source src/main/resources/database/seed.sql
```

---

## Design Patterns

| Pattern | Where used |
|---|---|
| **MVC** | Servlets (View dispatch) → Controllers → Services → Repositories |
| **Repository / DAO** | `*Repository` interfaces with `impl/*` implementations |
| **Strategy** | `IPricingStrategy` → `StandardRateStrategy` / `SeasonalRateStrategy` |
| **Adapter** | `IPaymentAdapter` → `POSAdapter` / `OnlineGatewayAdapter` |
| **DTO / Mapper** | `*DTO` classes + `*Mapper` classes decouple layers |

---

## User Roles & Workflows

### Guest
1. Sign up / log in at `/login`
2. Search available rooms (`/reservation/search`)
3. Complete booking form and pay online (`/reservation/payment`)
4. View existing reservations and cancel if needed (`/reservation/my-reservations`)

### Receptionist
1. Log in → Front Desk dashboard (`/frontdesk/dashboard`)
2. Create walk-in reservation (`/frontdesk/walk-in`)
3. Check in guest upon arrival (`/frontdesk/check-in`)
4. Check out guest and print bill (`/frontdesk/check-out`)

### Admin
1. Log in → Admin dashboard (`/admin/dashboard`)
2. Manage staff accounts (`/admin/staff`)
3. Schedule room maintenance (`/admin/maintenance`)
4. Configure seasonal pricing (`/admin/seasonal-pricing`)
5. Configure payment adapter (`/admin/payment-config`)
6. View reports: revenue, occupancy, cancellations (`/admin/reports`)

### Maintenance
- Log in to view and update assigned maintenance tasks.

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8+
- Apache Tomcat 10.1+ (Jakarta EE 10)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/pavi91/projectweb.git
   cd projectweb
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE hotel_reservation;
   USE hotel_reservation;
   SOURCE src/main/resources/database/schema.sql;
   SOURCE src/main/resources/database/seed.sql;
   ```

3. **Configure the application**  
   Edit `src/main/resources/application.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/hotel_reservation
   db.username=<your_mysql_user>
   db.password=<your_mysql_password>
   ```

4. **Build the WAR**
   ```bash
   mvn clean package
   ```

5. **Deploy**  
   Copy `target/projectweb.war` to your Tomcat `webapps/` directory and start Tomcat.

6. **Access the application**  
   Open `http://localhost:8080/projectweb/`

---

## Configuration

All runtime settings live in `src/main/resources/application.properties`:

| Key | Description |
|---|---|
| `db.url` / `db.username` / `db.password` | MySQL connection |
| `db.pool.size.min` / `db.pool.size.max` | HikariCP pool bounds |
| `payment.adapter.type` | `POS` or `ONLINE_GATEWAY` |
| `payment.pos.endpoint` | POS terminal URL |
| `payment.gateway.endpoint` | Online payment gateway URL |
| `session.timeout.minutes` | HTTP session timeout |
| `log.level` / `log.file.path` | Logging verbosity and output path |

Logging is configured separately in `src/main/resources/logback.xml`.

---

## Running Tests

```bash
# Run all tests
mvn test

# Run tests and generate JaCoCo coverage report
mvn test jacoco:report
# Report: target/site/jacoco/index.html
```

Test coverage spans controllers, services, entities, mappers, adapters, strategies, and exception types using JUnit 4 + Mockito 5.
