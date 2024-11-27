package com.sublayer.api.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.constants.CommonStatus;
import com.sublayer.api.domain.dto.ExchangeBuyLog;
import com.sublayer.api.domain.dto.ExchangeCancelLog;
import com.sublayer.api.domain.dto.NftInfo;
import com.sublayer.api.domain.dto.PrepareOrderInfo;
import com.sublayer.api.domain.vo.NftParamVO;
import com.sublayer.api.entity.*;
import com.sublayer.api.mapper.SubNftItemsExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SubNftItemsService {

    private static final Logger logger = LoggerFactory.getLogger(SubNftItemsService.class);
    @Autowired
    IBaseService baseService;

    @Autowired
    private SubPayTokenService subPayTokenService;

    @Autowired
    private SubNftItemsExtMapper subNftItemsExtMapper;

    public Long countOnsale(String address) {
        QueryWrapper<SubNftItems> wrapper = new QueryWrapper<>();
        wrapper.eq(SubNftItems.ITEM_OWNER, address)
                .eq(SubNftItems.IS_SYNC, true)
                .eq(BaseEntity.DELETED, false)
                .eq(SubNftItems.ONSELL, true);
        return baseService.counts(SubNftItems.class, wrapper);
    }

    public Long countCollections(String address) {
        QueryWrapper<SubNftItems> wrapper = new QueryWrapper<>();
        wrapper.eq(SubNftItems.ITEM_OWNER, address)
                .eq(SubNftItems.IS_SYNC, true)
                .eq(BaseEntity.DELETED, false);
        return baseService.counts(SubNftItems.class, wrapper);
    }

    public List<SubNftItems> listByMulti(List<NftParamVO> nftParamVOList){
        if (null == nftParamVOList){
            return new ArrayList<>();
        }
        if (nftParamVOList.size()==0){
            return new ArrayList<>();
        }
        return subNftItemsExtMapper.listByMulti(nftParamVOList);
    }

    public Integer create(NftInfo nft){
        SubNftItems item = new SubNftItems();
        item.setItemOwner(nft.getCreator());
        item.setAddress(nft.getAddress());
        item.setIsSync(nft.getIsSync());
        item.setCategoryId(nft.getCategoryId());
        item.setOnsell(false);
        item.setTokenId(nft.getTokenId());
        return this.baseService.save(item);
    }

    public Integer add(SubContractNft nft, String quanlity, String owner, Date time){
        SubNftItems nftItems = new SubNftItems();
        nftItems.setAddress(nft.getAddress());
        nftItems.setTokenId(nft.getTokenId());
        nftItems.setCategoryId(nft.getCategoryId());
        nftItems.setItemOwner(owner);
        nftItems.setIsSync(true);
        nftItems.setUpdateTime(time);
        nftItems.setCreateTime(time);
        return this.save(nftItems);
    }


    public List<SubNftItems> getList(String address, String tokenId){
        QueryWrapper<SubNftItems> wrapper = new QueryWrapper<>();
        wrapper.eq(SubNftItems.ADDRESS, address)
                .eq(SubNftItems.TOKEN_ID,tokenId)
                .eq(SubNftItems.DELETED, false);
        return this.baseService.findByCondition(SubNftItems.class, wrapper);
    }


    public SubNftItems get(String address, String tokenId, String owner){
        QueryWrapper<SubNftItems> wrapper = new QueryWrapper<>();
        wrapper.eq(SubNftItems.ADDRESS, address)
                .eq(SubNftItems.TOKEN_ID,tokenId)
                .eq(SubNftItems.ITEM_OWNER, owner)
                .eq(SubNftItems.DELETED, false);
        return this.baseService.getByCondition(SubNftItems.class, wrapper);
    }

    public Integer sale(PrepareOrderInfo orderInfo){
        SubNftItems nftItems = get(orderInfo.getSellToken(), orderInfo.getSellTokenId(), orderInfo.getOwner());
        SubPayToken payToken = subPayTokenService.get(orderInfo.getBuyToken());
        nftItems.setOnsell(true);
        nftItems.setPaytokenAddress(payToken.getAddress());
        nftItems.setOnsellTime(new Date());
        nftItems.setPaytokenName(payToken.getName());
        nftItems.setPaytokenDecimals(payToken.getDecimals());
        nftItems.setPaytokenSymbol(payToken.getSymbol());
        nftItems.setPrice(orderInfo.getBuyValue());
        return this.update(nftItems);
    }

    public void buy(ExchangeBuyLog log, SubOrder order){
        String address = null;
        String tokenId = null;
        String owner = null;
        Integer type = null;

        SubNftItems nftItems = null;
        if(order.getOrderType().equals(CommonStatus.SALE.getType())){
            address = log.getSellToken();
            tokenId = log.getSellTokenId().toString();
            owner = log.getOwner();
            type = CommonStatus.BUY.getType();
            nftItems = get(address, tokenId, owner);
            if(null == nftItems){
                logger.warn("buy nft-item未找到:address=>" + address + "; tokenId=>" + tokenId.toString() + "; owner=>" + owner);
                return;
            }
            nftItems.setOnsell(false);
            this.update(nftItems);
        }else{
            type = CommonStatus.ACCEPT_BID.getType();
        }

    }

    public void cancel(ExchangeCancelLog log, SubOrder order){
        String content = JSON.toJSONString(log);
        if(order.getOrderType().equals(CommonStatus.SALE.getType())) {
            // cancel sale
            Integer type = CommonStatus.getStatusByName("Cancel sale").getType();
            SubNftItems nftItems = this.get(log.getSellToken(),log.getSellTokenId().toString(), log.getOwner() );
            if(null == nftItems){
                logger.warn("cancel nft-item not found:" + JSON.toJSONString(log));
                return;
            }
            nftItems.setOnsell(false);
            this.update(nftItems);
        }else{
            // cancel bid
            Integer type = CommonStatus.getStatusByName("Cancel bid").getType();
            List<SubNftItems> nftItemsList = this.getList(log.getBuyToken(), log.getBuyTokenId().toString());
        }
    }


    public Integer update(SubNftItems nftItems){
        return baseService.update(nftItems);
    }

    public Integer save(SubNftItems nftItems){
        return baseService.saveOrUpdate(nftItems);
    }


    public SubNftItems getByAddressAndTokenId(String address, String tokenId) {
        QueryWrapper<SubNftItems> wrapper = new QueryWrapper<>();
        wrapper.eq(SubNftItems.DELETED, false)
                .eq(SubNftItems.ADDRESS, address)
                .eq(SubNftItems.TOKEN_ID, tokenId);
        return baseService.getByCondition(SubNftItems.class, wrapper);
    }

}
