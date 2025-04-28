package org.tsukilc.api.constant;

import lombok.Getter;

/**
 * 点赞类型枚举
 * 平台化设计：支持不同业务类型的点赞，如笔记、评论等
 */
@Getter
public enum LikeTypeEnum {
    
    NOTE(1, "笔记"),
    COMMENT(2, "评论"),
    REPLY(3, "回复");
    
    private final Integer code;
    private final String desc;
    
    LikeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static LikeTypeEnum getByCode(Integer code) {
        for (LikeTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 