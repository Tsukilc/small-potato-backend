package org.tsukilc.interactionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeMessage implements Serializable {
    
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
     * 点赞类型
     */
    private Integer type;
    
    /**
     * 操作状态，true为点赞，false为取消点赞
     */
    private Boolean status;
    
    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
} 