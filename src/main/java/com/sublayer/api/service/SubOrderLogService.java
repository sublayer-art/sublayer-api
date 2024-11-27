package com.sublayer.api.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sublayer.api.constants.CommonStatus;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.dto.ExchangeBuyLog;
import com.sublayer.api.domain.dto.ExchangeCancelLog;
import com.sublayer.api.domain.dto.PrepareOrderInfo;
import com.sublayer.api.domain.dto.TransferLog;
import com.sublayer.api.entity.SubOrder;
import com.sublayer.api.entity.SubOrderLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SubOrderLogService {

    @Autowired
    IBaseService baseService;

    public Integer buy(ExchangeBuyLog log, SubOrder order){
        SubOrderLog orderLog = new SubOrderLog();

        orderLog.setFrom(log.getOwner());
        orderLog.setOrderId(order.getId());
        orderLog.setPreLogId(0L);
        orderLog.setTo(log.getBuyer());
        orderLog.setTxHash(log.getTxHash());
        orderLog.setContent(JSON.toJSONString(order));
        orderLog.setExpired(false);

        Date time = new Date(Long.parseLong(log.getBlockTimestamp().toString()));
        orderLog.setCreateTime(time);
        orderLog.setUpdateTime(time);
        if(order.getOrderType().equals(1)){
            orderLog.setType(CommonStatus.getStatusByName("Buy").getType());
            orderLog.setToken(log.getSellToken());
            orderLog.setTokenId(log.getSellTokenId().toString());
        }else{
            orderLog.setType(CommonStatus.getStatusByName("Accept bid").getType());
            orderLog.setToken(log.getBuyToken());
            orderLog.setTokenId(log.getBuyTokenId().toString());
        }
        orderLog.setPaytokenAddress(order.getPaytokenAddress());
        orderLog.setPaytokenDecimals(order.getPaytokenDecimals());
        orderLog.setPaytokenName(order.getPaytokenName());
        orderLog.setPaytokenSymbol(order.getPaytokenSymbol());

        return baseService.save(orderLog);
    }

    public Integer cancel(ExchangeCancelLog log, SubOrder order){
        SubOrderLog orderLog = new SubOrderLog();
        orderLog.setFrom(log.getOwner());
        orderLog.setOrderId(order.getId());
        orderLog.setPreLogId(0L);
        orderLog.setTo(log.getOwner());
        orderLog.setTxHash(log.getTxHash());
        orderLog.setContent(JSON.toJSONString(order));
        orderLog.setExpired(true);
        Date time = new Date(Long.parseLong(log.getBlockTimestamp().toString()));
        orderLog.setUpdateTime(time);
        orderLog.setCreateTime(time);
        if(order.getOrderType().equals(1)){
            orderLog.setType(CommonStatus.getStatusByName("Cancel sale").getType());
            orderLog.setToken(log.getSellToken());
            orderLog.setTokenId(log.getSellTokenId().toString());
        }else{
            orderLog.setType(CommonStatus.getStatusByName("Cancel bid").getType());
            orderLog.setToken(log.getBuyToken());
            orderLog.setTokenId(log.getBuyTokenId().toString());
        }
        orderLog.setPaytokenAddress(order.getPaytokenAddress());
        orderLog.setPaytokenDecimals(order.getPaytokenDecimals());
        orderLog.setPaytokenName(order.getPaytokenName());
        orderLog.setPaytokenSymbol(order.getPaytokenSymbol());
        return baseService.save(orderLog);
    }

    public Integer sale(PrepareOrderInfo orderInfo, SubOrder order){
        SubOrderLog orderLog = new SubOrderLog();
        orderLog.setFrom(order.getOwner());
        orderLog.setOrderId(order.getId());
        orderLog.setToken(order.getSellToken());
        orderLog.setTokenId(order.getSellTokenId());
        orderLog.setContent(JSON.toJSONString(order));
        orderLog.setType(orderInfo.getType());
        orderLog.setPaytokenAddress(order.getPaytokenAddress());
        orderLog.setPaytokenName(order.getPaytokenName());
        orderLog.setPaytokenSymbol(order.getPaytokenSymbol());
        orderLog.setPaytokenDecimals(order.getPaytokenDecimals());
        return save(orderLog);
    }

    public Integer bid(PrepareOrderInfo orderInfo, SubOrder order){

        SubOrderLog orderLog = new SubOrderLog();
        orderLog.setFrom(orderInfo.getOwner());
        orderLog.setOrderId(order.getId());
        orderLog.setToken(order.getBuyerToken());
        orderLog.setTokenId(order.getBuyerTokenId());
        orderLog.setContent(JSON.toJSONString(order));
        orderLog.setType(orderInfo.getType());
        orderLog.setPaytokenAddress(order.getPaytokenAddress());
        orderLog.setPaytokenName(order.getPaytokenName());
        orderLog.setPaytokenSymbol(order.getPaytokenSymbol());
        orderLog.setPaytokenDecimals(order.getPaytokenDecimals());
        return save(orderLog);
    }

    public Integer transfer(TransferLog log){
        SubOrderLog orderLog = new SubOrderLog();
        orderLog.setFrom(log.getFrom());
        orderLog.setPreLogId(0L);
        orderLog.setTo(log.getTo());
        orderLog.setTxHash(log.getTxHash());
        orderLog.setContent(JSON.toJSONString(log));
        Date time = new Date(Long.parseLong(log.getBlockTimestamp().toString()));
        orderLog.setUpdateTime(time);
        orderLog.setCreateTime(time);
        if(log.getTo().equalsIgnoreCase(Constants.ZERO_ADDRESS)){
            orderLog.setType(CommonStatus.BURN.getType());
        }else if(log.getFrom().equalsIgnoreCase(Constants.ZERO_ADDRESS)){
            orderLog.setType(CommonStatus.MINT.getType());
        }else{
            orderLog.setType(CommonStatus.TRANSFER.getType());
        }
        orderLog.setToken(log.getAddress());
        orderLog.setTokenId(log.getTokenId().toString());
        return this.save(orderLog);
    }

    public Integer expireOrderLog(Long orderId){
        UpdateWrapper<SubOrderLog> wrapper = new UpdateWrapper<>();
        wrapper.eq(SubOrderLog.ORDER_ID, orderId)
                .eq(SubOrderLog.EXPIRED, false);
        wrapper.set(SubOrderLog.EXPIRED, true);
        return baseService.updateByCondition(SubOrderLog.class, wrapper);
    }

    public Integer update(SubOrderLog orderLog){
        return this.baseService.update(orderLog);
    }

    public Integer save(SubOrderLog orderLog){
        return this.baseService.save(orderLog);
    }
}
