# 🏨 Grand Horizon — Hotel Management System

A desktop Hotel Management System built with **Java + JavaFX** and **SQLite**, featuring a polished UI with dark/light mode, booking management, billing, and revenue tracking.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| UI Framework | JavaFX |
| Database | SQLite (via `sqlite-jdbc`) |
| Build Tool | Maven (`pom.xml`) |

---

## Project Structure

```
com.hotel/
├── Main.java            # Entry point — launches JavaFX app
├── LoginScreen.java     # Login UI (admin / admin123)
├── DashboardScreen.java # Main UI — all tabs and screens
├── HotelService.java    # Business logic layer
├── DataManager.java     # SQLite DB read/write layer
├── Booking.java         # Booking model
├── Room.java            # Room model
└── RoomHistory.java     # Room action history model
```

---

## Features

### 📊 Dashboard
- Live stat cards: Total Rooms, Available, Occupied, Total Revenue
- Revenue automatically excludes cancelled bookings
- Recent bookings table

### 🛏 Room Management
- Add new rooms (Single / Double / Suite) with custom pricing
- Delete available rooms
- Live availability status per room

### 📋 Booking Management
- Book a room with customer name, phone, check-in/check-out dates
- Auto-calculates total bill based on price per day
- **Cancel a booking** — frees the room immediately, excluded from revenue
- View all bookings with status (🟡 Active / ✅ Completed / 🚫 Cancelled)

### 🔑 Checkout
- Select any occupied room to check out
- Choose payment method (Cash / UPI / Card)
- Auto-generates a printable invoice popup on checkout

### 💰 Billing & Invoice
- Search invoice by Booking ID
- Click any booking row to preview invoice
- Cancelled bookings show a notice instead of an invoice

### 🗂 Bill Records
- Full billing history with search/filter (by name, phone, room, ID)
- Revenue summary cards: Total Bills, Total Revenue, Avg Bill, Highest Bill
- All revenue stats exclude cancelled bookings
- Click any record to view the full invoice

### 🌙 Dark / Light Mode
- Toggle between themes from the header — applies instantly across all tabs

---

## Default Rooms (seeded on first run)

| Room | Type | Price/Day |
|------|------|-----------|
| 101 | Single | ₹1,200 |
| 102 | Single | ₹1,200 |
| 201 | Double | ₹2,200 |
| 202 | Double | ₹2,200 |
| 301 | Suite | ₹4,500 |

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Run the app

```bash
mvn clean javafx:run
```

### Build a JAR

```bash
mvn clean package
java -jar target/hotel-management.jar
```

### Login Credentials

```
Username: admin
Password: admin123
```

---

## Database

The app uses a local SQLite file `hotel.db` created automatically on first run in the project root. No external database setup is required.

**Tables:**
- `rooms` — room number, type, price per day, availability
- `bookings` — booking ID, customer info, dates, total bill, status

**Booking statuses:** `ACTIVE` · `COMPLETED` · `CANCELLED`

---

## Invoice Format

```
╔══════════════════════════════════════════╗
║         GRAND HORIZON HOTEL              ║
║           OFFICIAL INVOICE               ║
╠══════════════════════════════════════════╣
  Booking ID    : #12
  Customer      : John Doe
  Phone         : 9876543210
  Room No       : 201
  Check-In      : 01 Apr 2026
  Check-Out     : 05 Apr 2026
  Days Stayed   : 4
  Rate / Day    : ₹2200.00
  ──────────────────────────────────────
  TOTAL BILL    : ₹8800.00
╚══════════════════════════════════════════╝
```
