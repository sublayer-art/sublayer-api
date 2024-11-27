package com.sublayer.api.controller;

import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.service.SubPayTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paytoken")
@Tag(name = "Pay token Controller", description = "Pay token operations")
public class SubPayTokenController extends BaseController{
    @Autowired
    SubPayTokenService subPayTokenService;

    @PostMapping(value = "/list")
    @Operation(summary="List pay tokens", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object list() {
        return R.ok(subPayTokenService.all());
    }
}
