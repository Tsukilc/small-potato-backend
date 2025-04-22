package org.tsukilc.common.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private List<T> list;
    private Integer page;
    private Integer pageSize;

    public static <T> PageResult<T> of(Long total, List<T> list, Integer page, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setList(list);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }
} 