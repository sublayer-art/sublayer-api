package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubOrder extends BaseEntity{
    @TableField("`owner`")
    private String owner;

    @TableField("`sell_token`")
    private String sellToken;

    @TableField("`sell_token_id`")
    private String sellTokenId;

    @TableField("`sell_type`")
    private Integer sellType;

    @TableField("`sell_value`")
    private String sellValue;

    @TableField("`buyer_token`")
    private String buyerToken;

    @TableField("`buyer_token_id`")
    private String buyerTokenId;

    @TableField("`buyer_type`")
    private Integer buyerType;

    @TableField("`buyer_value`")
    private String buyerValue;

    @TableField("`salt`")
    private String salt;

    @TableField("`signature`")
    private String signature;

    @TableField("`status`")
    private Integer status;

    @TableField("`expired`")
    private Boolean expired;

    @TableField("`order_type`")
    private Integer orderType;

    @TableField("`usdt_price`")
    private String usdtPrice;

    @TableField("`sells`")
    private String sells;

    @TableField("`paytoken_address`")
    private String paytokenAddress;

    @TableField("`paytoken_name`")
    private String paytokenName;

    @TableField("`paytoken_decimals`")
    private Integer paytokenDecimals;

    @TableField("`paytoken_symbol`")
    private String paytokenSymbol;

    @TableField("`buy_fee`")
    private Integer buyFee;

    @TableField("`sell_fee`")
    private Integer sellFee;

    public static final String OWNER = "`owner`";

    public static final String SELL_TOKEN = "`sell_token`";

    public static final String SELL_TOKEN_ID = "`sell_token_id`";

    public static final String SELL_TYPE = "`sell_type`";

    public static final String SELL_VALUE = "`sell_value`";

    public static final String BUYER_TOKEN = "`buyer_token`";

    public static final String BUYER_TOKEN_ID = "`buyer_token_id`";

    public static final String BUYER_TYPE = "`buyer_type`";

    public static final String BUYER_VALUE = "`buyer_value`";

    public static final String SALT = "`salt`";

    public static final String SIGNATURE = "`signature`";

    public static final String STATUS = "`status`";

    public static final String EXPIRED = "`expired`";

    public static final String ORDER_TYPE = "`order_type`";

    public static final String USDT_PRICE = "`usdt_price`";

    public static final String SELLS = "`sells`";

    public static final String PAYTOKEN_ADDRESS = "`paytoken_address`";

    public static final String PAYTOKEN_NAME = "`paytoken_name`";

    public static final String PAYTOKEN_DECIMALS = "`paytoken_decimals`";

    public static final String PAYTOKEN_SYMBOL = "`paytoken_symbol`";

    public static final String BUY_FEE = "`buy_fee`";

    public static final String SELL_FEE = "`sell_fee`";
}
