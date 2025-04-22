package org.tsukilc.api.note.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoteDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String content;
    private List<String> images;
    private String userId;
    private String username;
    private String avatar;
    private List<String> tags;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Boolean isLiked;
    private Boolean isCollected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 