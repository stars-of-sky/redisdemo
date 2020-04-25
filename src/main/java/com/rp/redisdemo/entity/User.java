package com.rp.redisdemo.entity;

import lombok.Data;

/**
 * Create By yrp.
 * Date:2020/3/29
 */

@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String tel;
}
