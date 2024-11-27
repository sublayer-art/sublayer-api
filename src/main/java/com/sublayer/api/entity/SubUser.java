package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.util.Date;

@Data
public class SubUser extends BaseEntity {

    @TableField("`address`")
    private String address;

    @TableField("`login_type`")
    private String loginType;

    @TableField("`last_login_time`")
    private Date lastLoginTime;

    @TableField("`last_login_ip`")
    private String lastLoginIp;

    @TableField("`user_verify`")
    private Integer userVerify;

    @TableField("`short_url`")
    private String shortUrl;

    @TableField("`brief`")
    private String brief;

    @TableField("`banner_url`")
    private String bannerUrl;

    @TableField("`is_web`")
    private Boolean isWeb;

    public static final String ADDRESS = "`address`";

    public static final String LOGIN_TYPE = "`login_type`";

    public static final String LAST_LOGIN_TIME = "`last_login_time`";

    public static final String LAST_LOGIN_IP = "`last_login_ip`";

    public static final String USER_VERIFY = "`user_verify`";

    public static final String SHORT_URL = "`short_url`";

    public static final String BRIEF = "`brief`";

    public static final String BANNER_URL = "`banner_url`";

    public static final String IS_WEB = "`is_web`";

    @Override
    public String toString() {
        return "SubUser{" +
                ", address=" + address +
                ", loginType=" + loginType +
                ", lastLoginTime=" + lastLoginTime +
                ", lastLoginIp=" + lastLoginIp +
                ", userVerify=" + userVerify +
                ", shortUrl=" + shortUrl +
                ", brief=" + brief +
                ", bannerUrl=" + bannerUrl +
                ", isWeb=" + isWeb +
                "}";
    }
}
