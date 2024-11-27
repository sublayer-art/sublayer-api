package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubContract extends BaseEntity{

    @TableField("`name`")
    private String name;

    @TableField("`symbol`")
    private String symbol;

    @TableField("`address`")
    private String address;

    @TableField("`short_url`")
    private String shortUrl;

    @TableField("`version`")
    private String version;

    @TableField("`cover`")
    private String cover;

    @TableField("`cover_ipfs`")
    private String coverIpfs;

    @TableField("`storage_id`")
    private Long storageId;

    @TableField("`owner`")
    private String owner;

    @TableField("`is_admin`")
    private Boolean isAdmin;

    @TableField("`verify`")
    private Boolean verify;

    @TableField("`description`")
    private String description;

    @TableField("`last_token_id`")
    private Long lastTokenId;

    @TableField("`banner_url`")
    private String bannerUrl;

    @TableField("`get_info_times`")
    private Integer getInfoTimes;

    @TableField("`is_royalties`")
    private Boolean isRoyalties;

    @TableField("`signer`")
    private String signer;

    @TableField("`is_sync`")
    private Boolean isSync;

    public static final String NAME = "`name`";

    public static final String SYMBOL = "`symbol`";

    public static final String ADDRESS = "`address`";

    public static final String SHORT_URL = "`short_url`";

    public static final String VERSION = "`version`";

    public static final String COVER = "`cover`";

    public static final String STORAGE_ID = "`storage_id`";

    public static final String OWNER = "`owner`";

    public static final String IS_ADMIN = "`is_admin`";

    public static final String VERIFY = "`verify`";

    public static final String DESCRIPTION = "`description`";

    public static final String LAST_TOKEN_ID = "`last_token_id`";

    public static final String BANNER_URL = "`banner_url`";

    public static final String GET_INFO_TIMES = "`get_info_times`";

    public static final String IS_ROYALTIES = "`is_royalties`";

    public static final String SIGNER = "`signer`";

    public static final String IS_SYNC = "`is_sync`";
}
