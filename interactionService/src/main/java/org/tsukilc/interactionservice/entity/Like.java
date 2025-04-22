package org.tsukilc.interactionservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_like")
public class Like {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String userId;
    
    private String targetId;
    
    private String targetType; // NOTE, COMMENT
    
    private LocalDateTime createdAt;
} 