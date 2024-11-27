package com.sublayer.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SubContractNft extends BaseEntity{
    @TableField("`contract_id`")
    private Long contractId;

    @TableField("`address`")
    private String address;

    @TableField("`name`")
    private String name;

    @TableField("`description`")
    private String description;

    @TableField("`category_id`")
    private Long categoryId;

    @TableField("`storage_id`")
    private Long storageId;

    @TableField("`token_id`")
    private String tokenId;

    @TableField("`royalties`")
    private String royalties;

    @TableField("`nft_verify`")
    private Integer nftVerify;

    @TableField("`is_sync`")
    private Boolean isSync;

    @TableField("`creator`")
    private String creator;

    @TableField("`tx_hash`")
    private String txHash;

    @TableField("`img_url`")
    private String imgUrl;

    @TableField("`metadata_url`")
    private String metadataUrl;

    @TableField("`metadata_content`")
    private String metadataContent;

    @TableField("`get_meta_times`")
    private Integer getMetaTimes;

    public static final String CONTRACT_ID = "`contract_id`";

    public static final String ADDRESS = "`address`";

    public static final String NAME = "`name`";

    public static final String DESCRIPTION = "`description`";

    public static final String CATEGORY_ID = "`category_id`";

    public static final String STORAGE_ID = "`storage_id`";

    public static final String TOKEN_ID = "`token_id`";

    public static final String ROYALTIES = "`royalties`";

    public static final String NFT_VERIFY = "`nft_verify`";

    public static final String IS_SYNC = "`is_sync`";

    public static final String CREATOR = "`creator`";

    public static final String TX_HASH = "`tx_hash`";

    public static final String METADATA_URL = "`metadata_url`";

    public static final String METADATA_CONTENT = "`metadata_content`";

    public static final String GET_META_TIMES = "`get_meta_times`";
}
