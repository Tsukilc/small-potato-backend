package org.tsukilc.api.note.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreateNoteRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private List<String> tags;
    private List<String> images;
} 