package com.sublayer.api.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.dto.ExchangeBuyLog;
import com.sublayer.api.domain.dto.ExchangeCancelLog;
import com.sublayer.api.domain.dto.PrepareOrderInfo;
import com.sublayer.api.domain.vo.NftParamVO;
import com.sublayer.api.domain.vo.SubOrderVo;
import com.sublayer.api.entity.*;
import com.sublayer.api.mapper.SubNftItemsExtMapper;
import com.sublayer.api.mapper.SubOrderExtMapper;
import com.sublayer.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubOrderManager {

    @Autowired
    IBaseService baseService;

    @Autowired
    private SubOrderExtMapper subOrderExtMapper;

    @Autowired
    private SubNftItemsExtMapper subNftItemsExtMapper;

    @Autowired
    private SubUserService subUserService;

    @Autowired
    private SubPayTokenService subPayTokenService;

    @Autowired
    private SubSystemService subSystemService;

    @Autowired
    private SubOrderLogService subOrderLogService;

    public List<SubOrder> allbymulti(List<String> saltList){
        if(saltList.isEmpty()){
            return new ArrayList<>();
        }
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.in(SubOrder.SALT, saltList);
        return baseService.findByCondition(SubOrder.class, wrapper);
    }

    public List<SubOrderVo> activesales(List<NftParamVO> params){
        List<SubOrder> orderList = subOrderExtMapper.salelistbymulti(params);

        List<SubNftItems> itemsList = subNftItemsExtMapper.listByMulti(params);
        Set<String> ownerList = itemsList.stream().map(vo -> vo.getItemOwner()+":"+vo.getAddress()+":"+vo.getTokenId()).collect(Collectors.toSet());

        orderList = orderList.stream().filter(
                vo -> ownerList.contains(vo.getOwner() + ":" + vo.getSellToken() + ":" + vo.getSellTokenId())
        ).collect(Collectors.toList());
        if(orderList.isEmpty()){
            return new ArrayList<>();
        }

        List<String> owners = orderList.stream().map(SubOrder::getOwner).collect(Collectors.toList());
        List<SubUser> userList = subUserService.listByMulti(owners);
        Map<String, SubUser> userMap = userList.stream().collect(Collectors.toMap(SubUser::getAddress, Function.identity()));
        List<SubOrderVo> orderVoList = orderList.stream().map(vo->new SubOrderVo(vo, userMap.get(vo.getOwner()))).collect(Collectors.toList());
        return orderVoList;
    }

    public List<SubOrderVo> activebids(List<NftParamVO> params){
        List<SubOrder> orderList = subOrderExtMapper.bidlistbymulti(params);
        List<String> owners = orderList.stream().map(SubOrder::getOwner).collect(Collectors.toList());
        List<SubUser> userList = subUserService.listByMulti(owners);
        Map<String, SubUser> userMap = userList.stream().collect(Collectors.toMap(SubUser::getAddress, Function.identity()));
        List<SubOrderVo> orderVoList = orderList.stream().map(vo->new SubOrderVo(vo, userMap.get(vo.getOwner()))).collect(Collectors.toList());
        return orderVoList;
    }

    public SubOrder get(ExchangeBuyLog log) {
        return get(
                log.getSellToken(),
                log.getSellTokenId().toString(),
                log.getBuyToken(),
                log.getBuyTokenId().toString(),
                log.getOwner(),
                log.getSalt().toString());
    }

    public SubOrder get(ExchangeCancelLog log) {
        return get(
                log.getSellToken(),
                log.getSellTokenId().toString(),
                log.getBuyToken(),
                log.getBuyTokenId().toString(),
                log.getOwner(),
                log.getSalt().toString()
        );
    }

    private SubOrder get(
            String sellToken,
            String sellTokenId,
            String buyerToken,
            String buyerTokenId,
            String owner,
            String salt) {
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(SubOrder.SELL_TOKEN, sellToken)
                .eq(SubOrder.SELL_TOKEN_ID, sellTokenId)
                .eq(SubOrder.BUYER_TOKEN, buyerToken)
                .eq(SubOrder.BUYER_TOKEN_ID, buyerTokenId)
                .eq(SubOrder.OWNER, owner)
                .eq(SubOrder.SALT, salt)
                .eq(BaseEntity.DELETED, false);
        return baseService.getByCondition(SubOrder.class, wrapper);
    }

    public SubOrder getActiveSellOrder(String sellToken, String sellTokenId, String owner, Integer orderType) {
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(SubOrder.SELL_TOKEN, sellToken)
                .eq(SubOrder.SELL_TOKEN_ID, sellTokenId)
                .eq(SubOrder.OWNER, owner)
                .eq(SubOrder.ORDER_TYPE, orderType)
                .eq(SubOrder.EXPIRED, false)
                .eq(SubOrder.STATUS, 0)
                .eq(BaseEntity.DELETED, false);
        return baseService.getByCondition(SubOrder.class, wrapper);
    }

    public SubOrder getActiveBuyerOrder(String buyerToken, String buyerTokenId, String owner, Integer orderType) {
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(SubOrder.BUYER_TOKEN, buyerToken)
                .eq(SubOrder.BUYER_TOKEN_ID, buyerTokenId)
                .eq(SubOrder.OWNER, owner)
                .eq(SubOrder.ORDER_TYPE, orderType)
                .eq(SubOrder.EXPIRED, false)
                .eq(SubOrder.STATUS, 0)
                .eq(SubOrder.DELETED, false);
        return baseService.getByCondition(SubOrder.class, wrapper);
    }

    public SubOrder getSellOrder(String sellToken, String sellTokenId, String owner) {
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(SubOrder.SELL_TOKEN, sellToken)
                .eq(SubOrder.SELL_TOKEN_ID, sellTokenId)
                .eq(SubOrder.OWNER, owner)
                .eq(BaseEntity.DELETED, false);
        return baseService.getByCondition(SubOrder.class, wrapper);
    }

    public SubOrder getBuyerOrder(String buyerToken, String buyerTokenId, String owner) {
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(SubOrder.BUYER_TOKEN, buyerToken)
                .eq(SubOrder.BUYER_TOKEN_ID, buyerTokenId)
                .eq(SubOrder.OWNER, owner)
                .eq(BaseEntity.DELETED, false);
        return baseService.getByCondition(SubOrder.class, wrapper);
    }

    public SubOrder getSellOrder(String sellToken, String sellTokenId, String owner, String salt) {
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(SubOrder.SELL_TOKEN, sellToken)
                .eq(SubOrder.SELL_TOKEN_ID, sellTokenId)
                .eq(SubOrder.OWNER, owner)
                .eq(SubOrder.SALT, salt)
                .eq(BaseEntity.DELETED, false);
        return baseService.getByCondition(SubOrder.class, wrapper);
    }

    public SubOrder getBuyerOrder(String buyerToken, String buyerTokenId, String owner, String salt) {
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(SubOrder.BUYER_TOKEN, buyerToken)
                .eq(SubOrder.BUYER_TOKEN_ID, buyerTokenId)
                .eq(SubOrder.OWNER, owner)
                .eq(SubOrder.SALT, salt)
                .eq(BaseEntity.DELETED, false);
        return baseService.getByCondition(SubOrder.class, wrapper);
    }

    public Integer sale(PrepareOrderInfo orderInfo) {
        SubOrder order = getSellOrder(orderInfo.getSellToken(), orderInfo.getSellTokenId(), orderInfo.getOwner());
        SubPayToken payToken = subPayTokenService.get(orderInfo.getBuyToken());
        if (null == order) {
            order = orderInfo.toOrder();
        } else {
            order.setBuyerToken(orderInfo.getBuyToken());
            order.setBuyerTokenId(orderInfo.getBuyTokenId());
            order.setBuyerValue(orderInfo.getBuyValue());
            order.setSignature(orderInfo.getSignature());
            order.setSellValue(orderInfo.getSellValue());
            order.setSells("0");
            subOrderLogService.expireOrderLog(order.getId());
        }
        order.setPaytokenAddress(payToken.getAddress());
        order.setPaytokenName(payToken.getName());
        order.setPaytokenSymbol(payToken.getSymbol());
        order.setPaytokenDecimals(payToken.getDecimals());

        order.setStatus(0);
        order.setOrderType(1);

        order.setSellFee(Integer.parseInt(subSystemService.getKeyValue(Constants.SELLER_FEE)));
        order.setBuyFee(Integer.parseInt(subSystemService.getKeyValue(Constants.BUYER_FEE)));

        if (null == order.getId()) {
            this.save(order);
        } else {
            this.update(order);
        }
        return subOrderLogService.sale(orderInfo, order);
    }

    public Integer bid(PrepareOrderInfo orderInfo) {
        SubOrder order = getBuyerOrder(orderInfo.getBuyToken(), orderInfo.getBuyTokenId(), orderInfo.getOwner());
        SubPayToken payToken = subPayTokenService.get(orderInfo.getSellToken());
        if (null == order) {
            order = orderInfo.toOrder();
        } else {
            order.setSellToken(orderInfo.getSellToken());
            order.setSellValue(orderInfo.getSellValue());
            order.setSellTokenId(orderInfo.getSellTokenId());
            order.setSignature(orderInfo.getSignature());
            order.setBuyerValue(orderInfo.getBuyValue());
            // expire order log
            subOrderLogService.expireOrderLog(order.getId());
        }

        order.setPaytokenName(payToken.getName());
        order.setPaytokenSymbol(payToken.getSymbol());
        order.setPaytokenDecimals(payToken.getDecimals());
        order.setPaytokenAddress(payToken.getAddress());
        order.setOrderType(2);
        order.setSells("0");
        order.setSellFee(Integer.parseInt(subSystemService.getKeyValue(Constants.SELLER_FEE)));
        order.setBuyFee(Integer.parseInt(subSystemService.getKeyValue(Constants.BUYER_FEE)));

        if (null == order.getId()) {
            this.save(order);
        } else {
            this.update(order);
        }
        return subOrderLogService.bid(orderInfo, order);
    }


    public Integer buy(ExchangeBuyLog log, SubOrder order) {
        BigInteger amount = log.getAmount();

        BigInteger sells = new BigInteger(order.getSells()).add(amount);
        order.setSells(sells.toString());

        BigInteger value = new BigInteger(order.getSellValue());

        if (value.compareTo(sells) <= 0) {
            order.setStatus(1);
            order.setExpired(true);
            order.setDeleted(true);
            subOrderLogService.expireOrderLog(order.getId());
        }

        subOrderLogService.buy(log, order);
        return this.update(order);
    }


    public Integer cancel(ExchangeCancelLog log, SubOrder order) {
        subOrderLogService.expireOrderLog(order.getId());
        subOrderLogService.cancel(log, order);

        order.setExpired(true);
        order.setStatus(2);
        order.setDeleted(true);
        return this.update(order);
    }

    public Integer update(SubOrder order) {
        return baseService.update(order);
    }

    public Integer save(SubOrder order) {
        return baseService.save(order);
    }

    /**
     * @param time
     * @return
     */
    public Long bidCountByCondition(Long time) {
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(SubOrder.STATUS, 0)
                .eq(SubOrder.ORDER_TYPE, 2)
                .eq(SubOrder.DELETED, false);
        if (null != time) {
            wrapper.gt(SubOrder.CREATE_TIME, time);
        }
        return baseService.counts(SubOrder.class, wrapper);
    }
}
