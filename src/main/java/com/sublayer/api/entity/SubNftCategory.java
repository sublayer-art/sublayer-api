package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubNftCategory extends BaseEntity{
    @TableField("`name`")
    private String name;
    @TableField("`order`")
    private Integer order;

    public static final String NAME = "`name`";

    public static final String ORDER = "`order`";
}
