package com.skillstorm.task_manager_business_service.Services;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.task_manager_business_service.DTOs.TaskRequestDTO;
import com.skillstorm.task_manager_business_service.Exceptions.BadRequestException;
import com.skillstorm.task_manager_business_service.Exceptions.InvalidReferenceException;
import com.skillstorm.task_manager_business_service.Exceptions.ResourceNotFoundException;
import com.skillstorm.task_manager_business_service.Enums.ReadStatus;
import com.skillstorm.task_manager_business_service.Enums.Priority;
import com.skillstorm.task_manager_business_service.Models.Task;
import com.skillstorm.task_manager_business_service.Repositories.TaskRepository;
import com.skillstorm.task_manager_business_service.Repositories.TeamRepository;
import com.skillstorm.task_manager_business_service.Repositories.UserRepository;
import com.skillstorm.task_manager_business_service.Enums.Status;
import tools.jackson.databind.JsonNode;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public TaskService(
        TaskRepository taskRepository,
        UserRepository userRepository,
        TeamRepository teamRepository
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public List<Task> getAllTasks(String sortBy, String direction) {
        List<Task> tasks = taskRepository.findAll();
        Comparator<Task> comparator = resolveComparator(sortBy);

        if (isDescending(direction)) {
            comparator = comparator.reversed();
        }

        return tasks.stream()
            .sorted(comparator)
            .toList();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(TaskRequestDTO request) {
        validateCreateRequest(request);
        validateTaskReferences(request);

        Task task = new Task();
        task.setAssignedUserId(request.getAssignedUserId());
        task.setAssignedTeamId(request.getAssignedTeamId());
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setReadStatus(ReadStatus.UNREAD);
        task.setDueDate(request.getDueDate());

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, JsonNode request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        validateUpdateRequest(request);

        boolean markUnread = false;

        if (request.has("assignedUserId")) {
            Long assignedUserId = parseNullableLong(request.get("assignedUserId"), "assignedUserId");
            validateAssignedUserReference(assignedUserId);

            if (!equalsNullable(assignedUserId, task.getAssignedUserId())) {
                task.setAssignedUserId(assignedUserId);
                markUnread = true;
            }
        }

        if (request.has("assignedTeamId")) {
            Long assignedTeamId = parseNullableLong(request.get("assignedTeamId"), "assignedTeamId");
            validateAssignedTeamReference(assignedTeamId);

            if (!equalsNullable(assignedTeamId, task.getAssignedTeamId())) {
                task.setAssignedTeamId(assignedTeamId);
                markUnread = true;
            }
        }

        if (request.has("title")) {
            String title = parseNullableText(request.get("title"), "title");
            if (title == null || title.trim().isEmpty()) {
                throw new BadRequestException("title cannot be blank");
            }
            task.setTitle(title.trim());
        }

        if (request.has("description")) {
            task.setDescription(parseNullableText(request.get("description"), "description"));
        }

        if (request.has("status")) {
            Status status = parseNullableEnum(request.get("status"), Status.class, "status");
            if (status != task.getStatus()) {
                task.setStatus(status);
                markUnread = true;
            }
        }

        if (request.has("priority")) {
            Priority priority = parseNullableEnum(request.get("priority"), Priority.class, "priority");
            if (priority != task.getPriority()) {
                task.setPriority(priority);
                markUnread = true;
            }
        }

        if (request.has("dueDate")) {
            task.setDueDate(parseNullableDateTime(request.get("dueDate"), "dueDate"));
        }

        if (markUnread) {
            task.setReadStatus(ReadStatus.UNREAD);
        }

        return taskRepository.save(task);
    }

    public Task markTaskAsRead(Long id, Long viewerUserId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (task.getAssignedUserId() == null || task.getAssignedUserId().equals(viewerUserId)) {
            task.setReadStatus(ReadStatus.READ);
            return taskRepository.save(task);
        }

        return task;
    }

    public void markTaskAsUnread(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        task.setReadStatus(ReadStatus.UNREAD);
        taskRepository.save(task);
    }

    private void validateTaskReferences(TaskRequestDTO request) {
        validateAssignedUserReference(request.getAssignedUserId());
        validateAssignedTeamReference(request.getAssignedTeamId());
    }

    private void validateCreateRequest(TaskRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("title is required");
        }
    }

    private void validateUpdateRequest(JsonNode request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
    }

    private void validateAssignedUserReference(Long assignedUserId) {
        if (assignedUserId != null && !userRepository.existsById(assignedUserId)) {
            throw new InvalidReferenceException("User not found with id: " + assignedUserId);
        }
    }

    private void validateAssignedTeamReference(Long assignedTeamId) {
        if (assignedTeamId != null && !teamRepository.existsById(assignedTeamId)) {
            throw new InvalidReferenceException("Team not found with id: " + assignedTeamId);
        }
    }

    private Long parseNullableLong(JsonNode node, String fieldName) {
        if (node == null || node.isNull()) {
            return null;
        }

        if (!node.canConvertToLong()) {
            throw new BadRequestException(fieldName + " must be a number or null");
        }

        return node.longValue();
    }

    private String parseNullableText(JsonNode node, String fieldName) {
        if (node == null || node.isNull()) {
            return null;
        }

        if (!node.isTextual()) {
            throw new BadRequestException(fieldName + " must be text or null");
        }

        return node.textValue();
    }

    private LocalDateTime parseNullableDateTime(JsonNode node, String fieldName) {
        if (node == null || node.isNull()) {
            return null;
        }

        if (!node.isTextual()) {
            throw new BadRequestException(fieldName + " must be an ISO datetime string or null");
        }

        try {
            return LocalDateTime.parse(node.textValue());
        } catch (DateTimeParseException exception) {
            throw new BadRequestException(fieldName + " must be an ISO datetime string");
        }
    }

    private <T extends Enum<T>> T parseNullableEnum(JsonNode node, Class<T> enumType, String fieldName) {
        if (node == null || node.isNull()) {
            return null;
        }

        if (!node.isTextual()) {
            throw new BadRequestException(fieldName + " must be a string or null");
        }

        try {
            return Enum.valueOf(enumType, node.textValue().trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(fieldName + " contains an invalid value");
        }
    }

    private boolean equalsNullable(Long left, Long right) {
        return left == null ? right == null : left.equals(right);
    }

    private Comparator<Task> resolveComparator(String sortBy) {
        Map<Long, String> userNamesById = new HashMap<>();
        Map<Long, String> teamNamesById = new HashMap<>();

        String normalizedSortBy = sortBy == null ? "updated" : sortBy.trim().toLowerCase();

        return switch (normalizedSortBy) {
            case "id" -> Comparator.comparing(Task::getId, Comparator.nullsLast(Long::compareTo));
            case "status" -> Comparator.comparing(
                task -> task.getStatus() == null ? "" : task.getStatus().name(),
                String.CASE_INSENSITIVE_ORDER
            );
            case "priority" -> Comparator.comparing(
                task -> task.getPriority() == null ? "" : task.getPriority().name(),
                String.CASE_INSENSITIVE_ORDER
            );
            case "team" -> {
                teamRepository.findAll().forEach(team -> teamNamesById.put(team.getId(), safe(team.getName())));
                yield Comparator.comparing(
                    task -> safe(teamNamesById.get(task.getAssignedTeamId())),
                    String.CASE_INSENSITIVE_ORDER
                );
            }
            case "assignee" -> {
                userRepository.findAll().forEach(user -> userNamesById.put(user.getId(), safe(user.getUsername())));
                yield Comparator.comparing(
                    task -> safe(userNamesById.get(task.getAssignedUserId())),
                    String.CASE_INSENSITIVE_ORDER
                );
            }
            case "due" -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(LocalDateTime::compareTo));
            case "created" -> Comparator.comparing(Task::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
            case "updated" -> Comparator.comparing(Task::getUpdatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
            default -> throw new BadRequestException(
                "Unsupported sortBy value. Use one of: id, status, priority, team, assignee, due, updated, created"
            );
        };
    }

    private boolean isDescending(String direction) {
        return direction != null && direction.trim().equalsIgnoreCase("desc");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
