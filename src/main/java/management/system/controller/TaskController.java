package management.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import management.system.dto.TaskDto;
import management.system.mapper.ManagementMapper;
import management.system.model.Task;
import management.system.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ManagementMapper managementMapper;

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public TaskDto createTask(@Valid @RequestBody TaskDto taskDto) {
        Task task = managementMapper.toTask(taskDto);
        Task createdTask = taskService.createTask(task);
        return managementMapper.toTaskDto(createdTask);
    }

    @Operation(summary = "Update an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{taskId}")
    public TaskDto updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskDto taskDetails) {
        Task task = managementMapper.toTask(taskDetails);
        Task updateTask = taskService.updateTask(taskId, task);
        return managementMapper.toTaskDto(updateTask);
    }

    @Operation(summary = "Get tasks by author ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @GetMapping("/author/{authorId}")
    public List<TaskDto> getTasksByAuthor(@PathVariable Long authorId) {
        return taskService.getTasksByAuthor(authorId).stream()
                .map(managementMapper::toTaskDto)
                .toList();
    }

    @Operation(summary = "Get tasks by executor ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Executor not found")
    })
    @GetMapping("/executor/{executorId}")
    public List<TaskDto> getTasksByExecutors(@PathVariable Long executorId) {
        return taskService.getTasksByExecutors(executorId).stream()
                .map(managementMapper::toTaskDto)
                .toList();
    }

    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get paginated tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping
    public Page<TaskDto> getTasks(
            @RequestParam Optional<String> author,
            @RequestParam Optional<String> executor,
            @RequestParam Integer page,
            @RequestParam Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = taskService.getTasks(author.orElseThrow(() ->
                        new IllegalArgumentException("Author is required")),
                executor.orElseThrow(() ->
                        new IllegalArgumentException("Executor is required")),
                pageable);
        return tasks.map(managementMapper::toTaskDto);
    }
}
