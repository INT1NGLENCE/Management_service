package management.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import management.system.dto.CommentDto;
import management.system.mapper.ManagementMapper;
import management.system.model.Comment;
import management.system.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    private final ManagementMapper managementMapper;

    @Operation(summary = "Create a new comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = managementMapper.toComment(commentDto);
        Comment createdComment = commentService.createComment(comment);
        return managementMapper.toCommentDto(createdComment);
    }

    @Operation(summary = "Get comments by task ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments received successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/task/{taskId}")
    public List<CommentDto> getCommentsByTaskId(@PathVariable Long taskId) {
        return commentService.getCommentsByTaskId(taskId).stream()
                .map(managementMapper::toCommentDto)
                .toList();
    }
}
