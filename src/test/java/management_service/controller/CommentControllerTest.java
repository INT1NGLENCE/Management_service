package management_service.controller;

import management.system.controller.CommentController;
import management.system.dto.CommentDto;
import management.system.mapper.ManagementMapper;
import management.system.model.AppUser;
import management.system.model.Comment;
import management.system.model.Task;
import management.system.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private ManagementMapper managementMapper;

    @InjectMocks
    private CommentController commentController;

    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment text");
        commentDto.setTask(new Task());
        commentDto.setAuthor(new AppUser());

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment text");
        comment.setTask(new Task());
        comment.setAuthor(new AppUser());
    }

    @Test
    public void createComment() {
        when(managementMapper.toComment(any(CommentDto.class))).thenReturn(comment);
        when(commentService.createComment(any(Comment.class))).thenReturn(comment);
        when(managementMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentController.createComment(commentDto);

        assertEquals(commentDto, result);

        verify(managementMapper).toComment(any(CommentDto.class));
        verify(commentService).createComment(any(Comment.class));
        verify(managementMapper).toCommentDto(any(Comment.class));
    }

    @Test
    public void getCommentsByTaskId() {
        Long taskId = 1L;

        List<Comment> comments = Collections.singletonList(comment);
        List<CommentDto> commentDtos = Collections.singletonList(commentDto);
        when(commentService.getCommentsByTaskId(taskId)).thenReturn(comments);
        when(managementMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);

        List<CommentDto> result = commentController.getCommentsByTaskId(taskId);

        assertEquals(commentDtos, result);

        verify(commentService).getCommentsByTaskId(taskId);
        verify(managementMapper, times(comments.size())).toCommentDto(any(Comment.class));
    }
}
