package com.example.demo.model.dto.spaceUser;

import lombok.Data;

import java.io.Serializable;


/// 编辑空间成؜员请求，给空间管理‏员使用，可以设置空​间成员的角色：
@Data
public class SpaceUserEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}
