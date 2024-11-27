package com.sublayer.api.domain.vo;

import com.sublayer.api.entity.SubContractNft;
import com.sublayer.api.entity.SubUser;

public class NftParamVO {
    private String address;

    private String tokenId;

    public NftParamVO(SubContractNft contractNft){
        this.address = contractNft.getAddress();
        this.tokenId = contractNft.getTokenId();
    }

    public NftParamVO(String address ,  String tokenId) {
        this.address = address;
        this.tokenId = tokenId;
    }
}
