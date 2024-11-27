package com.sublayer.api.mapper;

import com.sublayer.api.domain.vo.NftParamVO;
import com.sublayer.api.entity.SubOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SubOrderExtMapper {
    public List<SubOrder> salelistbymulti(@Param("params") List<NftParamVO> params);

    public List<SubOrder> bidlistbymulti(@Param("params") List<NftParamVO> params);
}
