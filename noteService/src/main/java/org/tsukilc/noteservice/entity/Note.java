package org.tsukilc.noteservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_note")
public class Note {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String title;
    
    private String content;
    
    private String userId;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer collectCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 