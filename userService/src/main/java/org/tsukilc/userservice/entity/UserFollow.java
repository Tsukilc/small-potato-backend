package org.tsukilc.userservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user_follow")
public class UserFollow {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String userId;
    
    private String followUserId;
    
    private LocalDateTime createdAt;
} 