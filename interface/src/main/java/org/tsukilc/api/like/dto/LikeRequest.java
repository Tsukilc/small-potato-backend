package org.tsukilc.api.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 点赞请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest implements Serializable {
    
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
     * 操作状态，true为点赞，false为取消点赞
     */
    private Boolean status;
} 