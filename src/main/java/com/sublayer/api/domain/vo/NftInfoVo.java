package com.sublayer.api.domain.vo;

import com.sublayer.api.entity.SubContractNft;
import com.sublayer.api.entity.SubUser;
import lombok.Data;

import java.util.List;

@Data
public class NftInfoVo {
    private String address;

    private String tokenId;

    private Long categoryId;

    private String royalties;

    private Integer nftVerify;

    private String creator;

    private UserBaseInfoVo user;

    public UserBaseInfoVo getUser() {
        return user;
    }

    public void setUser(UserBaseInfoVo user) {
        this.user = user;
    }

    private String txHash;

    private String imgUrl;

    private String metadataUrl;

    private String metadataContent;

    private List<NftItemInfoVo> items;


    public NftInfoVo(SubContractNft contractNft, List<NftItemInfoVo> items) {
        this.address = contractNft.getAddress();
        this.categoryId = contractNft.getCategoryId();
        this.tokenId = contractNft.getTokenId();
        this.royalties = contractNft.getRoyalties();
        this.nftVerify = contractNft.getNftVerify();
        this.creator = contractNft.getCreator();
        this.txHash = contractNft.getTxHash();
        this.imgUrl = contractNft.getImgUrl();
        this.metadataContent = contractNft.getMetadataContent();
        this.metadataUrl = contractNft.getMetadataUrl();
        this.items = items;
    }


    public NftInfoVo(SubContractNft contractNft, SubUser creator, List<NftItemInfoVo> items) {
        this.address = contractNft.getAddress();
        this.categoryId = contractNft.getCategoryId();
        this.tokenId = contractNft.getTokenId();
        this.royalties = contractNft.getRoyalties();
        this.nftVerify = contractNft.getNftVerify();
        this.creator = contractNft.getCreator();
        this.user = new UserBaseInfoVo(creator);
        this.txHash = contractNft.getTxHash();
        this.metadataContent = contractNft.getMetadataContent();
        this.metadataUrl = contractNft.getMetadataUrl();
        this.items = items;
    }
}
