package org.tsukilc.userservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String username;
    
    private String password;
    
    private String nickname;
    
    private String avatar;
    
    private String bio;
    
    private String gender;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 