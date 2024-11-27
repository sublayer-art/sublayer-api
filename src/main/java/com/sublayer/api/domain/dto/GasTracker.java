package com.sublayer.api.domain.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class GasTracker {
    private String low;
    private String medium;
    private String high;

    public String getLastBlock() {
        return lastBlock;
    }

    public void setLastBlock(String lastBlock) {
        this.lastBlock = lastBlock;
    }

    private String lastBlock;

    public GasTracker(BigInteger low, BigInteger medium, BigInteger high, BigInteger lastBlock){
        this.low = low.toString();
        this.medium = medium.toString();
        this.high = high.toString();
        this.lastBlock = lastBlock.toString();
    }
}
