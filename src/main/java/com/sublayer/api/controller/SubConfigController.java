package com.sublayer.api.controller;

import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.service.SubSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@Tag(name = "Config Controller", description = "Config parameters")
public class SubConfigController extends BaseController{
    @Autowired
    private SubSystemService subConfigService;

    /**
     * @return
     */
    @PostMapping("/fetch")
    @Operation(summary="Fetch config", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public  Object fetch() {
        return R.ok(subConfigService.allShow());
    }

    @PostMapping("/gasTracker")
    @Operation(summary="Gas tracker", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public  Object gasTracker() {
        return R.ok(subConfigService.gasTracker());
    }
}
