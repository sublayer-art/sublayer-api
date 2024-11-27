package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubUserToken extends BaseEntity{
    @TableField("`user_address`")
    private String userAddress;

    @TableField("`user_token`")
    private String userToken;

    public static final String USER_ADDRESS = "`user_address`";

    public static final String USER_TOKEN = "`user_token`";
}
