package org.tsukilc.likeservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞计数实体类
 */
@Data
@TableName("like_count")
public class LikeCount {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 目标ID（如文章ID）
     */
    private Long targetId;
    
    /**
     * 点赞类型（1:文章 2:评论）
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