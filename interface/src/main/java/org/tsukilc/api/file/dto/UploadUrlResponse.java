package org.tsukilc.api.file.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取上传URL的响应
 */
@Data
public class UploadUrlResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 预签名上传URL
     */
    private String uploadUrl;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 文件访问路径 (可选，相对路径)
     */
    private String filePath;
    
    /**
     * 过期时间 (秒)
     */
    private Long expiresIn;
} 