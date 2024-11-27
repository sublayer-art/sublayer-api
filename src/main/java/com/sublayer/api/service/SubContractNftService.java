package com.sublayer.api.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sublayer.api.constants.CommonStatus;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.domain.dto.*;
import com.sublayer.api.domain.vo.*;
import com.sublayer.api.entity.*;
import com.sublayer.api.manager.SubContractNftManager;
import com.sublayer.api.manager.SubOrderManager;
import com.sublayer.api.mapper.SubContractNftExtMapper;
import com.sublayer.api.mapper.SubNftItemsExtMapper;
import com.sublayer.api.utils.DappWeb3jUtil;
import com.sublayer.api.utils.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubContractNftService {
    private static final Logger logger = LoggerFactory.getLogger(SubContractNftService.class);

    @Autowired
    IBaseService baseService;

    @Autowired
    private SubNftCategoryService subNftCategoryService;

    @Autowired
    private SubSystemService subSystemService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private SubUserService subUserService;

    @Autowired
    private SubContractService subContractService;

    @Autowired
    private SubOrderManager subOrderManager;

    @Autowired
    private SubContractNftManager subContractNftManager;

    @Autowired
    private SubContractNftExtMapper subContractNftExtMapper;

    public IPage<SubContractNft> findCollections(IPage<SubContractNft> pageInfo) {
        QueryWrapper<SubContractNft> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContractNft.IS_SYNC, true).eq(BaseEntity.DELETED, false);
        return baseService.findByPage(SubContractNft.class, wrapper, pageInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public SubContractNft save(NftInfo nft) {
        nft = uploadMetadata(nft);
        if (null == nft.getCategoryId()) {
            SubNftCategory nftCategory = subNftCategoryService.getDefault();
            if(null != nftCategory){
                nft.setContractId(nftCategory.getId());
            }
        }
        return subContractNftManager.create(nft);
    }

    private NftInfo uploadMetadata(NftInfo nft) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", nft.getName());
        map.put("description", nft.getDescription());
        SubStorage storage = baseService.getById(SubStorage.class, nft.getStorageId());
        if (StringUtils.isEmpty(nft.getAnimUrl())) {
            map.put("animation_url", "");
            map.put("image", storage.getIpfshash());
        } else {
            map.put("image", storage.getIpfshash() + "/" + storage.getKey());
            storage = this.baseService.getById(SubStorage.class, nft.getAnimStorageId());
            map.put("animation_url", storage.getIpfshash() + "/" + storage.getKey());
        }
        String website = subSystemService.getKeyValue(Constants.WEBSITE);

        map.put("external_url", website + "/detail/" + nft.getAddress() + ":" + nft.getTokenId());
        if (StringUtils.isEmpty(nft.getProperties())) {
            map.put("attributes", new ArrayList<>());
        } else {
            JSONArray ja = JSONArray.parseArray(nft.getProperties());
            map.put("attributes", ja);
        }
        String metadata = JSON.toJSONString(map);
        InputStream is = new ByteArrayInputStream(metadata.getBytes());
        storage = storageService.store(is, metadata.getBytes().length, "application/json", nft.getName() + ".json");
        nft.setMetadataUrl(storage.getIpfshash());
        nft.setMetadataContent(metadata);
        return nft;
    }

    public Object detail(String token, String tokenId) {
        SubContractNft nft = subContractNftManager.get(token, tokenId);
        if (null == nft) {
            return R.ok();
        }
        NftInfoVo nftInfo = subContractNftManager.getNftInfo(nft);
        return R.ok(nftInfo);
    }

    public IPage<SubContractNft> findAllByAddress(String address, IPage<SubContractNft> pageInfo) {
        QueryWrapper<SubContractNft> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContractNft.ADDRESS, address).eq(BaseEntity.DELETED, false);
        return baseService.findByPage(SubContractNft.class, wrapper, pageInfo);
    }

    public Object owners(String token, String tokenId) {

        SubContractNft nft = subContractNftManager.get(token, tokenId);
        if (null == nft) {
            return R.ok(new ArrayList<>());
        }

        NftInfoVo nftInfoVo = subContractNftManager.getNftInfo(nft);
        return R.ok(nftInfoVo.getItems());
    }

    public Object bids(String token, String tokenId) {
        List<Integer> typeList = new ArrayList<>();
        typeList.add(CommonStatus.BID.getType());
        typeList.add(CommonStatus.EDIT_BID.getType());
        typeList.add(CommonStatus.CANCEL_BID.getType());
        typeList.add(CommonStatus.ACCEPT_BID.getType());

        QueryWrapper<SubOrderLog> logWrapper = new QueryWrapper<>();
        logWrapper.eq(SubOrderLog.TOKEN, token)
                .eq(SubOrderLog.TOKEN_ID, tokenId)
                .in(SubOrderLog.TYPE, typeList);
        logWrapper.orderByDesc(BaseEntity.CREATE_TIME);
        List<SubOrderLog> logList = this.baseService.findByCondition(SubOrderLog.class, logWrapper);
        if (null == logList || logList.isEmpty()) {
            return R.ok(new ArrayList<>());
        }
        List<String> fromList = logList.stream().map(SubOrderLog::getFrom).collect(Collectors.toList());
        List<String> toList = logList.stream().map(SubOrderLog::getTo).collect(Collectors.toList());
        fromList = fromList.stream().filter(vo->!StringUtils.isEmpty(vo)).collect(Collectors.toList());
        toList = toList.stream().filter(vo->!StringUtils.isEmpty(vo)).collect(Collectors.toList());
        fromList.addAll(toList);
        fromList = ListUtils.unrepeated(fromList);      // 去重

        List<String> saltList = new ArrayList<>();
        SubOrder order = null;
        for(SubOrderLog orderLog: logList){
            order = JSON.parseObject(orderLog.getContent(), SubOrder.class);
            saltList.add(order.getSalt());
        }
        saltList = ListUtils.unrepeated(saltList);
        List<SubOrder> orderList = subOrderManager.allbymulti(saltList);

        Map<Long, Map<String, Object>> orderMap = new HashMap<>();
        for(SubOrder order1: orderList){
            orderMap.put(order1.getId(), JSON.parseObject(JSON.toJSONString(order1)));
        }

        List<SubUser> userList = subUserService.listByMulti(fromList);
        Map<String, SubUser> userMap = userList.stream().collect(Collectors.toMap(SubUser::getAddress, Function.identity()));
        List<OrderLogVo> orderLogVoList = logList.stream().map(
                vo -> new OrderLogVo(vo, orderMap.get(vo.getOrderId()), userMap.get(vo.getFrom()), userMap.get(vo.getTo()))
        ).collect(Collectors.toList());
        return R.ok(orderLogVoList);
    }


    public List<SubOrderVo> activebids(List<NftParamVO> paramVoList){
        return subOrderManager.activebids(paramVoList);
    }


    public List<SubOrderVo> activesales(List<NftParamVO> paramVoList){
        return subOrderManager.activesales(paramVoList);
    }

    public Object history(String token, String tokenId) {
        QueryWrapper<SubOrderLog> logWrapper = new QueryWrapper<>();
        logWrapper.eq(SubOrderLog.TOKEN, token)
                .eq(SubOrderLog.TOKEN_ID, tokenId);
        logWrapper.orderByDesc(BaseEntity.CREATE_TIME);
        List<SubOrderLog> logList = baseService.findByCondition(SubOrderLog.class, logWrapper);
        if (null == logList || logList.isEmpty()) {
            return R.ok(new ArrayList<>());
        }
        List<String> fromList = logList.stream().map(SubOrderLog::getFrom).collect(Collectors.toList());
        List<String> toList = logList.stream().map(SubOrderLog::getTo).collect(Collectors.toList());
        fromList = fromList.stream().filter(vo->!StringUtils.isEmpty(vo)).collect(Collectors.toList());
        toList = toList.stream().filter(vo->!StringUtils.isEmpty(vo)).collect(Collectors.toList());
        fromList.addAll(toList);
        fromList = ListUtils.unrepeated(fromList);
        List<SubUser> userList = subUserService.listByMulti(fromList);
        Map<String, SubUser> userMap = userList.stream().collect(Collectors.toMap(SubUser::getAddress, Function.identity()));

        List<String> saltList = new ArrayList<>();
        SubOrder order = null;
        for(SubOrderLog orderLog: logList){
            if(orderLog.getOrderId().equals(0)){
                continue;
            }
            order = JSON.parseObject(orderLog.getContent(), SubOrder.class);
            saltList.add(order.getSalt());
        }
        saltList = ListUtils.unrepeated(saltList);
        List<SubOrder> orderList = subOrderManager.allbymulti(saltList);
        Map<Long, Map<String, Object>> orderMap = new HashMap<>();
        for(SubOrder order1: orderList){
            orderMap.put(order1.getId(), JSON.parseObject(JSON.toJSONString(order1)));
        }

        List<OrderLogVo> orderLogVoList = new ArrayList<>();
        Map<String, Object> content = null;
        for(SubOrderLog orderLog: logList){
            if(orderLog.getOrderId().equals(0)){
                content = JSON.parseObject(orderLog.getContent());
            }else{
                content = orderMap.get(orderLog.getOrderId());
            }
            orderLogVoList.add(
                    new OrderLogVo(
                            orderLog,
                            content,
                            userMap.get(orderLog.getFrom()),
                            userMap.get(orderLog.getTo())
                    )
            );
        }

        return R.ok(orderLogVoList);
    }

    public Map<String, String> getMedia(String[] info) {
        List<NftParamVO> paramVOList = new ArrayList<>();
        String[] tempList = null;
        for(int i = 0; i < info.length; i++){
            tempList = info[i].split(":");
            paramVOList.add(new NftParamVO(tempList[0],tempList[1]));
        }
        List<SubContractNft> nftList = subContractNftExtMapper.listByMulti(paramVOList);
        Map<String, String> map = new HashMap<>();
        String key = null;
        String uri = null;
        ERCTokenInfo tokenInfo = null;
        List<SubContractNft> updateNftList = new ArrayList<>();
        for(SubContractNft nft: nftList){
            key = nft.getAddress() + ":" + nft.getTokenId();
            if(!StringUtils.isEmpty(nft.getMetadataContent())){
                map.put(key, nft.getMetadataContent());
                continue;
            }
            if(nft.getGetMetaTimes().compareTo(13) > 0){
                logger.warn("Stop fetching after 13 tries");
                continue;
            }
            if(StringUtils.isEmpty(nft.getMetadataUrl())){
                uri = DappWeb3jUtil.getErc721Uri(nft.getAddress(), nft.getTokenId());
                if(StringUtils.isEmpty(uri)){
                    logger.warn("uri is not exist");
                    continue;
                }
            }
            nft.setMetadataUrl(uri);

            try {
                tokenInfo = DappWeb3jUtil.processUri(nft.getMetadataUrl());
                if (null != tokenInfo) {
                    map.put(key, tokenInfo.getContent());
                    nft.setName(tokenInfo.getName());
                    nft.setDescription(tokenInfo.getDescription());
                    nft.setMetadataContent(tokenInfo.getContent());
                } else {
                    nft.setGetMetaTimes(nft.getGetMetaTimes() + 1);
                }
                updateNftList.add(nft);
            } catch (Exception e) {
                logger.error("Fetch resource failed", e);
            }
        }
        if(!updateNftList.isEmpty()){
            subContractNftManager.updateBatch(updateNftList);
        }
        return map;
    }

    public IPage<NftInfoVo> findListForIndex(
            IPage<SubContractNft> pageInfo,
            HomeIndexParamsVO params
    ) {
        String address = params.getAddress();
        Long cate = params.getCate();
        List<String> contracts = params.getContracts();
        String payToken = params.getPayToken();
        BigDecimal minPrice = params.getMinPrice();
        BigDecimal maxPrice = params.getMaxPrice();
        String search = params.getSearch();
        String sort = params.getSort();
        String order = params.getOrder();
        if (StringUtils.isEmpty(address)) {
            address = null;
        }
        if (null == contracts || contracts.isEmpty()) {
            contracts = null;
        }
        if(StringUtils.isEmpty(payToken)) {
            payToken = null;
        }
        if(StringUtils.isEmpty(search)) {
            search = null;
        }
        IPage<SubContractNft> iPage = null;
        iPage = subContractNftExtMapper.findVerifyNft(pageInfo, address, cate, contracts, payToken, minPrice, maxPrice, search, sort, order, true, 1);
        IPage<NftInfoVo> nftInfoVoIPage = subContractNftManager.getNftInfoList(iPage);
        return nftInfoVoIPage;
    }


    public Object findContractNft(IPage<SubContractNft> pageInfo, String address, Boolean isSell) {
        if (StringUtils.isEmpty(address)) {
            address = null;
        }
        IPage<SubContractNft> data = subContractNftExtMapper.findContractNft(pageInfo, address, isSell);
        IPage<NftInfoVo> nftInfoList = subContractNftManager.getNftInfoList(data);
        return R.ok(nftInfoList);
    }

    public IPage<NftInfoVo> findOnSellListByAddress(String owner, IPage<SubContractNft> pageInfo) {
        IPage<SubContractNft> iPage = subContractNftExtMapper.findOnsaleNft(pageInfo, owner);
        IPage<NftInfoVo> nftInfoVoIPage = subContractNftManager.getNftInfoList(iPage);
        return nftInfoVoIPage;
    }


    public Object findSearch(String name, IPage<SubContractNft> pageInfo) {
        IPage<SubContractNft> data = subContractNftExtMapper.searchNft(pageInfo, name);
        IPage<NftInfoVo> nftInfoList = subContractNftManager.getNftInfoList(data);
        return R.ok(nftInfoList);
    }

    public IPage<NftInfoVo> findListByUserAddress(SearchNftParamDto paramDto, IPage<SubContractNft> pageInfo) {
        IPage<SubContractNft> iPage = subContractNftExtMapper.findCollectionNft(pageInfo, paramDto);
        IPage<NftInfoVo> nftInfoVoIPage = subContractNftManager.getNftInfoList(iPage);
        return nftInfoVoIPage;
    }

    public List<SubContractNft> nftlist(SearchNftParamDto paramDto){
        return subContractNftExtMapper.nftlist(paramDto);
    }

    public Object listByTokenAndTokenId(Set<NftParamVO> params){
        List<SubContractNft> contractNfts = subContractNftExtMapper.listByTokenAndTokenId(params);
        List<NftInfoVo> nftInfoList = subContractNftManager.getNftInfoList(contractNfts);
        return R.ok(nftInfoList);
    }

    public Integer countCreatorNft(String address) {
        return this.subContractNftExtMapper.countCreatorNft(address);
    }

    public IPage<NftInfoVo> findByCreators(String creator, IPage<SubContractNft> pageInfo) {
        IPage<SubContractNft> iPage = subContractNftExtMapper.findCreatorNft(pageInfo, creator);
        IPage<NftInfoVo> nftInfoVoIPage = subContractNftManager.getNftInfoList(iPage);
        return nftInfoVoIPage;
    }

    public IPage<NftInfoVo> findByLike(String userAddress, IPage<SubContractNft> pageInfo) {
        IPage<SubContractNft> iPage = subContractNftExtMapper.findLikeNft(pageInfo, userAddress);
        IPage<NftInfoVo> nftInfoVoIPage = subContractNftManager.getNftInfoList(iPage);
        return nftInfoVoIPage;
    }

    /**
     *
     * @param address
     * @return
     */
    public Integer countContractOnsale(String address) {
        return subContractNftExtMapper.countContractOnsale(address);
    }

    public String flootContractOnSale(String address) {
        return subContractNftExtMapper.floorContractOnsale(address);
    }


    /**
     *
     * @param address
     * @return
     */
    public Long countContractCollections(String address) {
        QueryWrapper<SubContractNft> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContractNft.ADDRESS, address)
                .eq(SubContractNft.IS_SYNC, true)
                .eq(BaseEntity.DELETED, false);
        return baseService.counts(SubContractNft.class, wrapper);
    }

    /**
     * @param info
     * @return
     */
    public Map<String, String> getRoyalties(String[] info) {
        String temp = null;
        Map<String, String> map = new HashMap<>();
        String token = null;
        String tokenId = null;
        QueryWrapper<SubContractNft> nftQWrapper = null;
        UpdateWrapper<SubContractNft> nftUWrapper = null;
        SubContractNft nft = null;
        List<BigInteger> royalties = null;
        Map<String, Boolean> supportMap = new HashMap<>();
        SubContract contract = null;
        for (int i = 0; i < info.length; i++) {
            temp = info[i];
            token = temp.split(":")[0];
            tokenId = temp.split(":")[1];
            nftQWrapper = new QueryWrapper<>();
            nftQWrapper.eq(SubContractNft.ADDRESS, token).eq(SubContractNft.TOKEN_ID, tokenId);
            nft = this.baseService.getByCondition(SubContractNft.class, nftQWrapper);
            if (null == nft) {
                logger.warn("nft doesn't exist");
                continue;
            }
            if (!StringUtils.isEmpty(nft.getRoyalties())) {
                if ("[]".equals(nft.getRoyalties()) || "0".equals(nft.getRoyalties())) {
                    map.put(temp, "");
                } else {
                    map.put(temp, nft.getRoyalties());
                }
                continue;
            }
            if (null == supportMap.get(token)) {
                contract = subContractService.getByAddress(token);
                if (null == contract) {
                    map.put(temp, "");
                    continue;
                }
                if (null == contract.getIsRoyalties()) {
                    contract.setIsRoyalties(DappWeb3jUtil.isSupportRoyalties(token));
                    this.baseService.update(contract);
                }
                supportMap.put(token, contract.getIsRoyalties());
            }
            if (!supportMap.get(token)) {
                map.put(temp, "");
                continue;
            }
            royalties = null;
            try {
                royalties = DappWeb3jUtil.getRoyalties(token, tokenId);
            } catch (Exception e) {
                logger.error("Fetch royalties error", e);
            }
            if (null == royalties) {
                map.put(temp, "");
                continue;
            }
            nftUWrapper = new UpdateWrapper<>();
            nftUWrapper.set(SubContractNft.ROYALTIES, JSON.toJSONString(royalties));
            nftUWrapper.eq(BaseEntity.ID, nft.getId());
            this.baseService.updateByCondition(SubContractNft.class, nftUWrapper);

        }
        return map;
    }
}
