package com.sublayer.api.domain.vo;

import com.sublayer.api.domain.dto.TransferLog;
import com.sublayer.api.entity.SubOrder;
import com.sublayer.api.entity.SubOrderLog;
import com.sublayer.api.entity.SubUser;

import java.util.Date;
import java.util.Map;

public class OrderLogVo {
    private String from;
    private String to;
    private UserBaseInfoVo fromUser;
    private UserBaseInfoVo toUser;
    private Integer type;
    private String txHash;
    private String token;
    private String tokenId;
    private Boolean expired;

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    private Map<String, Object> content;

    private String paytokenAddress;
    private String paytokenName;
    private String paytokenSymbol;
    private Integer paytokenDecimals;

    private Date createTime;
    private Date updateTime;

    private SubOrder order;
    private TransferLog transferLog;


    public OrderLogVo(SubOrderLog orderLog, Map<String, Object> content, SubUser fromUser, SubUser toUser){
        this.from = orderLog.getFrom();
        this.to = orderLog.getTo();
        if(null != fromUser) {
            this.fromUser = new UserBaseInfoVo(fromUser);
        }
        if(null != toUser) {
            this.toUser = new UserBaseInfoVo(toUser);
        }
        this.type = orderLog.getType();
        this.txHash = orderLog.getTxHash();
        this.token = orderLog.getToken();
        this.tokenId = orderLog.getTokenId();
        this.expired = orderLog.getExpired();
        // this.content = JSON.parseObject(orderLog.getContent());
        this.content = content;
        this.updateTime = orderLog.getUpdateTime();
        this.createTime = orderLog.getCreateTime();
        this.paytokenAddress = orderLog.getPaytokenAddress();
        this.paytokenName = orderLog.getPaytokenName();
        this.paytokenSymbol = orderLog.getPaytokenSymbol();
        this.paytokenDecimals = orderLog.getPaytokenDecimals();
    }
}
