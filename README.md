# Enterprise Banking Core Engine 🏦

A high-performance, enterprise-grade banking backend engine built with **Java 17**, **Spring Boot 3.2.5**, and **Oracle Database**. Designed for robust financial operations, audit compliance, multi-currency processing, and high-concurrency transactional integrity.

---

## 📋 Executive Summary

The **Enterprise Banking Core Engine** provides a comprehensive core banking platform supporting retail banking, corporate banking, treasury services, investment management, and compliance enforcement. Built with modern microservices-ready architecture, it ensures strict double-entry ledger bookkeeping, real-time fraud/AML compliance checking, and high-throughput transaction processing.

---

## ✨ Key Features & Functional Modules

### 1. 💳 Core & Retail Banking
* **Account Management**: Savings, Checking, Fixed Deposit, and Safe Deposit Vault servicing.
* **Double-Entry Ledger Engine**: Implements strict debits/credits balance matching for institutional accounting integrity.
* **Card Servicing**: Credit and Debit card issuance, limit control, pin management, and freeze/block functionality.
* **Retail Loans & Mortgages**: Automated loan application processing, credit scoring, and automated mortgage underwriting.

### 2. 💸 Transactions & Money Transfer
* **Real-time Funds Transfer**: Instant peer-to-peer and account-to-account transfers.
* **Wire Transfer System**: Supports domestic (ACH/FedWire) and international cross-border transfers (SWIFT/SEPA).
* **Multi-Currency & Forex**: Real-time foreign exchange rate execution, multi-currency wallets, and FX settlement.

### 3. 🏢 Corporate & Investment Banking
* **Corporate Banking Services**: Enterprise corporate accounts, payroll dispatch, and liquidity management.
* **Syndicated Loans & Trade Finance**: Letters of Credit (LC), Bank Guarantees (BG), and multi-lender syndicated loan administration.
* **Treasury & Escrow Management**: Institutional asset-liability management, yield optimization, and escrow vaults.

### 4. 📈 Wealth Management & Capital Markets
* **Securities & Equities Trading**: Real-time order execution for stocks, bonds, and mutual funds.
* **Wealth Management**: Portfolio tracking, rebalancing, and financial advisory pipelines.
* **Bancassurance**: Integrated insurance product offerings (Life, Health, Property).

### 5. 🛡️ Compliance, Security & Audit
* **AML / KYC & Fraud Monitoring**: Real-time transaction sanction screening and suspicious activity reporting.
* **Immutable Audit Trail**: Comprehensive audit logging for all transactional and administrative activities.
* **Spring Security & OpenAPI**: Enterprise RBAC security with automated Swagger REST API documentation.

---

## 🛠️ Technology Stack

| Component | Technology |
| :--- | :--- |
| **Language & Framework** | Java 17, Spring Boot 3.2.5 |
| **Data & Persistence** | Spring Data JPA, Oracle Database (`ojdbc11`), H2 Database (Test Profile) |
| **Security & APIs** | Spring Security, OpenAPI 3.0 / Swagger UI (`springdoc-openapi`) |
| **Build & Test Suite** | Apache Maven, JUnit 5, Spring Security Test, Lombok |

---

## 🚀 Getting Started

### Prerequisites
* **Java**: JDK 17 or higher
* **Maven**: Apache Maven 3.8+ (or Maven Wrapper)
* **Database**: Oracle DB 19c/21c (or H2 for local testing)

### Installation & Run

1. **Clone the Repository**
   ```bash
   git clone https://github.com/SreejaBiswas17/banking_application.git
   cd banking_application
