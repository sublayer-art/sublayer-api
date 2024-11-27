package com.sublayer.api.domain.vo;

import lombok.Data;

@Data
public class SubContractNftWithBidVO {
    private Long id;

    private String address;

    private Long categoryId;

    private String imgUrl;

    private String tokenId;

    private Boolean locked;

    private String lockedContent;

    private String name;

    private String description;

    private String royalties;

    private String properties;

    private Integer nftVerify;

    private Boolean isSync;

    private String creator;

    private String txHash;

    private String animUrl;

    private String metadataContent;

    private Long bidNums;
}
