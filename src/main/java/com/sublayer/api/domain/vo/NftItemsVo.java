package com.sublayer.api.domain.vo;

import lombok.Data;

@Data
public class NftItemsVo {
    private String name;

    private String description;

    private String owner;

    private String token;

    private String tokenId;

    private String metaContent;

    private String metaUrl;
}
