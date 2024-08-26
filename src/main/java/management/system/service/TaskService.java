package management.system.service;

import jakarta.persistence.EntityNotFoundException;
import management.system.model.Task;
import management.system.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Task with id " + taskId + " not found"));
        task.setTittle(taskDetails.getTittle());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setExecutor(taskDetails.getExecutor());
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public List<Task> getTasksByAuthor(Long authorId) {
        return taskRepository.findByAuthorId(authorId);
    }

    public List<Task> getTasksByExecutors(Long executorId) {
        return taskRepository.findByExecutorId(executorId);
    }

    public Page<Task> getTasks(String author, String executor, Pageable pageable) {
        if (author != null && executor != null) {
            return taskRepository.findByAuthorAndExecutor(author, executor, pageable);
        } else if (author != null) {
            return taskRepository.findByAuthor(author, pageable);
        } else if (executor != null) {
            return taskRepository.findByExecutor(executor, pageable);
        } else {
            return taskRepository.findAll(pageable);
        }
    }
}
