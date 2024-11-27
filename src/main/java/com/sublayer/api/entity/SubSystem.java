package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubSystem extends BaseEntity{
    @TableField("`key_name`")
    private String keyName;

    @TableField("`key_value`")
    private String keyValue;

    @TableField("`show`")
    private Boolean show;

    public static final String KEY_NAME = "`key_name`";

    public static final String KEY_VALUE = "`key_value`";

    public static final String SHOW = "`show`";
}
