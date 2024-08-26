package management_service.mapper;

import management.system.dto.CommentDto;
import management.system.dto.TaskDto;
import management.system.dto.UserDto;
import management.system.mapper.ManagementMapperImpl;
import management.system.model.AppUser;
import management.system.model.Comment;
import management.system.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ManagementMapperTest {

    private ManagementMapperImpl managementMapper;

    @BeforeEach
    public void setUp() {
        managementMapper = new ManagementMapperImpl();
    }

    @Test
    public void testToUserDto() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);
        appUser.setEmail("test@test.com");

        UserDto userDto = managementMapper.toUserDto(appUser);

        assertNotNull(userDto);
        assertEquals(appUser.getId(), userDto.getId());
        assertEquals(appUser.getEmail(), userDto.getEmail());
    }

    @Test
    public void testToUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");

        AppUser appUser = managementMapper.toUser(userDto);

        assertNotNull(appUser);
        assertEquals(userDto.getId(), appUser.getId());
        assertEquals(userDto.getEmail(), appUser.getEmail());
    }

    @Test
    public void testToCommentDto() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");

        CommentDto commentDto = managementMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
    }

    @Test
    public void testToComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment");

        Comment comment = managementMapper.toComment(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
    }

    @Test
    public void testToTaskDto() {
        Task task = new Task();
        task.setId(1L);
        task.setTittle("test");

        TaskDto taskDto = managementMapper.toTaskDto(task);

        assertNotNull(taskDto);
        assertEquals(task.getId(), taskDto.getId());
        assertEquals(task.getTittle(), taskDto.getTittle());
    }

    @Test
    public void testToTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTittle("test");

        Task task = managementMapper.toTask(taskDto);

        assertNotNull(task);
        assertEquals(taskDto.getId(), task.getId());
        assertEquals(taskDto.getTittle(), task.getTittle());
    }
}