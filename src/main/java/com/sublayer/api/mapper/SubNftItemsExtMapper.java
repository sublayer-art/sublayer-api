package com.sublayer.api.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sublayer.api.domain.vo.NftItemsVo;
import com.sublayer.api.domain.vo.NftParamVO;
import com.sublayer.api.entity.SubNftItems;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SubNftItemsExtMapper {
    @MapKey("nftId")
    Map<Long, SubNftItems> getIdAndItemsMap(@Param(Constants.WRAPPER) Wrapper ew);

    public IPage<NftItemsVo> list(@Param("owner")String owner, @Param("tokenList") List<String> tokenList, @Param("token")String token, @Param("tokenId")String tokenId, IPage<NftItemsVo> page);

    Long count(@Param("owner")String owner, @Param("token")String token, @Param("tokenId")String tokenId);

    NftItemsVo get(@Param("owner")String owner, @Param("token")String token, @Param("tokenId")String tokenId);

    public List<SubNftItems> listByMulti(@Param("params") List<NftParamVO> params);

    Integer countByCondition(@Param("time")Long time, @Param("onsell")int onsell);
}
