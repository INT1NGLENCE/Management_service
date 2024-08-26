package management_service.service;

import jakarta.persistence.EntityNotFoundException;
import management.system.model.Status;
import management.system.model.Task;
import management.system.repository.TaskRepository;
import management.system.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    public void createTask_success() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertThat(createdTask).isEqualTo(task);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void updateTask_success() {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setTittle("Old Title");
        existingTask.setStatus(Status.TO_DO);
        Task taskDetails = new Task();
        taskDetails.setTittle("New Title");
        taskDetails.setStatus(Status.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        Task updatedTask = taskService.updateTask(taskId, taskDetails);

        assertThat(updatedTask.getTittle()).isEqualTo("New Title");
        assertThat(updatedTask.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    public void updateTask_throwException() {
        Long taskId = 1L;
        Task taskDetails = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(taskId, taskDetails));
    }

    @Test
    public void deleteTask_success() {
        Long taskId = 1L;

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    public void getTasksByAuthor_success() {
        Long authorId = 1L;
        List<Task> tasks = Collections.singletonList(new Task());
        when(taskRepository.findByAuthorId(authorId)).thenReturn(tasks);

        List<Task> retrievedTasks = taskService.getTasksByAuthor(authorId);

        assertThat(retrievedTasks).isEqualTo(tasks);
    }

    @Test
    public void getTasksByExecutors_success() {
        Long executorId = 1L;
        List<Task> tasks = Collections.singletonList(new Task());
        when(taskRepository.findByExecutorId(executorId)).thenReturn(tasks);

        List<Task> retrievedTasks = taskService.getTasksByExecutors(executorId);

        assertThat(retrievedTasks).isEqualTo(tasks);
    }

    @Test
    public void getTasks_withAuthorAndExecutor() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> tasks = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskRepository.findByAuthorAndExecutor(anyString(), anyString(), eq(pageable))).thenReturn(tasks);

        Page<Task> result = taskService.getTasks("author", "executor", pageable);

        assertThat(result).isEqualTo(tasks);
    }

    @Test
    public void getTasks_withAuthorOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> tasks = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskRepository.findByAuthor(anyString(), eq(pageable))).thenReturn(tasks);

        Page<Task> result = taskService.getTasks("author", null, pageable);

        assertThat(result).isEqualTo(tasks);
    }

    @Test
    public void getTasks_withExecutorOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> tasks = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskRepository.findByExecutor(anyString(), eq(pageable))).thenReturn(tasks);

        Page<Task> result = taskService.getTasks(null, "executor", pageable);

        assertThat(result).isEqualTo(tasks);
    }

    @Test
    public void getTasks_empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> tasks = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskRepository.findAll(pageable)).thenReturn(tasks);

        Page<Task> result = taskService.getTasks(null, null, pageable);

        assertThat(result).isEqualTo(tasks);
    }
}