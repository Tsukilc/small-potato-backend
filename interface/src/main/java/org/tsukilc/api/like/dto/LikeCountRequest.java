package org.tsukilc.api.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 点赞计数请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeCountRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 目标ID列表
     */
    private List<Long> targetIds;
    
    /**
     * 点赞类型，参考LikeTypeEnum
     */
    private Integer type;
} 