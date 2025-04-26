package org.tsukilc.api.file.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取上传URL的请求
 */
@Data
public class UploadUrlRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件类型 (MIME类型)
     */
    private String fileType;
    
    /**
     * 文件大小 (字节)
     */
    private Long fileSize;
    
    /**
     * 文件分类 (可选，例如: avatar, note-image, video等)
     */
    private String category;
} 