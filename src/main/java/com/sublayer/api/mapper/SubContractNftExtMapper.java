package com.sublayer.api.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sublayer.api.domain.dto.SearchNftParamDto;
import com.sublayer.api.domain.vo.NftParamVO;
import com.sublayer.api.domain.vo.SubContractNftVo;
import com.sublayer.api.domain.vo.SubContractNftWithBidVO;
import com.sublayer.api.entity.SubContractNft;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface SubContractNftExtMapper {
    public void setNftVerify();

    public IPage<SubContractNft> findVerifyNft(IPage<SubContractNft> page, @Param("address")String address,
                                              @Param("categoryId")Long categoryId,
                                              @Param("contracts") List<String> contracts,
                                              @Param("payToken")String payToken,
                                              @Param("minPrice") BigDecimal minPrice,
                                              @Param("maxPrice")BigDecimal maxPrice,
                                              @Param("search")String search,
                                              @Param("sort")String sort,
                                              @Param("order")String order,
                                              @Param("isSell")Boolean isSell,
                                              @Param("onsellType")Integer onsellType
    );

    public IPage<SubContractNft> findContractNft(IPage<SubContractNft> page, @Param("address")String address, @Param("isSell")Boolean isSell);

    public IPage<SubContractNft> findOnsaleNft(IPage<SubContractNft> page, @Param("owner")String owner);

    public IPage<SubContractNft> findCollectionNft(IPage<SubContractNft> page, @Param("params") SearchNftParamDto params);

    public List<SubContractNft> nftlist(@Param("params")SearchNftParamDto params);

    public IPage<SubContractNft> searchNft(IPage<SubContractNft> page, @Param("name")String name);

    public IPage<SubContractNft> findCreatorNft(IPage<SubContractNft> page, @Param("creator")String creator);

    public Integer countCreatorNft(@Param("creator")String creator);

    public IPage<SubContractNft> findLikeNft(IPage<SubContractNft> page, @Param("userAddress")String userAddress);

    public List<SubContractNft> listByMulti(@Param("params") List<NftParamVO> params);
    /**
     * @param address
     * @return
     */
    public Integer countContractOnsale(@Param("address")String address);

    public String floorContractOnsale(@Param("address")String address);

    public IPage<SubContractNft> findOnAuctionNft(IPage<SubContractNft> page);

    @SuppressWarnings("rawtypes")
    IPage<SubContractNftVo> getList(IPage<SubContractNftVo> page, @Param(Constants.WRAPPER) Wrapper ew);

    //获取所有合约分类
    @SuppressWarnings("rawtypes")
    List<String> getAllAddress(@Param(Constants.WRAPPER) Wrapper ew);

    @SuppressWarnings("rawtypes")
    List<SubContractNft> getByAddress(@Param(Constants.WRAPPER) Wrapper ew);

    /**
     * @param params
     * @param pageInfo
     * @return
     */
    public IPage<SubContractNft> listByTokenAndTokenId(@Param("params") Set<NftParamVO> params, IPage<SubContractNft> pageInfo);

    public List<SubContractNft> listByTokenAndTokenId(@Param("params") Set<NftParamVO> params);

    IPage<SubContractNftWithBidVO> findNftWithBidNum(@Param("nftName") String name, @Param("categoryId") Long categoryId, IPage<SubContractNftWithBidVO> page);


    /**
     *
     * @param time
     * @param type
     * @return
     */
    Integer countByCondition(@Param("time")Long time, @Param("type")Integer type);
}
