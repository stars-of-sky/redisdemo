package com.rp.redisdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Create By yrp.
 * Date:2020/3/29
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @NotNull
    private String name;
    @Range(min = 0, max = 100)
    private Integer age;
    private String tel;
}
