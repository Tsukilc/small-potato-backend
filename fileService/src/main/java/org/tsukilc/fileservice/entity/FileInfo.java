package org.tsukilc.fileservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_file_info")
public class FileInfo {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String originalName;
    
    private String fileName;
    
    private String url;
    
    private String path;
    
    private Long size;
    
    private String type;
    
    private String extension;
    
    private String md5;
    
    private String userId;
    
    private String fileType; // NOTE_IMAGE, AVATAR, VIDEO
    
    private LocalDateTime createdAt;
} 