package org.tsukilc.noteservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_tag")
public class Tag {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String name;
    
    private Integer useCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 