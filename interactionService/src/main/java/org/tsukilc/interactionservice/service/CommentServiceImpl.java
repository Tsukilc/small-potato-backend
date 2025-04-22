package org.tsukilc.interactionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tsukilc.api.interaction.CommentService;
import org.tsukilc.api.interaction.dto.CommentDTO;
import org.tsukilc.api.interaction.dto.CreateCommentRequest;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;
import org.tsukilc.interactionservice.entity.Comment;
import org.tsukilc.interactionservice.mapper.CommentMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DubboService
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public Result<PageResult<CommentDTO>> getComments(String noteId, Integer page, Integer pageSize) {
        // 模拟实现，返回假数据
        List<CommentDTO> mockComments = createMockCommentList(noteId);
        
        return Result.success(PageResult.of(10L, mockComments, page, pageSize));
    }

    @Override
    @Transactional
    public Result<CommentDTO> createComment(String noteId, CreateCommentRequest request) {
        // 模拟实现，返回假数据
        CommentDTO mockComment = new CommentDTO();
        mockComment.setId("mock_comment_id");
        mockComment.setContent(request.getContent());
        mockComment.setUserId("mock_user_id");
        mockComment.setUsername("用户昵称");
        mockComment.setAvatar("https://via.placeholder.com/150");
        mockComment.setNoteId(noteId);
        mockComment.setCreatedAt(LocalDateTime.now());
        
        return Result.success(mockComment);
    }

    @Override
    @Transactional
    public Result<Void> deleteComment(String noteId, String commentId) {
        // 模拟实现，直接返回成功
        return Result.success();
    }
    
    // 创建模拟评论列表
    private List<CommentDTO> createMockCommentList(String noteId) {
        List<CommentDTO> comments = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            CommentDTO comment = new CommentDTO();
            comment.setId("mock_comment_id_" + i);
            comment.setContent("这是一条模拟评论 " + i);
            comment.setUserId("mock_user_id_" + i);
            comment.setUsername("用户昵称 " + i);
            comment.setAvatar("https://via.placeholder.com/150");
            comment.setNoteId(noteId);
            comment.setCreatedAt(LocalDateTime.now().minusHours(i));
            
            comments.add(comment);
        }
        
        return comments;
    }
} 