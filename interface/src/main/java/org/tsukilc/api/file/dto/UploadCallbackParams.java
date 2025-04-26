package org.tsukilc.api.file.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传完成回调参数
 */
@Data
public class UploadCallbackParams implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 原始文件名
     */
    private String originalFileName;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 文件分类
     */
    private String category;
} 