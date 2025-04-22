package org.tsukilc.searchservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_hot_search")
public class HotSearch {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String keyword;
    
    private String searchType; // USER, NOTE, TAG
    
    private Integer count;
    
    private Integer score;
    
    private Integer rank;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 