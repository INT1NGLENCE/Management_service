package management_service.controller;

import management.system.controller.TaskController;
import management.system.dto.TaskDto;
import management.system.mapper.ManagementMapper;
import management.system.model.Task;
import management.system.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private ManagementMapper managementMapper;

    @InjectMocks
    private TaskController taskController;

    @Test
    public void testCreateTask() {
        TaskDto taskDto = new TaskDto();
        Task task = new Task();
        when(managementMapper.toTask(any(TaskDto.class))).thenReturn(task);
        when(taskService.createTask(any(Task.class))).thenReturn(task);
        when(managementMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskController.createTask(taskDto);
        assertEquals(taskDto, result);
        verify(taskService, times(1)).createTask(task);
        verify(managementMapper, times(1)).toTaskDto(task);
    }

    @Test
    public void testUpdateTask() {
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto();
        Task task = new Task();
        when(managementMapper.toTask(any(TaskDto.class))).thenReturn(task);
        when(taskService.updateTask(anyLong(), any(Task.class))).thenReturn(task);
        when(managementMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskController.updateTask(taskId, taskDto);
        assertEquals(taskDto, result);
        verify(taskService, times(1)).updateTask(taskId, task);
        verify(managementMapper, times(1)).toTaskDto(task);
    }

    @Test
    public void testGetTasksByAuthor() {
        Long authorId = 1L;
        TaskDto taskDto = new TaskDto();
        List<Task> tasks = List.of(new Task());
        when(taskService.getTasksByAuthor(anyLong())).thenReturn(tasks);
        when(managementMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        List<TaskDto> result = taskController.getTasksByAuthor(authorId);
        assertEquals(1, result.size());
        assertEquals(taskDto, result.get(0));
        verify(taskService, times(1)).getTasksByAuthor(authorId);
        verify(managementMapper, times(1)).toTaskDto(tasks.get(0));
    }

    @Test
    public void testGetTasksByExecutors() {
        Long executorId = 1L;
        TaskDto taskDto = new TaskDto();
        List<Task> tasks = List.of(new Task());
        when(taskService.getTasksByExecutors(anyLong())).thenReturn(tasks);
        when(managementMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        List<TaskDto> result = taskController.getTasksByExecutors(executorId);
        assertEquals(1, result.size());
        assertEquals(taskDto, result.get(0));
        verify(taskService, times(1)).getTasksByExecutors(executorId);
        verify(managementMapper, times(1)).toTaskDto(tasks.get(0));
    }

    @Test
    public void testDeleteTask() {
        Long taskId = 1L;
        ResponseEntity<Void> response = taskController.deleteTask(taskId);
        assertEquals(ResponseEntity.noContent().build(), response);
        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    public void testGetTasks() {
        String author = "author1";
        String executor = "executor1";
        PageRequest pageable = PageRequest.of(0, 10);
        List<Task> tasks = List.of(new Task());
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());
        TaskDto taskDto = new TaskDto();

        when(taskService.getTasks(anyString(), anyString(), any(PageRequest.class))).thenReturn(taskPage);
        when(managementMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        Page<TaskDto> result = taskController.getTasks(Optional.of(author), Optional.of(executor), 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(taskDto, result.getContent().get(0));
        verify(taskService, times(1)).getTasks(eq(author), eq(executor), any(PageRequest.class));
        verify(managementMapper, times(1)).toTaskDto(tasks.get(0));
    }
}