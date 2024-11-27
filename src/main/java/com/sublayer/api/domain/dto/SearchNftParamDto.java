package com.sublayer.api.domain.dto;

import lombok.Data;

@Data
public class SearchNftParamDto {
    private String creator;
    private String owner;
    private String address;
    private String tokenId;
}
