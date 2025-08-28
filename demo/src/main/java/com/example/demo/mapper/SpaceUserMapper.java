package com.example.demo.mapper;

import com.example.demo.model.entity.SpaceUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.entity.Space;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SpaceUserMapper extends BaseMapper<SpaceUser> {

}