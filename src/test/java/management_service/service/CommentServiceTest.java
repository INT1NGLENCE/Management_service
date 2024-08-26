package management_service.service;

import management.system.model.Comment;
import management.system.repository.CommentRepository;
import management.system.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void testCreateComment() {
        Comment comment = new Comment();
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment createdComment = commentService.createComment(comment);

        assertEquals(comment, createdComment);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    public void testGetCommentsByTaskId() {
        Long taskId = 1L;
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(2L);

        List<Comment> expectedComments = Arrays.asList(comment1, comment2);
        when(commentRepository.findByTaskId(taskId)).thenReturn(expectedComments);

        List<Comment> actualComments = commentService.getCommentsByTaskId(taskId);

        assertEquals(expectedComments.size(), actualComments.size());
        assertEquals(expectedComments, actualComments);
        verify(commentRepository, times(1)).findByTaskId(taskId);
    }
}
