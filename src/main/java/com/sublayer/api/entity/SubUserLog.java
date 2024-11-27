package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubUserLog extends BaseEntity{

    @TableField("`address`")
    private String address;

    @TableField("`type`")
    private Integer type;

    @TableField("`ip`")
    private String ip;

    @TableField("`action`")
    private String action;

    @TableField("`status`")
    private Boolean status;

    @TableField("`result`")
    private String result;

    public static final String ADDRESS = "`address`";

    public static final String TYPE = "`type`";

    public static final String IP = "`ip`";

    public static final String ACTION = "`action`";

    public static final String STATUS = "`status`";

    public static final String RESULT = "`result`";
}
