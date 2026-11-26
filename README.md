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

## Continuous integration and delivery

GitHub Actions runs independent build, Checkstyle, and test jobs for the
restaurant and orders services on every pull request and branch push. It then
builds and scans a separate local container image for each service. Branch
pushes publish images to GitHub Container Registry; pushes to `master` also
update the `latest` tag. The commit SHA tag is published for every branch push.
Publishing uses the workflow's `GITHUB_TOKEN`, so no registry credentials need
to be stored in the repository.

To require these checks before merging, protect the `master` branch in
repository settings. Require a pull request, disable direct pushes, and select
`Build and test (restaurant)`, `Build and test (orders)`,
`Container image (restaurant)`, and `Container image (orders)` as required
status checks. The publishing jobs are push-only and should not be required.
Ensure the Actions workflow permissions allow packages to be written. The
workflow requests `packages: write` only for its publishing job.

To demonstrate that the protection blocks a failing change, push a temporary
branch with a deliberately incorrect expectation in an existing test, then
open a pull request to `master`. Confirm a build-and-test check fails and the
merge button is blocked. Close the pull request without merging and delete the
temporary branch afterward.

The published image names are
`ghcr.io/<owner>/rms-restaurant` and `ghcr.io/<owner>/rms-orders`. Pull an
image using its commit SHA tag or, for the `master` branch, `latest`:

```sh
docker pull ghcr.io/<owner>/rms-restaurant:<commit-sha>
docker run --rm -p 8080:8080 -e DB_URL=jdbc:postgresql://host.docker.internal:5432/restaurant -e DB_USERNAME=restaurant -e DB_PASSWORD=restaurant ghcr.io/<owner>/rms-restaurant:<commit-sha>
```

Set the GHCR package visibility in the GitHub package settings if the images
need to be publicly pullable.
