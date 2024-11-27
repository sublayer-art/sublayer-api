package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubPayToken extends BaseEntity{
    @TableField("`name`")
    private String name;

    @TableField("`symbol`")
    private String symbol;

    @TableField("`decimals`")
    private Integer decimals;

    @TableField("`rate`")
    private BigDecimal rate;

    @TableField("`address`")
    private String address;

    @TableField("`avatar`")
    private String avatar;

    @TableField("`storage_id`")
    private Long storageId;

    @TableField("`type`")
    private Integer type;

    @TableField("`is_default`")
    private Integer isDefault;

    public static final String NAME = "`name`";

    public static final String SYMBOL = "`symbol`";

    public static final String DECIMALS = "`decimals`";

    public static final String RATE = "`rate`";

    public static final String ADDRESS = "`address`";

    public static final String AVATAR = "`avatar`";

    public static final String STORAGE_ID = "`storage_id`";

    public static final String TYPE = "`type`";

    public static final String IS_DEFAULT = "`is_default`";
}
