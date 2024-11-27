package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubTxOrder extends BaseEntity{
    @TableField("`tx_hash`")
    private String txHash;

    @TableField("`block_number`")
    private Integer blockNumber;

    @TableField("`block_timestamp`")
    private Integer blockTimestamp;

    public static final String TX_HASH = "`tx_hash`";

    public static final String BLOCK_NUMBER = "`block_number`";

    public static final String BLOCK_TIMESTAMP = "`block_timestamp`";

    @Override
    public String toString() {
        return "FcTxOrder{" +
                "txHash=" + txHash +
                ", blockNumber=" + blockNumber +
                ", blockTimestamp=" + blockTimestamp +
                "}";
    }
}
