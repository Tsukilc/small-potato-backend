package org.tsukilc.interactionservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 点赞计数表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("like_count")
public class LikeCount {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 目标ID（如笔记ID）
     */
    private Long targetId;
    
    /**
     * 点赞类型
     * @see org.tsukilc.api.constant.LikeTypeEnum
     */
    private Integer type;
    
    /**
     * 点赞数量
     */
    private Long count;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 