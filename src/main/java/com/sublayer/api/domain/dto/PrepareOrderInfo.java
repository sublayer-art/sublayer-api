package com.sublayer.api.domain.dto;

import com.sublayer.api.entity.SubOrder;
import lombok.Data;

@Data
public class PrepareOrderInfo {
    private Integer type;

    private String owner;

    private String sellToken;

    private String sellTokenId;

    private String sellValue;

    private String sellType;

    private String buyToken;

    private String buyTokenId;

    private String buyValue;

    private String buyType;

    private String sellFee;

    private String salt;

    private String signature;

    private Long nftItemsId;

    private Long orderId;

    private String message;

    private Integer quantity;

    private String buyFee;

    private String r;

    private String v;

    private String s;

    public PrepareOrderInfo() {
    }

    public PrepareOrderInfo(SubOrder order) {
        this.buyToken = order.getBuyerToken();
        this.buyTokenId = order.getBuyerTokenId().toString();
        this.buyType = order.getBuyerType().toString();
        this.buyValue = order.getBuyerValue();
        this.owner = order.getOwner();
        this.sellToken = order.getSellToken();
        this.sellTokenId = order.getSellTokenId().toString();
        this.sellType = order.getSellType().toString();
        this.sellValue = order.getSellValue();
        this.salt = order.getSalt();
    }

    public SubOrder toOrder() {
        SubOrder order = new SubOrder();
        order.setBuyerToken(this.buyToken);
        order.setBuyerTokenId(this.buyTokenId);
        order.setBuyerType(Integer.parseInt(this.buyType));
        order.setBuyerValue(this.buyValue);
        order.setOwner(this.owner);
        order.setSalt(this.salt);
        order.setSellToken(this.sellToken);
        order.setSellTokenId(this.sellTokenId);
        order.setSellType(Integer.parseInt(this.sellType));
        order.setSellValue(this.sellValue);
        order.setSignature(this.signature);
        return order;
    }
}
