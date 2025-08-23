package com.example.demo.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.entity.Picture;
import com.example.demo.model.entity.Space;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpaceMapper extends BaseMapper<Space> {
}