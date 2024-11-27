package com.sublayer.api.domain.vo;

import com.sublayer.api.entity.SubContract;
import lombok.Data;

@Data
public class SubContractVo extends SubContract {
    Integer saleCount;
    Long collectionCount;
    String price;
}
