package org.tsukilc.interactionservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_comment")
public class Comment {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String content;
    
    private String userId;
    
    private String noteId;
    
    private String parentId;
    
    private Integer likeCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 