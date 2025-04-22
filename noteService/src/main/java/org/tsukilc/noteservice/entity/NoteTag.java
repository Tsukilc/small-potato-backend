package org.tsukilc.noteservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_note_tag")
public class NoteTag {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String noteId;
    
    private String tagId;
    
    private LocalDateTime createdAt;
} 