package org.tsukilc.api.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String nickname;
} 