package com.boxinggym.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教练视图对象
 */
@Data
public class CoachVO {
    private Long id;
    private Long userId;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private String specialties;
    private Integer level;
    private Integer status;
    private String imageUrl;
    private String description;
    private LocalDate hireDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
