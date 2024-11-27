package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubStorage extends BaseEntity{
    @TableField("`key`")
    private String key;

    @TableField("`name`")
    private String name;

    @TableField("`type`")
    private String type;

    @TableField("`size`")
    private Integer size;

    @TableField("`url`")
    private String url;

    @TableField("`ipfsHash`")
    private String ipfshash;

    public static final String KEY = "`key`";

    public static final String NAME = "`name`";

    public static final String TYPE = "`type`";

    public static final String SIZE = "`size`";

    public static final String URL = "`url`";

    public static final String IPFSHASH = "`ipfsHash`";
}
