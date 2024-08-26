package management.system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import management.system.model.Priority;
import management.system.model.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    @NotNull(message = "Id can't be null")
    private Long id;
    @NotEmpty(message = "Tittle is can't be required")
    private String tittle;
    private Status status = Status.TO_DO;
    @NotNull(message = "Priority can't be null")
    private Priority priority;
    @NotNull(message = "Author can't be null")
    private Long authorId;
    private Long executorId;
}
