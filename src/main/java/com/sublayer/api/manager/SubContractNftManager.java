package com.sublayer.api.manager;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sublayer.api.constants.CommonStatus;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.dto.*;
import com.sublayer.api.domain.vo.ERCTokenInfo;
import com.sublayer.api.domain.vo.NftInfoVo;
import com.sublayer.api.domain.vo.NftItemInfoVo;
import com.sublayer.api.domain.vo.NftParamVO;
import com.sublayer.api.entity.*;
import com.sublayer.api.service.*;
import com.sublayer.api.utils.DappWeb3jUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubContractNftManager {

    private static final Logger logger = LoggerFactory.getLogger(SubContractNftService.class);

    @Autowired
    IBaseService baseService;

    @Autowired
    private SubNftItemsService nftItemsService;

    @Autowired
    private SubUserService subUserService;

    @Autowired
    private SubSystemService subSystemService;

    @Autowired
    private SubNftCategoryService subNftCategoryService;

    @Autowired
    private SubContractService subContractService;

    @Autowired
    private SubOrderManager subOrderManager;

    @Autowired
    private SubOrderLogService subOrderLogService;


    public IPage<SubContractNft> page(IPage<SubContractNft> page, QueryWrapper<SubContractNft> wrapper){
        return this.baseService.findByPage(SubContractNft.class, wrapper, page);
    }

    public List<NftInfoVo> getNftInfoList(List<SubContractNft> contractNftList){
        List<NftParamVO> nftParamVOList = contractNftList.stream().map(vo -> new NftParamVO(vo)).collect(Collectors.toList());
        List<SubNftItems> nftItemsList = nftItemsService.listByMulti(nftParamVOList);

        List<String> addressList = nftItemsList.stream().map(vo ->vo.getItemOwner()).collect(Collectors.toList());
        Set<String> addressSet = new HashSet<>(addressList);
        addressList = new ArrayList<>(addressSet);
        List<SubUser> userList = subUserService.listByMulti(addressList);
        Map<String, SubUser> userMap = userList.stream().collect(Collectors.toMap(SubUser::getAddress, Function.identity()));

        Map<String, List<NftItemInfoVo>> map = new HashMap<>();

        String key = null;
        for(SubNftItems nftItems: nftItemsList){
            key = nftItems.getAddress().toLowerCase() + ":" + nftItems.getTokenId();
            if(!map.containsKey(key)){
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(new NftItemInfoVo(nftItems, userMap.get(nftItems.getItemOwner())));
        }

        return contractNftList.stream().map(
                vo -> new NftInfoVo(
                        vo,
                        map.get(vo.getAddress().toLowerCase() + ":" + vo.getTokenId())
                )
        ).collect(Collectors.toList());
    }

    public IPage<NftInfoVo> getNftInfoList(IPage<SubContractNft> iPage){
        List<SubContractNft> contractNftList = iPage.getRecords();
        List<NftInfoVo> nftInfoVoList = getNftInfoList(contractNftList);
        IPage<NftInfoVo> nftInfoVoIPage = new Page<>();
        nftInfoVoIPage.setPages(iPage.getPages());
        nftInfoVoIPage.setSize(iPage.getSize());
        nftInfoVoIPage.setTotal(iPage.getTotal());
        nftInfoVoIPage.setRecords(nftInfoVoList);
        nftInfoVoIPage.setCurrent(iPage.getCurrent());
        return nftInfoVoIPage;
    }

    public NftInfoVo getNftInfo(SubContractNft contractNft){
        List<SubNftItems> nftItemsList = nftItemsService.getList(contractNft.getAddress(), contractNft.getTokenId());
        SubUser creator = subUserService.get(contractNft.getCreator());
        List<String> addressList = nftItemsList.stream().map(vo -> vo.getItemOwner()).collect(Collectors.toList());
        List<SubUser> userList = subUserService.listByMulti(addressList);

        Map<String, SubUser> map = userList.stream().collect(Collectors.toMap(SubUser::getAddress, Function.identity()));
        List<NftItemInfoVo> itemInfoVoList = nftItemsList.stream().map(vo -> new NftItemInfoVo(vo, map.get(vo.getItemOwner()))).collect(Collectors.toList());

        return new NftInfoVo(contractNft, creator, itemInfoVoList);
    }

    public SubContractNft create(NftInfo nft){
        SubContractNft contractNft = this.get(nft.getAddress(), nft.getTokenId());
        if(null != contractNft){
            return contractNft;
        }

        contractNft = new SubContractNft();
        contractNft.setContractId(nft.getContractId());
        contractNft.setStorageId(nft.getStorageId());
        contractNft.setAddress(nft.getAddress());
        contractNft.setTokenId(nft.getTokenId());
        contractNft.setName(nft.getName());
        contractNft.setDescription(nft.getDescription());
        contractNft.setRoyalties(nft.getRoyalties());
        contractNft.setIsSync(nft.getIsSync());
        contractNft.setCreator(nft.getCreator());
        contractNft.setCategoryId(nft.getCategoryId());
        contractNft.setImgUrl(nft.getImgUrl());
        contractNft.setMetadataUrl(nft.getMetadataUrl());
        contractNft.setMetadataContent(nft.getMetadataContent());

        String value = subSystemService.getKeyValue(Constants.NFT_DEFAULT_VERIFY);
        if(value.equals("true")){
            contractNft.setNftVerify(1);
        }

        save(contractNft);
        nftItemsService.create(nft);
        return contractNft;
    }


    public Integer sale(PrepareOrderInfo orderInfo){
        nftItemsService.sale(orderInfo);
        return subOrderManager.sale(orderInfo);
    }

    public Integer bid(PrepareOrderInfo orderInfo){
        List<SubNftItems> nftItemsList = nftItemsService.getList(orderInfo.getBuyToken(), orderInfo.getBuyTokenId());
        String content = JSON.toJSONString(orderInfo);

        return subOrderManager.bid(orderInfo);
    }

    public void buy(ExchangeBuyLog log){

        SubOrder order = subOrderManager.get(log);
        if(null == order){
            return;
        }
        nftItemsService.buy(log, order);
        subOrderManager.buy(log, order);
    }

    public void cancel(ExchangeCancelLog log){
        SubOrder order = subOrderManager.get(log);
        if(null == order){
            return;
        }
        nftItemsService.cancel(log, order);
        subOrderManager.cancel(log, order);
    }

    public SubContractNft getActive(String address, String tokenId) {
        QueryWrapper<SubContractNft> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContractNft.ADDRESS, address)
                .eq(SubContractNft.TOKEN_ID, tokenId)
                .eq(SubContractNft.IS_SYNC, false)
                .eq(BaseEntity.DELETED, false);
        return this.baseService.getByCondition(SubContractNft.class, wrapper);
    }

    public SubContractNft get(String address, String tokenId) {
        QueryWrapper<SubContractNft> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContractNft.ADDRESS, address)
                .eq(SubContractNft.TOKEN_ID, tokenId)
                .eq(BaseEntity.DELETED, false);
        return this.baseService.getByCondition(SubContractNft.class, wrapper);
    }


    public Integer transfer(TransferLog log){
        if(log.getFrom().equalsIgnoreCase(Constants.ZERO_ADDRESS) &&
                log.getTo().equalsIgnoreCase(Constants.ZERO_ADDRESS)
        ){
            // from and to is zero address
            return 0;
        }

        String tokenId = log.getTokenId().toString();
        SubContract contract = subContractService.get(log.getAddress());
        if(null == contract ){
            subContractService.add(log.getAddress());
        }
        SubContractNft nft = this.get(log.getAddress(), tokenId);
        if(null == nft){
            if(!log.getFrom().equalsIgnoreCase(Constants.ZERO_ADDRESS)){
                return 0;
            }
            // mint transfer
            nft = this.add(log);
        }
        if(!nft.getIsSync()){
            nft.setIsSync(true);
        }

        if(log.getTo().equalsIgnoreCase(Constants.ZERO_ADDRESS)){
            // burn transfer
            nft.setDeleted(true);
        }

        SubNftItems nftItems = null;
        String content = JSON.toJSONString(log);
        Integer transferType = CommonStatus.TRANSFER.getType();
        if(!log.getFrom().equalsIgnoreCase(Constants.ZERO_ADDRESS)){
            logger.info("address: "+log.getAddress());
            logger.info("tokenId: "+tokenId);
            logger.info("from: "+log.getFrom());
            nftItems = nftItemsService.get(log.getAddress(), tokenId, log.getFrom());
            nftItems.setDeleted(true);
            nftItemsService.update(nftItems);
        }

        if(!log.getTo().equalsIgnoreCase(Constants.ZERO_ADDRESS)){
            nftItems = this.nftItemsService.get(log.getAddress(), tokenId, log.getTo());
            if(null == nftItems){
                nftItems = new SubNftItems();
                nftItems.setAddress(log.getAddress());
                nftItems.setTokenId(tokenId);
                nftItems.setItemOwner(log.getTo());
            }
            nftItems.setIsSync(true);
            nftItemsService.save(nftItems);
            Integer receiveType = CommonStatus.RECEIVE.getType();
        }

        subOrderLogService.transfer(log);
        nft = this.getMedia(nft);
        return this.update(nft);
    }

    private SubContractNft getMedia(SubContractNft nft) {
        String uri = null;
        ERCTokenInfo tokenInfo = null;
        if(!StringUtils.isEmpty(nft.getMetadataContent())){
            return nft;
        }
        if(null != nft.getGetMetaTimes()){
            if (nft.getGetMetaTimes().compareTo(13) > 0){
                logger.warn("Stop fetching after 13 tries");
                return nft;
            }
        }
        if(StringUtils.isEmpty(nft.getMetadataUrl())){
            uri = DappWeb3jUtil.getErc721Uri(nft.getAddress(), nft.getTokenId());
            if(StringUtils.isEmpty(uri)){
                logger.warn("uri is not exist");
                return nft;
            }
        }
        nft.setMetadataUrl(uri);

        try {
            tokenInfo = DappWeb3jUtil.processNftUri(nft.getMetadataUrl());
            if (null != tokenInfo) {
                nft.setName(tokenInfo.getName());
                nft.setDescription(tokenInfo.getDescription());
                nft.setMetadataContent(tokenInfo.getContent());
            } else {
                nft.setGetMetaTimes(nft.getGetMetaTimes() + 1);
            }
        } catch (Exception e) {
            logger.error("Fetch resource error", e);
        }
        return nft;
    }


    public SubContractNft add(TransferLog log){
        SubContractNft nft = new SubContractNft();
        nft.setAddress(log.getAddress());
        nft.setTokenId(log.getTokenId().toString());
        nft.setCreator(log.getTo());
        nft.setIsSync(true);
        nft.setTxHash(log.getTxHash());

        String value = subSystemService.getKeyValue(Constants.NFT_DEFAULT_VERIFY);
        if(value.equals("true")){
            nft.setNftVerify(1);
        }

        SubNftCategory category = subNftCategoryService.getDefault();
        if(null != category){
            nft.setCategoryId(category.getId());
        }
        this.save(nft);

        return nft;
    }

    public Integer updateBatch(List<SubContractNft> nftList){
        return baseService.updateBatch(nftList);
    }

    public Integer update(SubContractNft nft){
        return baseService.update(nft);
    }

    public Integer save(SubContractNft nft){
        return baseService.save(nft);
    }
}
