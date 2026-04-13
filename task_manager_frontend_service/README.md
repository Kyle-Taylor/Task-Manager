# Task Manager Frontend Service

Angular frontend microservice starter for the Task Manager platform.

## Current status

- Scaffolded with Angular CLI
- Styled landing page replaces the default Angular placeholder
- Central API base URL points to `http://localhost:8082`
- Intended backend is `task_manager_business_service`

## Install

The workspace was created with `--skip-install`, so install packages first:

```bash
npm install
```

## Run

Start the Spring backend first on `http://localhost:8082`, then run:

```bash
npm start
```

Then open `http://localhost:4200`.

## Backend expectation

This frontend is currently wired for the merged business service:

- `GET /api/tasks`
- `GET /api/profiles/users`
- `GET /api/profiles/teams`
- `GET /api/comments/task/{taskId}`
- `POST /api/tasks`
- `PATCH /api/tasks/{id}`
- `PATCH /api/comments/{commentId}`
- `DELETE /api/comments/{commentId}`

Angular dev server proxies `/api` requests to `http://localhost:8082` through `proxy.conf.json`.

## Recommended next frontend work

1. Replace the static dashboard route with a routed layout and dedicated task detail route.
2. Add create/edit views for users and teams.
3. Add comment creation when the backend supports it.
4. Add auth views and route guards after the auth microservice exists.
