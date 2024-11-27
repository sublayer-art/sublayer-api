package com.sublayer.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.domain.vo.HomeIndexParamsVO;
import com.sublayer.api.domain.vo.NftInfoVo;
import com.sublayer.api.entity.SubUser;
import com.sublayer.api.service.SubContractNftService;
import com.sublayer.api.service.SubUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@Tag(name = "Homepage Controller", description = "Homepage query")
public class SubHomeController extends BaseController{

    @Autowired
    SubContractNftService subContractNftService;

    @Autowired
    SubUserService subUserService;

    /**
     * @param params
     * @return
     */
    @PostMapping("list")
    @Operation(summary="Contract NFT list", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object list(HomeIndexParamsVO params) {
        IPage<NftInfoVo> iPage = subContractNftService.findListForIndex(this.getPageInfo(), params);
        return R.ok(iPage);
    }

    /**
     * @param search
     * @return
     */
    @PostMapping("search")
    @Operation(summary="Contract NFT search", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object search(String search) {

        if(StringUtils.isEmpty(search)){
            search = "";
        }
        return subContractNftService.findSearch(search, this.getPageInfo());
    }
}
