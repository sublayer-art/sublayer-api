package com.sublayer.api.domain.dto;

import lombok.Data;

@Data
public class ConfigContract {
    private String multiCallAddress;
    private String nft721Address;
    private String nft1155Address;
    private String transferProxyForDeprecatedAddress;
    private String erc20TransferProxyAddress;
    private String exchangeStateAddress;
    private String exchangeOrdersHolderAddress;
    private String transferProxyAddress;
    private String nftExchangeAddress;
}