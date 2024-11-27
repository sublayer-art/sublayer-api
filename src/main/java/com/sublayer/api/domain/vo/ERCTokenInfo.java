package com.sublayer.api.domain.vo;

import lombok.Data;

@Data
public class ERCTokenInfo {

    private String name;

    private String contractName;

    private String contractSymbol;

    private Integer contractDecimals;

    private String creator;

    private String properties;

    private String content;

    private String uri;

    private String description;
}
