package com.sublayer.api.domain.dto;

import lombok.Data;

@Data
public class NftInfo {
    private Long contractId;

    private String address;

    private Long categoryId;

    private String imgUrl;

    private Long storageId;

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

    private Long animStorageId;

    private String metadataUrl;

    private String metadataContent;

    private Integer getMetaTimes;
}
