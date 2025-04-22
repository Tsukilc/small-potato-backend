package org.tsukilc.api.interaction.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateCommentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String content;
} 