package com.sublayer.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sublayer.api.entity.SubSystem;
import com.sublayer.api.entity.SubUserLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubUserLogMapper extends BaseMapper<SubUserLog> {
}
