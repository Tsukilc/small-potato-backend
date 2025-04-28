package org.tsukilc.likeservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞记录实体类
 */
@Data
@TableName("like_record")
public class LikeRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 被点赞对象ID
     */
    private Long targetId;
    
    /**
     * 点赞类型（1:文章 2:评论）
     */
    private Integer type;
    
    /**
     * 点赞状态（0:取消点赞 1:已点赞）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 