package com.example.demo.model.dto.picture.analyze;

import lombok.Data;

import java.io.Serializable;


///按存储使用量排序查询前 N 个空间
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 排名前 N 的空间
     */
    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}
