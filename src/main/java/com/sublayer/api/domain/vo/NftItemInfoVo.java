package com.sublayer.api.domain.vo;

import com.sublayer.api.entity.SubNftItems;
import com.sublayer.api.entity.SubUser;
import lombok.Data;

import java.util.Date;

@Data
public class NftItemInfoVo {
    private String address;

    private String tokenId;

    private String price;

    private String paytokenAddress;

    private String paytokenName;

    private Integer paytokenDecimals;

    private String paytokenSymbol;

    private String itemOwner;

    private Long categoryId;

    private Boolean onsell;

    private Date onsellTime;

    private UserBaseInfoVo user;

    public UserBaseInfoVo getUser() {
        return user;
    }

    public void setUser(UserBaseInfoVo user) {
        this.user = user;
    }

    public NftItemInfoVo(SubNftItems nftItems, SubUser user){
        this.address = nftItems.getAddress();
        this.tokenId = nftItems.getTokenId();
        this.price = nftItems.getPrice();
        this.paytokenAddress = nftItems.getPaytokenAddress();
        this.paytokenName = nftItems.getPaytokenName();
        this.paytokenDecimals = nftItems.getPaytokenDecimals();
        this.paytokenSymbol = nftItems.getPaytokenSymbol();
        this.itemOwner = nftItems.getItemOwner();
        this.onsell = nftItems.getOnsell();
        this.categoryId = nftItems.getCategoryId();
        this.onsellTime = nftItems.getOnsellTime();
        this.user = new UserBaseInfoVo(user);
    }
}
