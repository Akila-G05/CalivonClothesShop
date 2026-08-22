# Calivon Clothes Shop

Calivon is a full-stack e-commerce web application for an online clothes shop, built with Java, an embedded Tomcat server, a JAX-RS REST API (Jersey), Hibernate ORM with MySQL, and a static HTML/JS frontend.

## Features

- **Accounts** – signup with email verification, login/logout, guest checkout accounts, profile management, password change
- **Product catalog** – categories/subcategories, brands, colors, sizes, per-variant stock, discounts, multi-image products
- **Shopping** – cart, wishlist, live stock lookup by color/size, shop filtering
- **Checkout** – Cash on Delivery and PayHere payment gateway (hash generation + callback signature verification)
- **Admin panel** – dashboard, product CRUD with image uploads, rich-text editor for descriptions
- **Security** – session-based auth with servlet filters (page guards) and JAX-RS request filters (`@IsUser` / `@IsAdmin`)

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 11, Maven (`war`) |
| Server | Embedded Tomcat 10.1.7 |
| REST API | Jersey 3.1.2 (JAX-RS), multipart |
| ORM / DB | Hibernate ORM 6.1.7 + MySQL 8 |
| JSON | Gson |
| Email | Jakarta Mail + rocketbase email-template-builder (Mailtrap SMTP) |
| Payments | PayHere gateway |
| Frontend | HTML/CSS/JS (Bootstrap template, jQuery, Notiflix) |

## Project Structure

```
src/main/java/lk/jiat/calivon
├── Main.java              # Boots embedded Tomcat, registers Jersey servlet
├── config/                # Jersey ResourceConfig
├── controller/api/        # REST resources (users, products, checkout, ...)
├── service/               # Business logic + DAOs
├── entity/                # JPA entities (22)
├── dto/                   # Request/response DTOs
├── middleware/            # Auth filters (servlet + JAX-RS)
├── mail/                  # Async email sending
└── util/                  # HibernateUtil, Env, PayHereUtil, AppUtil

src/main/webapp            # Static frontend (HTML pages + assets/js/webjs modules)
src/main/resources         # hibernate.cfg.xml, app.properties
src/test/java              # Manual test harnesses
```

## Getting Started

### Prerequisites

- JDK 11+
- Maven 3.x
- MySQL 8 running on `localhost:3306` with a database named `calivon`

### Configuration

1. Update DB credentials in `src/main/resources/hibernate.cfg.xml`.
2. Update mail/payment settings in `src/main/resources/app.properties`.

### Run

```bash
mvn compile exec:java -Dexec.mainClass="lk.jiat.calivon.Main"
```

Or run `Main.java` directly from IntelliJ IDEA.

App URL: http://localhost:8080/calivon

## API Overview

All endpoints are mounted under `/calivon/api/*`. Main resources:

- `/api/users` – signup, login, account verification, logout
- `/api/products` – cart, wishlist, product/stock data
- `/api/checkout` – COD & PayHere checkout
- `/api/orders`, `/api/profile`, `/api/session`, `/api/content`, `/api/payment-gateway`
