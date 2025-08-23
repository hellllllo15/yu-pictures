package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.example.demo.model.entity.Space;

public interface SpaceService extends IService<Space> {

//判断是更新还是创建空间
    public void validSpace(Space space, boolean add);

    public void fillSpaceBySpaceLevel(Space space);
}
