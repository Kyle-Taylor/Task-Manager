# Endpoints

## Services

- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8080`
- Business: `http://localhost:8082`

## Business Service

`task_manager_business_service` now owns the business-domain tables together: `users`, `teams`, `tasks`, and `task_comments`.

### Users

- `GET /api/profiles/users` - get all users
- `GET /api/profiles/users/{id}` - get one user
- `POST /api/profiles/users` - create user
- `PATCH /api/profiles/users/{id}` - update user

Example:

```json
{
  "teamId": 1,
  "username": "alice",
  "email": "alice@example.com"
}
```

### Teams

- `GET /api/profiles/teams` - get all teams
- `GET /api/profiles/teams/{id}` - get one team
- `POST /api/profiles/teams` - create team
- `PATCH /api/profiles/teams/{id}` - update team

Example:

```json
{
  "name": "Engineering",
  "description": "Builds and maintains the platform",
  "teamLeadId": null
}
```

### Tasks

- `GET /api/tasks` - get all tasks
- `GET /api/tasks/{id}` - get one task
- `POST /api/tasks` - create task
- `PATCH /api/tasks/{id}` - update task
- `PATCH /api/tasks/{id}/read?viewerUserId={userId}` - mark a task as read when the opener is allowed to clear it

Example:

```json
{
  "assignedUserId": 1,
  "assignedTeamId": 1,
  "title": "Finish API integration",
  "description": "Keep task validation inside the business service",
  "status": "OPEN",
  "priority": "HIGH",
  "dueDate": "2026-04-10T17:00:00"
}
```

Task responses now also include `readStatus` with values `READ` or `UNREAD`.

### Comments

- `GET /api/comments/task/{taskId}` - get comments for a task
- `PATCH /api/comments/{commentId}` - update comment text
- `DELETE /api/comments/{commentId}` - delete comment

`PATCH /api/comments/{commentId}` uses plain text in the body, not a JSON object.

## Error Codes

- `200 OK` - request succeeded
  Used for successful `GET`, `POST`, and `PATCH` requests in the current controllers.

- `204 No Content` - delete succeeded
  Used for `DELETE /api/comments/{commentId}`.

- `400 Bad Request` - request data is invalid
  Use when the body is malformed, required values are missing, enums are invalid, or a task references a missing user or team.

- `404 Not Found` - requested resource does not exist
  Use when a user, team, task, or comment ID is not found.

- `409 Conflict` - request conflicts with existing data
  Use for duplicate team names, duplicate usernames, duplicate emails, or other unique constraint violations.

- `500 Internal Server Error` - unexpected server error
  Use only for unhandled exceptions, database outages, or unexpected failures.

## When To Use Them

- Creating a task with a user ID that does not exist: `400`
- Updating a task that does not exist: `404`
- Creating a team named `Engineering` when that name already exists: `409`
- Creating a user with an email already in use: `409`
- Deleting a comment successfully: `204`
- Database connection failure: `500`
