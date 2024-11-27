package com.sublayer.api.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.domain.vo.SubContractVo;
import com.sublayer.api.entity.SubContract;
import com.sublayer.api.manager.SubContractNftManager;
import com.sublayer.api.service.SubContractNftService;
import com.sublayer.api.service.SubContractService;
import com.sublayer.api.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contract")
@Tag(name = "Contract Controller", description = "Contract operations")
public class SubContractController extends BaseController{

    @Autowired
    private SubContractService subContractService;

    @Autowired
    private SubContractNftService subContractNftService;

    @PostMapping("add")
    @Operation(summary="Add contract", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public R<SubContract> add(SubContract subContract) {
        String userAddress = (String) request.getAttribute("userAddress");
        if (StringUtils.isEmpty(userAddress)) {
            return R.fail("Please login first");
        }

        SubContract temp = subContractService.getByAddress(subContract.getAddress());
        if (null == temp) {
            subContract.setDeleted(false);
            subContract.setOwner(userAddress);
            subContract.setIsAdmin(false);
            Integer count = subContractService.save(subContract);
            if (count == 0) {
                return R.fail( "System error");
            } else {
                return R.ok(temp);
            }
        } else {
            return R.ok(temp);
        }
    }

    @PostMapping("listall")
    @Operation(summary="Contract list all", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public R<IPage<SubContractVo>> listAll() {
        IPage<SubContract> contractList = subContractService.listAll(this.getPageInfo());
        return R.ok(contractList.convert(contract -> {
            SubContractVo contractVo = new SubContractVo();
            BeanUtils.copyProperties(contract, contractVo);
            contractVo.setCollectionCount(subContractNftService.countContractCollections(contract.getAddress()));
            contractVo.setSaleCount(subContractNftService.countContractOnsale(contract.getAddress()));
            contractVo.setPrice(subContractNftService.flootContractOnSale(contract.getAddress()));
            return contractVo;
        }));
    }

    @PostMapping("info")
    @Operation(summary="Contract info", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public R<SubContractVo> info(String caddress) {
        if (StringUtils.isEmpty(caddress)) {
            return R.ok();
        }
        SubContract contract = subContractService.getByAddress(caddress);
        if (null == contract) {
            return R.ok();
        }
        SubContractVo contractVo = new SubContractVo();
        BeanUtils.copyProperties(contract, contractVo);
        Integer saleCount = subContractNftService.countContractOnsale(caddress);
        Long collectionCount = subContractNftService.countContractCollections(caddress);
        contractVo.setSaleCount(saleCount);
        contractVo.setCollectionCount(collectionCount);
        contractVo.setPrice(subContractNftService.flootContractOnSale(caddress));
        return R.ok(contractVo);
    }

    @PostMapping("getinfo")
    @Operation(summary="Get contract data", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public R<SubContract> getinfo(String address) {
        if (StringUtils.isEmpty(address)) {
            return R.ok();
        }
        SubContract contract = subContractService.getInfo(address);
        if (null == contract) {
            return R.ok();
        }
        return R.ok(contract);
    }

    @PostMapping("list")
    @Operation(summary="Contract list", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public R<List<SubContract>> list(HttpServletRequest request) {
        return R.ok(subContractService.findAll());
    }

    @PostMapping("create")
    @Operation(summary="Create contract", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public R<Void> create(SubContract contract) {
        Integer save = subContractService.save(contract);
        if (save > 0) {
            return R.ok();
        } else {
            return R.fail("save fail");
        }
    }

    @PostMapping("listbyaddr")
    @Operation(summary="Contract query by list", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public R<List<SubContract>> listByAddress(String[] addresss) {
        if (null == addresss || addresss.length == 0) {
            return R.ok();
        }
        List<String> addrList = Arrays.asList(addresss);
        return R.ok(subContractService.findListByAdress(addrList));
    }

    @PostMapping("listitems")
    @Operation(summary="Contract NFT items query", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object listContractItems(String address, Boolean isSell) {
        SubContract contract = subContractService.getByAddress(address);
        if (null == contract) {
            return R.ok(this.getPageInfo());
        }
        return subContractNftService.findContractNft(this.getPageInfo(), address, isSell);
    }

    @PostMapping("stat")
    @Operation(summary="Contract NFT total query", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object stat(String address) {
        Map<String, Object> result = new HashMap<String, Object>();
        Integer saleCount = subContractNftService.countContractOnsale(address);
        Long collectionCount = subContractNftService.countContractCollections(address);
        result.put("saleCount", saleCount);
        result.put("collectionCount", collectionCount);
        return R.ok(result);
    }
}
