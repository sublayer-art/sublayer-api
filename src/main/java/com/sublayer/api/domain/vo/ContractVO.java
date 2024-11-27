package com.sublayer.api.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractVO {
    private Long id;

    private String name;

    private String symbol;

    private String address;

    private String shortUrl;

    private String cover;

    private String owner;

    private Boolean isAdmin;

    private String description;

    private Long totalSell;

    private BigDecimal totalDeal;
}
