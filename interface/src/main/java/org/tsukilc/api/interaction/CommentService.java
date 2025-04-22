package org.tsukilc.api.interaction;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.interaction.dto.CommentDTO;
import org.tsukilc.api.interaction.dto.CreateCommentRequest;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;

@DubboService
@RequestMapping("/api/notes/{noteId}/comments")
public interface CommentService {

    /**
     * 获取笔记评论
     */
    @GetMapping
    Result<PageResult<CommentDTO>> getComments(
            @PathVariable("noteId") String noteId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize);

    /**
     * 发表评论
     */
    @PostMapping
    Result<CommentDTO> createComment(
            @PathVariable("noteId") String noteId,
            @RequestBody CreateCommentRequest request
);

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    Result<Void> deleteComment(
            @PathVariable("noteId") String noteId,
            @PathVariable("commentId") String commentId);
} 