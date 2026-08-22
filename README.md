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

## Pages

All pages are static HTML sharing a common header/footer (injected by `header.js` / `footer.js`) that is session-aware (`/api/sessions/sessions-data`) and includes a mini-cart. Product cards are rendered by a shared `product-card.js` component used on every listing page.

| Page | Purpose |
|---|---|
| `index.html` / `index-2.html` | Home – hero slider, promo banners, featured/new arrivals (`api/products/load-home`, `/sort`) |
| `shop.html` | Catalog – category tree, price-range slider, color/size/brand filters, search, sorting, page-size, quick-view modal (`api/products/sort?...`) |
| `single-product.html` | Product detail – gallery, color/size pickers with live stock check, quantity, add to cart/wishlist, related products |
| `cart.html` | Cart – load, update quantities, remove items |
| `wishlist.html` | Wishlist (**login required**) – list, remove, move to cart |
| `signup.html` / `login.html` | Account creation and sign-in |
| `verify-account.html` | 6-digit email verification code entry (normal & guest flows) |
| `complete-order.html` | Checkout – delivery type, address form with cascading Province → District → City, payment method choice |
| `my-account.html` | (**login required**) Profile edit, multiple addresses (add/update/delete/set primary), password change with emailed code, order history with item cancellation |
| `admin-account.html` | (**admin only**) Dashboard (top-selling, low stock), product manager (details + stocks + multi-image upload + discounts), order manager (per-order/per-item status), user manager (enable/disable) |
| `404.html` | Not-found page |

### Design

- Bootstrap-based e-commerce template ("Clothing") with Material Design icons, Font Awesome, slick/nivo sliders, jQuery UI widgets, and Notiflix toast notifications.
- Per-page JS modules in `assets/js/webjs` call the REST API using `fetch` + async/await; all payloads are JSON (multipart only for image uploads).
- Access control on pages is enforced server-side by servlet filters (`web.xml`): unauthenticated users hitting protected pages are redirected to `login.html`.

## Key Workflows

**1. Registration & verification**
```
signup.html ──POST /api/users──▶ verification email (Mailtrap SMTP)
    │
verify-account.html ──POST /api/users/verify-accounts──▶ account activated ──▶ login.html
```

**2. Shopping & cart**
```
index / shop ──filter & sort──▶ product cards (add to cart / wishlist from card)
    │
single-product.html ──pick color+size──▶ live stock check ──add to cart──▶
cart.html ──update qty / remove──▶ proceed to checkout
```
Guest carts live in the HTTP session; wishlist requires login (`@IsUser`).

**3. Checkout**
```
complete-order.html
├─ choose delivery type (api/data/load-delivery-types)
├─ enter/select address (province ▶ district ▶ city cascades)
└─ pay:
   ├─ Cash on Delivery ──▶ POST /api/checkout/cod ──▶ order created
   └─ PayHere ──▶ POST /api/checkout/payhere ──▶ signed request ──▶
        PayHere checkout ──▶ notify callback (MD5 signature validated,
        /api/payments/dev-notify for local testing) ──▶ order confirmed
```
Guests can check out: a guest account is created (`/api/checkout/create-guest-account`) and verified by email before the order completes.

**4. Post-purchase (customer)**
```
my-account.html ──order history──▶ track status / cancel item
             └──profile──▶ edit details, manage addresses, change password (email code)
```

**5. Admin operations**
```
admin-account.html
├─ Dashboard: most-selling products, low-stock alerts
├─ Products: save product ▶ save stocks ▶ upload images ▶ set discounts/status
├─ Orders: sort/filter ▶ update order & item statuses
└─ Users: search/sort ▶ enable/disable accounts
```

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
