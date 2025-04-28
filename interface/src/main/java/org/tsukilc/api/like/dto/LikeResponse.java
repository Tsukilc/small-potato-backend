package org.tsukilc.api.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 目标ID（如笔记ID）
     */
    private Long targetId;
    
    /**
     * 点赞类型，参考LikeTypeEnum
     */
    private Integer type;
    
    /**
     * 是否已点赞
     */
    private Boolean liked;
    
    /**
     * 点赞数量
     */
    private Long count;
    
    /**
     * 点赞时间
     */
    private LocalDateTime createTime;
} 