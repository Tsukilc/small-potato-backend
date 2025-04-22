package org.tsukilc.api.search.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchSuggestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String keyword;
    private String type; // USER、NOTE、TAG
} 