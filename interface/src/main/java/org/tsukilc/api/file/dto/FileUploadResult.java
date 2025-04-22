package org.tsukilc.api.file.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileUploadResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String url;
} 