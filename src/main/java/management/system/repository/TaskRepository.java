package management.system.repository;

import management.system.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAuthorId(Long authorId);

    List<Task> findByExecutorId(Long assigneeId);

    Page<Task> findByAuthor(String author, Pageable pageable);

    Page<Task> findByExecutor(String executor, Pageable pageable);

    Page<Task> findByAuthorAndExecutor(String author, String executor, Pageable pageable);
}
