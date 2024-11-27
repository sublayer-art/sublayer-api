package com.sublayer.api.controller;

import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.entity.SubUser;
import com.sublayer.api.service.SubContractService;
import com.sublayer.api.service.SubSystemService;
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
@RequestMapping("/dapp")
@Tag(name = "Dapp Controller", description = "Sign operations")
public class SubDappController extends BaseController{
    @Autowired
    SubContractService subContractService;

    @Autowired
    SubUserService subUserService;

    @Autowired
    SubSystemService subSystemService;

    @PostMapping(value = "/sign")
    @Operation(summary="Sign message", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object sign(String address) throws Exception {
        String  userAddress = (String) request.getAttribute("userAddress");
        if(StringUtils.isEmpty(userAddress)) {
            return R.fail("Not login");
        }
        SubUser user = subUserService.getUserByAddress(userAddress);
        if(null == user) {
            return R.fail("Not login");
        }
        return subContractService.MinerTokenId(address);
    }
}
