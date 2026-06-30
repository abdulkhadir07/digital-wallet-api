# Digital Wallet API

A RESTful API for a digital wallet platform supporting multi-currency transfers, FX conversion, deposits, withdrawals, and KYC verification, built with Spring Boot 4 and PostgreSQL.

## Live Demo

- Base URL (secured): [https://digital-wallet-api-551y.onrender.com](https://digital-wallet-api-551y.onrender.com)
- Swagger Docs: [https://digital-wallet-api-551y.onrender.com/swagger-ui/index.html](https://digital-wallet-api-551y.onrender.com/swagger-ui/index.html)

> The base URL is secured and requires authentication. Use Swagger UI to test endpoints.
> The API is hosted on a free tier service and may take up to 2 minutes to wake on first request due to cold starts.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Transfer Pricing Model](#transfer-pricing-model)
- [KYC Verification](#kyc-verification)
- [Project Structure](#project-structure)
- [Roadmap](#roadmap)

---

## Features

- **User registration and login** with JWT authentication
- **Automatic wallet creation** in the user's native currency on registration
- **Peer-to-peer transfers** with self-transfer protection and balance validation
- **Live transfer quote** : preview fee, FX rate, and recipient amount before sending
- **Recipient search** : find registered users by phone number
- **Multi-currency FX conversion** via ExchangeRate-API with 2% retail spread
- **Atomic balance updates** ensuring consistency between debit and credit operations
- **Tiered fee model** : free domestic, 1% same-currency cross-border, 2% FX spread
- **Deposit and withdrawal** framework with payment method selection (bank transfer, debit card, agent)
- **KYC enforcement** : transfers of $200 or more require verified KYC
- **Append-only transaction ledger** for full auditability
- **Wallet freeze and unfreeze** controls
- **Change password** with current password verification
- **Swagger UI** for interactive API exploration

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3 |
| Language | Java 17 |
| Database | PostgreSQL (Neon) |
| ORM | JPA / Hibernate |
| Security | Spring Security + JWT |
| FX Rates | ExchangeRate-API |
| HTTP Client | RestTemplate |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Deployment | Render |
| Utilities | Lombok, MapStruct |

---

## Architecture

The project follows a layered architecture with clean separation between service logic and API contracts:

- **Service layer** : operates on JPA entities and returns entities or primitives
- **Controller layer** : constructs response DTOs from service output; never leaks entities to clients
- **Security context** : `UserPrincipal` stores only primitive user fields (no JPA entities)

Money values use `BigDecimal` with `precision = 18, scale = 4` throughout. An append-only wallet transaction ledger records every balance change with `balanceBefore` and `balanceAfter` for full auditability.

---

## Getting Started

### Prerequisites

- Java 17+
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

### Running Locally

```bash
git clone https://github.com/abdulkhadir07/digital-wallet-api.git
cd digital-wallet-api

./mvnw clean package
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## API Reference

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

### Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | ❌ | Register a new user (wallet auto-created) |
| `POST` | `/auth/verify` | ❌ | Verify phone number with OTP code |
| `POST` | `/auth/login` | ❌ | Login and receive JWT token |
| `GET` | `/auth/me` | ✅ | Get logged-in user profile |
| `PATCH` | `/auth/change-password` | ✅ | Change account password |

### Wallet

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/wallet/me` | ✅ | Get current user's wallet balance and status |
| `GET` | `/wallet/transactions` | ✅ | Get full wallet transaction history |
| `POST` | `/wallet/deposit` | ✅ | Deposit funds into wallet |
| `POST` | `/wallet/withdraw` | ✅ | Withdraw funds from wallet |
| `PATCH` | `/wallet/freeze` | ✅ | Freeze wallet |
| `PATCH` | `/wallet/unfreeze` | ✅ | Unfreeze wallet |

### Transfers

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/transfer/quote` | ✅ | Preview fee, FX rate, and recipient amount |
| `POST` | `/transfer/send` | ✅ | Send funds to another registered user |
| `GET` | `/transfer/recipients/search` | ✅ | Search registered users by phone number |
| `GET` | `/transfer/history` | ✅ | All transfers (sent and received) |
| `GET` | `/transfer/{reference}` | ✅ | Lookup transfer by reference |
| `GET` | `/transfer/sent` | ✅ | Transfers sent by current user |
| `GET` | `/transfer/received` | ✅ | Transfers received by current user |

### KYC

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/kyc/submit` | ✅ | Submit KYC information for review |

---

## Testing the API

Use Swagger UI: [https://digital-wallet-api-551y.onrender.com/swagger-ui/index.html](https://digital-wallet-api-551y.onrender.com/swagger-ui/index.html)

Steps:

1. `POST /auth/register` : register a new user
2. Copy the `verificationCode` from the response
3. `POST /auth/verify` : verify the account with the code
4. `POST /auth/login` : login and copy the `token`
5. Click **Authorize** in Swagger and paste the token
6. Test wallet, transfer, and other protected endpoints

> OTP is returned in the API response for demo and testing purposes only. In production, OTP should be delivered via SMS or email.

### Transfer Request Body

```json
POST /transfer/send
{
  "recipientPhoneNumber": "+12025550123",
  "senderAmount": 150.00,
  "description": "Rent split"
}
```

### Transfer Response

```json
{
  "reference": "TRF-000CD09H1",
  "transferStatus": "COMPLETED",
  "message": "Your transfer has been successfully sent",
  "description": "Rent split",
  "senderInfo": {
    "senderName": "Abdul Khadir",
    "senderPhoneNumber": "+12025550123"
  },
  "senderAmount": 150.00,
  "recipientAmount": 150.00,
  "fee": 0.00,
  "senderCurrency": "USD",
  "recipientCurrency": "USD",
  "recipientInfo": {
    "recipientName": "Jane Smith",
    "recipientPhoneNumber": "+12025550124"
  },
  "createdAt": "2026-04-09T02:27:18.090Z"
}
```

---

## Transfer Pricing Model

| Scenario | Fee |
|---|---|
| Sender and recipient in the same country | Free (0%) |
| Same currency, different countries | 1% of transfer amount |
| Different currencies (FX conversion) | 2% FX spread on retail rate |

FX rates are fetched live from ExchangeRate-API. A 2% retail spread is applied to the mid-market rate for international FX transfers. Same-currency transfers short-circuit the FX API call entirely.

---

## KYC Verification

Transfers of **$75,000 USD or more** require the sending user to have a verified KYC status. Unverified users attempting large transfers will receive a `400` error with a prompt to complete verification.

---

## Project Structure

```
src/main/java/com/abdulkhadirjallow/digitalwalletapi/
├── controller/
│   ├── AuthController.java
│   ├── TransferController.java
│   ├── WalletController.java
│   └── KycController.java
├── service/
│   ├── AuthService.java
│   ├── TransferService.java
│   ├── WalletService.java
│   ├── FxRateService.java
│   └── KycService.java (if present)
├── entity/
│   ├── User.java
│   ├── Wallet.java
│   ├── WalletTransaction.java
│   ├── Transfer.java
│   └── KycProfile.java
├── dto/
│   ├── RegisterRequest.java / RegisterResponse.java
│   ├── LoginRequest.java / LoginResponse.java
│   ├── VerifyRequest.java / VerifyResponse.java
│   ├── UserProfileResponse.java
│   ├── ChangePasswordRequest.java
│   ├── WalletResponse.java
│   ├── WalletTransactionResponse.java
│   ├── DepositRequest.java / DepositResponse.java
│   ├── WithdrawalRequest.java / WithdrawalResponse.java
│   ├── TransferRequest.java / TransferResponse.java
│   ├── TransferQuoteRequest.java / TransferQuoteResponse.java
│   ├── RecipientInfo.java
│   ├── RecipientSearchResponse.java
│   ├── SenderInfo.java
│   └── KycSubmitRequest.java / KycResponse.java
├── repository/
│   ├── UserRepository.java
│   ├── WalletRepository.java
│   ├── WalletTransactionRepository.java
│   ├── TransferRepository.java
│   └── KycProfileRepository.java
├── enums/
│   ├── Country.java
│   ├── Currency.java
│   ├── WalletStatus.java
│   ├── TransactionType.java
│   ├── TransactionStatus.java
│   ├── TransactionSource.java
│   ├── TransferStatus.java
│   ├── TransferType.java
│   ├── PaymentMethod.java
│   ├── KycStatus.java
│   └── Continent.java
├── security/
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAccessDeniedHandler.java
│   ├── UserPrincipal.java
│   └── CustomUserDetailsService.java
├── config/
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   └── RestTemplateConfig.java
└── DigitalWalletApiApplication.java
```

---

## Roadmap

- [ ] SMS OTP delivery via Twilio or similar
- [ ] Stripe integration for debit card deposits and withdrawals
- [ ] Plaid integration for bank account verification and ACH transfers
- [ ] Agent network for cash deposit and withdrawal
- [ ] Scheduled FX rate caching to reduce external API calls
- [ ] Pagination on transaction and transfer history endpoints
- [ ] Server-side search on transaction history
- [ ] Admin dashboard API
- [ ] Push notification support
- [ ] Rate limiting and abuse protection
