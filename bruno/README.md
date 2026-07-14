# TaskFlow Bruno Collection

An API testing collection for [Bruno](https://www.usebruno.com/), covering every endpoint of the
TaskFlow API, including error cases (401/403/404/409/400).

## Usage

1. Open Bruno → "Open Collection" → select `bruno/TaskFlow-API`.
2. Select the **Local** environment (top right in Bruno).
3. Make sure the app is running (`docker compose up` from the repo root) and, if you changed
   `ADMIN_PASSWORD` in your `.env`, update `adminPassword` in the **Local** environment to match.
4. Run the whole collection top to bottom (folders are numbered `00`–`04` and requests within a
   folder are numbered too), or open individual requests.

## Folder overview

- **00 - Setup** — logs in as the seeded admin, creates a `MEMBER` and a `REVIEWER` test user,
  and logs in as both. Populates `adminToken` / `memberToken` / `reviewerToken` /
  `memberId` / `reviewerId` env vars used by every other folder.
- **01 - Auth** — login error cases, plus a full logout round-trip proving the Redis token
  blocklist actually rejects a revoked token on the next request.
- **02 - Users** — user creation/listing/lookup, including RBAC (`ADMIN`-only create,
  `ADMIN`/`REVIEWER`-only list) and validation/conflict error cases.
- **03 - Tasks** — the full task lifecycle: create → list (filter/sort) → get → replace (PUT) →
  update (PATCH) → status transitions through the whole workflow (`TODO → IN_PROGRESS →
  IN_REVIEW → DONE`) → delete. Includes role-based transition errors (403), invalid-transition
  errors (409), optimistic-locking version conflicts (409), and the terminal-state update lock
  (409) once a task reaches `DONE`.
- **04 - Statistics** — the aggregated statistics endpoint, restricted to `ADMIN`/`REVIEWER`.

## Important: this is not idempotent

**00 - Setup** creates users with fixed email addresses (see the **Local** environment). Running
it twice against the same database will fail on step 2/3 with `409 Conflict` (email already
exists), since those users already exist from the first run.

To re-run the whole collection from a clean slate:

```bash
docker compose down -v
docker compose up -d
```

This wipes the Postgres volume (re-seeding only the initial admin) so Setup can create its
`MEMBER`/`REVIEWER` users again.

If you only want to re-run **03 - Tasks** or **04 - Statistics** without recreating users, you
can skip **00 - Setup** as long as `adminToken`/`memberToken`/`reviewerToken`/`memberId` are
still set from a previous run in the same Bruno session (tokens expire after `JWT_EXPIRATION_SECONDS`,
1 hour by default — re-run the login requests in **00 - Setup** if you get unexpected 401s).
