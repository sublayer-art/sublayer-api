package com.sublayer.api.domain.dto;

import lombok.Data;

@Data
public class ConfigDeploy {
    private String minerKey;
    private String buyerFeeKey;
    private String contractName;
    private String contractSymbol;
    private String buyerFeeAddress;
    private String minerAddress;
    private String beneficiary;
}
