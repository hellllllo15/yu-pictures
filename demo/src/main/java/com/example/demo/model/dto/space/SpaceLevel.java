package com.example.demo.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;


//给用户展示空间级别信息
@Data
@AllArgsConstructor
public class SpaceLevel {

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;
}
