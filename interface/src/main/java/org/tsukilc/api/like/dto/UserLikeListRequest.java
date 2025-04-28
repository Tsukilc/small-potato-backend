package org.tsukilc.api.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户点赞列表请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeListRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 点赞类型，参考LikeTypeEnum
     */
    private Integer type;
    
    /**
     * 页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
} 