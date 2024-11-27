package com.sublayer.api.controller;

import com.sublayer.api.domain.R;
import com.sublayer.api.entity.SubNftCategory;
import com.sublayer.api.service.SubNftCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@Tag(name = "NFT Category Controller", description = "NFT category info")
public class SubNftCategoryController extends BaseController{

    @Autowired
    private SubNftCategoryService subNftCategoryService;
    @PostMapping("/list")
    @Operation(summary="List all NFT Categories")
    public R<List<SubNftCategory>> listAllSelectiveCategory() {
        List<SubNftCategory> categories = subNftCategoryService.findAll();
        return R.ok(categories);
    }
}
