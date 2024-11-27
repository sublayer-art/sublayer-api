package com.sublayer.api.domain.vo;

import com.sublayer.api.entity.SubOrder;
import com.sublayer.api.entity.SubUser;

import java.util.Date;

public class SubOrderVo {
    private String owner;
    private String sellToken;
    private String sellTokenId;
    private Integer sellType;
    private String sellValue;
    private String buyerToken;
    private String buyerTokenId;
    private Integer buyerType;
    private String buyerValue;
    private Integer orderType;
    private String paytokenAddress;
    private String paytokenName;
    private Integer paytokenDecimals;
    private String paytokenSymbol;

    private Date updateTime;
    private Date createTime;

    UserBaseInfoVo user;

    public SubOrderVo(SubOrder order, SubUser user){
        this.owner = order.getOwner();
        this.sellToken = order.getSellToken();
        this.sellTokenId = order.getSellTokenId();
        this.sellType = order.getSellType();
        this.sellValue = order.getSellValue();
        this.buyerToken = order.getBuyerToken();
        this.buyerTokenId = order.getBuyerTokenId();
        this.buyerType = order.getBuyerType();
        this.buyerValue = order.getBuyerValue();
        this.orderType = order.getOrderType();
        this.paytokenAddress = order.getPaytokenAddress();
        this.paytokenName = order.getPaytokenName();
        this.paytokenDecimals = order.getPaytokenDecimals();
        this.paytokenSymbol = order.getPaytokenSymbol();
        this.updateTime = order.getUpdateTime();
        this.createTime = order.getCreateTime();

        this.user = new UserBaseInfoVo(user);
    }
}
