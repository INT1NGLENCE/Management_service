package management.system.mapper;

import management.system.dto.CommentDto;
import management.system.dto.TaskDto;
import management.system.dto.UserDto;
import management.system.model.AppUser;
import management.system.model.Comment;
import management.system.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ManagementMapper {
    //UserMapper
    UserDto toUserDto(AppUser appUser);

    AppUser toUser(UserDto userDto);

    //CommentMapper
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentDto commentDto);

    //TaskMapper
    TaskDto toTaskDto(Task task);

    Task toTask(TaskDto taskDto);
}
