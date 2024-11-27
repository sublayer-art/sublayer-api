package com.sublayer.api.domain.vo;

import lombok.Data;

@Data
public class TxOrderInfo {
    private Integer lastBlockNumber;
    private Integer earlyBlockNumber;
    private Long txAmount;
}
