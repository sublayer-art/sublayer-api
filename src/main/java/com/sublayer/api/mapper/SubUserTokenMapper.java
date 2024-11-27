package com.sublayer.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sublayer.api.entity.SubUser;
import com.sublayer.api.entity.SubUserToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubUserTokenMapper extends BaseMapper<SubUserToken> {
}
