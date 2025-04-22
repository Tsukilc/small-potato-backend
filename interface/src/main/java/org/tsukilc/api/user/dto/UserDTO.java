package org.tsukilc.api.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String bio;
    private String gender;
    private Integer following;
    private Integer followers;
    private Integer noteCount;
    private Integer likeCount;
    private Boolean isFollowed;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 