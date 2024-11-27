package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class SubNftItems extends BaseEntity{
    @TableField("`address`")
    private String address;

    @TableField("`token_id`")
    private String tokenId;

    @TableField("`price`")
    private String price;

    @TableField("`usdt_price`")
    private String usdtPrice;

    @TableField("`paytoken_address`")
    private String paytokenAddress;

    @TableField("`paytoken_name`")
    private String paytokenName;

    @TableField("`paytoken_decimals`")
    private Integer paytokenDecimals;

    @TableField("`paytoken_symbol`")
    private String paytokenSymbol;

    @TableField("`signature`")
    private String signature;

    @TableField("`item_owner`")
    private String itemOwner;

    @TableField("`category_id`")
    private Long categoryId;

    @TableField("`onsell`")
    private Boolean onsell;

    @TableField("`onsell_time`")
    private Date onsellTime;

    @TableField("`is_sync`")
    private Boolean isSync;

    public static final String ADDRESS = "`address`";

    public static final String TOKEN_ID = "`token_id`";

    public static final String PRICE = "`price`";

    public static final String USDT_PRICE = "`usdt_price`";

    public static final String PAYTOKEN_ADDRESS = "`paytoken_address`";

    public static final String PAYTOKEN_NAME = "`paytoken_name`";

    public static final String PAYTOKEN_DECIMALS = "`paytoken_decimals`";

    public static final String PAYTOKEN_SYMBOL = "`paytoken_symbol`";

    public static final String SIGNATURE = "`signature`";

    public static final String ITEM_OWNER = "`item_owner`";

    public static final String CATEGORY_ID = "`category_id`";

    public static final String ONSELL = "`onsell`";

    public static final String ONSELL_TIME = "`onsell_time`";

    public static final String IS_SYNC = "`is_sync`";
}
