package com.sublayer.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sublayer.api.entity.SubUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubUserMapper extends BaseMapper<SubUser> {
}