package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubOrderLog  extends BaseEntity{
    @TableField("`order_id`")
    private Long orderId;

    @TableField("`from`")
    private String from;

    @TableField("`to`")
    private String to;

    @TableField("`type`")
    private Integer type;

    @TableField("`tx_hash`")
    private String txHash;

    @TableField("`pre_log_id`")
    private Long preLogId;

    @TableField("`token`")
    private String token;

    @TableField("`token_id`")
    private String tokenId;

    @TableField("`content`")
    private String content;

    @TableField("`expired`")
    private Boolean expired;

    @TableField("`paytoken_address`")
    private String paytokenAddress;

    @TableField("`paytoken_name`")
    private String paytokenName;

    @TableField("`paytoken_symbol`")
    private String paytokenSymbol;

    @TableField("`paytoken_decimals`")
    private Integer paytokenDecimals;

    public static final String ORDER_ID = "`order_id`";

    public static final String FROM = "`from`";

    public static final String TO = "`to`";

    public static final String TYPE = "`type`";

    public static final String TX_HASH = "`tx_hash`";

    public static final String PRE_LOG_ID = "`pre_log_id`";

    public static final String TOKEN = "`token`";

    public static final String TOKEN_ID = "`token_id`";

    public static final String CONTENT = "`content`";

    public static final String EXPIRED = "`expired`";

    public static final String PAYTOKEN_ADDRESS = "`paytoken_address`";

    public static final String PAYTOKEN_NAME = "`paytoken_name`";

    public static final String PAYTOKEN_SYMBOL = "`paytoken_symbol`";

    public static final String PAYTOKEN_DECIMALS = "`paytoken_decimals`";
}
