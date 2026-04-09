# Digital Wallet API

A RESTful API for a digital wallet platform supporting multi-currency transfers, FX conversion, and KYC verification; built with Spring Boot 4 and PostgreSQL.

## 🚀 Live Demo

- 🌐 Base URL (secured):
  [https://digital-wallet-api-551y.onrender.com](https://digital-wallet-api-551y.onrender.com)

- 📘 Swagger Docs:
  [https://digital-wallet-api-551y.onrender.com/swagger-ui/index.html](https://digital-wallet-api-551y.onrender.com/swagger-ui/index.html)

> ⚠️ Note: The base URL is secured and requires authentication. Use Swagger UI to test endpoints.
> ⚠️ The API is hosted on a free tier service and may take up to 2 minutes to wake on first request

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Running Locally](#running-locally)
- [API Reference](#api-reference)
  - [Authentication](#authentication)
  - [Wallet](#wallet)
  - [Transfers](#transfers)
  - [Kyc](#kyc)
- [Testing the api](#testing-the-api)
- [Transfer Price Model](#transfer-pricing-model)
- [KYC Verification](#kyc-verification)
- [Project Structure](#project-structure)

---

## 🧠 Features

- **User registration & login** with JWT authentication
- **Automatic wallet creation** in the user's native currency on registration
- **Peer-to-peer transfers** with self transfer protection and balance validation
- **Multi-currency FX conversion** via ExchangeRate-API
- **Atomic balance updates** ensuring consistency between debit and credit operations
- **Tiered fee model** — free domestic, 1% same currency cross-border, 0.5% FX spread
- **KYC enforcement** — transfers ≥ $200 equivalent require verified KYC
- **Append-only transaction ledger** for full auditability
- **Swagger UI** for interactive API exploration

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4 |
| Language | Java |
| Database | PostgreSQL (Neon) |
| ORM | JPA / Hibernate |
| Security | Spring Security + JWT |
| FX Rates | ExchangeRate-API |
| HTTP Client | RestTemplate |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Deployment | Render |
| Containerization | Docker |
| Utilities | Lombok |

---

## Architecture

The project follows a layered architecture with a clean separation between service logic and API contracts:

- **Service layer** — operates on JPA entities and returns entities or primitives
- **Controller layer** — constructs response DTOs from service output; never leaks entities to clients
- **Security context** — `UserPrincipal` stores only primitive user fields (no JPA entities)

Money values use `BigDecimal` with `precision = 18, scale = 4` throughout.

---

## Getting Started

### Prerequisites

- Java 21+
- PostgreSQL 14+
- Maven 3.9+
- An [ExchangeRate-API](https://www.exchangerate-api.com/) key (free tier works)

### Environment Variables

```properties
# Database
DB_URL=jdbc:postgresql://<host>/<db>
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION_MS=3600000

# FX
FX_API_KEY=your_exchangerate_api_key
FX_API_URL=https://v6.exchangerate-api.com/v6
```

## 📦 Running Locally

```bash
# Clone the repo
git clone https://github.com/abdulkhadir07/digital-wallet-api.git
cd digital-wallet-api

# Build
./mvnw clean package

# Run
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

Swagger UI: http://localhost:8080/swagger-ui/index.html

---

## API Reference

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

## 🔑 Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | ❌ | Register a new user (wallet auto-created) |
| `POST` | `/auth/login` | ❌ | Login and receive JWT token |

## 💳 Wallet

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/wallet` | ✅ | Get current user's wallet balance |

## 💸 Transfers

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/transfer/send` | ✅ | Send funds to another user |
| `GET` | `/transfer/history` | ✅ | All transfers (sent + received) |
| `GET` | `/transfer/{reference}` | ✅ | Lookup transfer by reference (e.g. `TRF-0001`) |
| `GET` | `/transfer/sent` | ✅ | Transfers sent by current user |
| `GET` | `/transfer/received` | ✅ | Transfers received by current user |

## 🧪 Testing the API

Use Swagger UI:

👉 [https://digital-wallet-api-551y.onrender.com/swagger-ui/index.html](https://digital-wallet-api-551y.onrender.com/swagger-ui/index.html)

Steps:

1. Register
2. Copy OTP from response
3. Verify account
4. Login
5. Click **Authorize** and paste JWT
6. Test wallet and transfer endpoints

## ⚠️ Development Notes

- OTP is returned in API response (for demo/testing only)
- In production, OTP should be delivered via SMS or email provider
- New wallets are initialized with a default balance (dev purpose)

#### Transfer Request Body

```json
POST /transfer/send
{
  "recipientPhoneNumber": "000-000-000",
  "amount": 150.00,
  "description": "Rent split"
}
```

#### Transfer Response

```json
{
  "reference": "TRF-0001",
  "transferStatus": "COMPLETED",
  "message": "Your transfer has been successfully sent",
  "description": "Rent split",
  "senderInfo": {
    "senderName": "Abdul Khadir",
    "senderPhoneNumber": "000-000-000"
  },
  "senderAmount": 150.00,
  "recipientAmount": 150.00,
  "fee": 0.00,
  "senderCurrency": "USD",
  "recipientCurrency": "USD",
  "recipientInfo": {
    "recipientName": "Jane Smith",
    "recipientPhoneNumber": "000-000-000"
  },
  "createdAt": "2026-04-09T02:27:18.090Z"
}
```

### KYC

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/kyc/status` | ✅ | Get current user's KYC status |
| `POST` | `/kyc/submit` | ✅ | Submit KYC documents for review |

---

## Transfer Pricing Model

| Scenario | Fee |
|---|---|
| Sender and recipient in the same country | Free (0%) |
| Same currency, different countries | 1% of transfer amount |
| Different currencies (FX conversion) | 0.5% FX spread |

FX rates are fetched live from ExchangeRate-API. Same currency transfers short circuit the API call.

---

## KYC Verification

Transfers with a value equivalent to **$200 USD or more** require the sending user to have a verified KYC status. Unverified users attempting large transfers will receive a `403` error with a prompt to complete verification.

KYC records are subject to expiry and are periodically reviewed via a scheduled checker.

---

## Project Structure

```
src/main/java/com/example/digitalwalletapi/
├── auth/
│   ├── AuthController.java
│   ├── AuthService.java
│   └── dto/  (RegisterRequest, RegisterResponse, LoginRequest, LoginResponse, VerifyRequest, VerifyResponse)
├── config/
│   ├── RestTemplateConfig.java
│   ├── SecurityConfig.java
│   └── OpenApiConfig.java
├── transfer/
│   ├── Transfer.java
│   ├── TransferRepository.java
│   ├── TransferService.java
│   ├── TransferController.java
│   └── dto/  (TransferRequest, TransferResponse, SenderInfo, RecipientInfo)
├── wallet/
│   ├── Wallet.java
│   ├── WalletRepository.java
│   ├── WalletService.java
│   ├── WalletController.java
│   └── dto/  (WalletResponse, WalletTransactionResponse)
├── kyc/
│   ├── Kyc.java
│   ├── KycRepository.java
│   ├── KycService.java
│   ├── KycController.java
│   └── dto/  (KycSubmitRequest, KycResponse)
├── user/
│   ├── User.java
│   ├── UserRepository.java
│   └── UserPrincipal.java
└── fx/
    └── FxRateService.java
```
