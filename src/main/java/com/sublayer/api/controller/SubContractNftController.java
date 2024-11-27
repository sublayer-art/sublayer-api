package com.sublayer.api.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.domain.dto.NftInfo;
import com.sublayer.api.domain.vo.NftParamVO;
import com.sublayer.api.domain.vo.SubOrderVo;
import com.sublayer.api.entity.SubContract;
import com.sublayer.api.entity.SubContractNft;
import com.sublayer.api.entity.SubUser;
import com.sublayer.api.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nft")
@Tag(name = "Contract NFT Controller", description = "Contract operations")
public class SubContractNftController extends BaseController{
    @Autowired
    private SubContractNftService subContractNftService;

    @Autowired
    private SubContractService subContractService;

    @Autowired
    private SubUserService subUserService;

    @Autowired
    private SubSystemService subSystemService;

    @Autowired
    private SubNftItemsService subNftItemsService;

    @PostMapping("add")
    @Operation(summary="Add contract NFT", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object add(NftInfo nft, String address) {
        String userAddress = (String) request.getAttribute("userAddress");
        if(StringUtils.isEmpty(userAddress)) {
            return R.fail("Not login");
        }
        if(StringUtils.isEmpty(address)) {
            return R.fail("bad argument");
        }
        SubContract contract = subContractService.getByAddress(address);
        if(null == contract) {
            return R.fail("bad argument");
        }
        SubUser user = subUserService.getUserByAddress(userAddress);
        if(null == user) {
            return R.fail("Not found");
        }
        if(StringUtils.isEmpty(nft.getRoyalties())) {
            nft.setRoyalties("");
        } else {
            List<BigDecimal> royalties = JSON.parseArray(nft.getRoyalties(), BigDecimal.class);
            if(null == royalties || royalties.isEmpty()) {
                nft.setRoyalties("");
            } else {
                for(BigDecimal roy : royalties) {
                    int result = roy.compareTo(new BigDecimal("1000"));
                    if(result > 0) {
                        return R.fail("Royalties can not be greater then 10");
                    }
                    result = new BigDecimal("0").compareTo(roy);
                    if(result > 0) {
                        return R.fail("Royalties can not be lower then 0");
                    }
                }
            }
        }
        nft.setContractId(contract.getId());
        nft.setCreator(user.getAddress());

        String temp = subSystemService.getKeyValue(Constants.NFT_DEFAULT_VERIFY);
        if(!StringUtils.isEmpty(temp) || "true".equals(temp)) {
            nft.setNftVerify(1);
        } else {
            nft.setNftVerify(0);
        }
        nft.setIsSync(false);

        SubContractNft contractNft = subContractNftService.save(nft);
        if(null == contractNft) {
            return R.fail("create nft fail");
        } else {
            return R.ok(contractNft);
        }
    }

    @PostMapping("detail")
    @Operation(summary="Contract NFT info", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object detail(String token, String tokenId) {
        if(StringUtils.isEmpty(token) || null == tokenId) {
            return R.ok(new JSONObject());
        }
        return subContractNftService.detail(token, tokenId);
    }

    @PostMapping("owners")
    @Operation(summary="Contract NFT owners", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object owner(String token, String tokenId, HttpServletRequest request) {
        if(StringUtils.isEmpty(token) || null == tokenId) {
            return R.ok(new ArrayList<>(0));
        }
        return subContractNftService.owners(token, tokenId);
    }

    @PostMapping("bids")
    @Operation(summary="Contract NFT bids", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object bids(String token, String tokenId) {
        if(StringUtils.isEmpty(token) || null == tokenId) {
            return R.ok(new ArrayList<>(0));
        }
        return subContractNftService.bids(token, tokenId);
    }

    @PostMapping("activebids")
    @Operation(summary="Contract NFT active bids", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object activebids(String info) {
        if(StringUtils.isEmpty(info)) {
            return R.ok(new ArrayList<>(0));
        }
        List<NftParamVO> paramVOList = this.parseNftParam(info);
        if(paramVOList.isEmpty()){
            return R.ok(new ArrayList<>(0));
        }
        List<SubOrderVo> orderVoList = subContractNftService.activebids(paramVOList);
        return R.ok(orderVoList);
    }

    @PostMapping("activesales")
    @Operation(summary="Contract NFT sales", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object activesales(String info) {
        if(StringUtils.isEmpty(info)) {
            return R.ok(new ArrayList<>(0));
        }
        List<NftParamVO> paramVOList = this.parseNftParam(info);
        if(paramVOList.isEmpty()){
            return R.ok(new ArrayList<>(0));
        }
        List<SubOrderVo> orderVoList = subContractNftService.activesales(paramVOList);
        return R.ok(orderVoList);
    }

    private List<NftParamVO> parseNftParam(String info){
        String[] nftStrs = info.split(",");
        String[] nft = null;
        List<NftParamVO> paramVOList = new ArrayList<>();
        for(String nftStr : nftStrs){
            nft = nftStr.split(":");
            paramVOList.add(new NftParamVO(nft[0], nft[1]));
        }
        return paramVOList;
    }


    @PostMapping("history")
    @Operation(summary="Contract NFT history", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object history(String token, String tokenId) {
        if(StringUtils.isEmpty(token) || null == tokenId) {
            return R.ok(new ArrayList<>(0));
        }
        return subContractNftService.history(token, tokenId);
    }

    @PostMapping("collections")
    @Operation(summary="Contract NFT collections", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object collections() {
        IPage<SubContractNft> list = subContractNftService.findCollections(this.getPageInfo());
        return R.ok(list);
    }


    @PostMapping("getmedia")
    @Operation(summary="Get contract NFT data", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object getmedia(String[] info) {
        if(null == info || info.length <= 0) {
            return R.ok(new ArrayList<>());
        }
        Map<String, String> map = subContractNftService.getMedia(info);
        return R.ok(map);
    }

    @PostMapping("getroyalties")
    @Operation(summary="Contract NFT royalties", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object getRoyalties(String[] info) {
        if(null == info || info.length <= 0) {
            return R.ok(new ArrayList<>());
        }
        Map<String, String> map = subContractNftService.getRoyalties(info);
        return R.ok(map);
    }
}
