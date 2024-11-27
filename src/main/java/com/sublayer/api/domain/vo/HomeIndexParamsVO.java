package com.sublayer.api.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HomeIndexParamsVO {
    private String address;
    private Long cate;
    private List<String> contracts;
    private String payToken;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String search;
    private String sort;
    private String order;
    private Integer sellType;
}
