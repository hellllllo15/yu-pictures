package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.dto.space.SpaceAddRequest;
import com.example.demo.model.dto.space.SpaceQueryRequest;
import com.example.demo.model.dto.spaceUser.SpaceUserAddRequest;
import com.example.demo.model.dto.spaceUser.SpaceUserQueryRequest;
import com.example.demo.model.entity.Space;
import com.example.demo.model.entity.SpaceUser;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.SpaceUserVO;
import com.example.demo.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SpaceUserService extends IService<SpaceUser> {



    /**
     * 创建空间成员
     *
     * @param spaceUserAddRequest
     *
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验空间
     *
     * @param spaceUser
     * @param add   是否为创建时检验
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取空间成员包装类（单条）
     *
     * @param spaceUser
     * @param request
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间成员包装类（列表）
     *
     * @param spaceUsers
     * @param request
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserslist);

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);


}