package com.sublayer.api.domain.vo;

import lombok.Data;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Type;

import java.math.BigInteger;
import java.util.List;

@Data
public class EventValuesExt {
    private List<Type> indexedValues;

    private List<Type> nonIndexedValues;

    private String txHash;

    private String address;

    private BigInteger blockNumber;

    private BigInteger blockTimestamp;

    public EventValuesExt(EventValues eventValues, String txHash, String address, BigInteger blockNumber, BigInteger blockTimestamp) {
        this.indexedValues = eventValues.getIndexedValues();
        this.nonIndexedValues = eventValues.getNonIndexedValues();
        this.txHash = txHash;
        this.address = address;
        this.blockNumber = blockNumber;
        this.blockTimestamp = blockTimestamp;
    }
}
