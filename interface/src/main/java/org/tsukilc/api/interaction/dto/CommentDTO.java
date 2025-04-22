package org.tsukilc.api.interaction.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String content;
    private String userId;
    private String username;
    private String avatar;
    private String noteId;
    private LocalDateTime createdAt;
} 