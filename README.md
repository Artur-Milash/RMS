# Restaurant Management System

## Running the services with Docker Compose

Run `docker compose up --build` to start PostgreSQL and two API instances from
the same image:

- The restaurant service is available at `http://localhost:8080`. It serves
  authentication, users, tables, reservations, products, menu items, recipe
  items, and supplies.
- The orders service is available at `http://localhost:8081`. It serves
  orders, order items, and bills.

Both instances use the same PostgreSQL database and JWT settings. This keeps
the existing relationships between orders, menu items, tables, and users
working. The `restaurant` and `orders` Spring profiles select which API
controllers each instance exposes. Running without an active profile (or with
the `all` profile) keeps every endpoint available for local development.

`GET /api/orders` returns 100 orders by default. Use `?page=0&size=100` to
request a specific page; page numbers are zero-based and `size` is limited to
200. `X-Total-Count` and `X-Total-Pages` describe the full result, and the
`Link` response header provides next/previous page URLs.
