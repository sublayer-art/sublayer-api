package com.sublayer.api.domain.dto;

import lombok.Data;

@Data
public class SignOrderInfo {
    private String owner;

    private String salt;

    private String sellerToken;

    private String sellerTokenId;

    private String sellerAssetType;

    private String buyerToken;

    private String buyerTokenId;

    private String buyerAssetType;

    private String selling;

    private String buying;

    private String sellerFee;

    private String signature;

    private String buyFee;

    private String r;

    private String s;

    private String v;

    public SignOrderInfo(PrepareOrderInfo info) {
        this.owner = info.getOwner();
        this.salt = info.getSalt();
        this.sellerToken = info.getSellToken();
        this.sellerTokenId = info.getSellTokenId();
        this.sellerAssetType = info.getSellType();
        this.buyerToken = info.getBuyToken();
        this.buyerTokenId = info.getBuyTokenId();
        this.buyerAssetType = info.getBuyType();
        this.selling = info.getSellValue();
        this.buying = info.getBuyValue();
        this.sellerFee = info.getSellFee();
    }
}
