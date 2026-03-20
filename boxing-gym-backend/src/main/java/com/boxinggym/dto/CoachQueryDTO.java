package com.boxinggym.dto;

import lombok.Data;

/**
 * 教练查询条件
 */
@Data
public class CoachQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private String name;
    private Integer status;
}
