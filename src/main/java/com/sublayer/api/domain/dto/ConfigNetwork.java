package com.sublayer.api.domain.dto;

import lombok.Data;

@Data
public class ConfigNetwork {
    private String network;
    private Integer chainId;
    private String name;
    private String symbol;
    private String explorer;
    private String opensea;
    private String rpc;
    private Integer blockTime;
}
