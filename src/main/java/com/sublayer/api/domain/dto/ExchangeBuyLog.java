package com.sublayer.api.domain.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ExchangeBuyLog {
    private String sellToken;

    private BigInteger sellTokenId;

    private BigInteger sellValue;

    private String owner;

    private String buyToken;

    private BigInteger buyTokenId;

    private BigInteger buyValue;

    private String buyer;

    private BigInteger amount;

    private BigInteger salt;

    private String txHash;

    private BigInteger blockTimestamp;

    public ExchangeBuyLog(
            String sellToken,
            BigInteger sellTokenId,
            BigInteger sellValue,
            String owner,
            String buyToken,
            BigInteger buyTokenId,
            BigInteger buyValue,
            String buyer,
            BigInteger amount,
            BigInteger salt,
            String txHash,
            BigInteger blockTimestamp
    ){
        this.sellToken = sellToken;
        this.sellTokenId = sellTokenId;
        this.sellValue = sellValue;
        this.owner = owner;
        this.buyToken = buyToken;
        this.buyTokenId = buyTokenId;
        this.buyValue = buyValue;
        this.buyer = buyer;
        this.amount = amount;
        this.salt = salt;
        this.txHash = txHash;
        this.blockTimestamp = blockTimestamp;
    }
}
