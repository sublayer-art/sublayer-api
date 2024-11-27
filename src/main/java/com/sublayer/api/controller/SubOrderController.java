package com.sublayer.api.controller;

import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.domain.dto.PrepareOrderInfo;
import com.sublayer.api.entity.SubUser;
import com.sublayer.api.service.SubOrderService;
import com.sublayer.api.service.SubUserService;
import com.sublayer.api.utils.DappCryptoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
@Tag(name = "Order Controller", description = "Order operations")
public class SubOrderController extends BaseController{
    @Autowired
    SubOrderService subOrderService;

    @Autowired
    SubUserService subUserService;

    @PostMapping("add")
    @Operation(summary="Add order", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object add(PrepareOrderInfo order) {
        String userAddress = (String) request.getAttribute("userAddress");
        if(StringUtils.isEmpty(userAddress)) {
            return R.fail("Not login");
        }
        SubUser user = subUserService.getUserByAddress(userAddress);
        if(null == user) {
            return R.fail("Not login");
        }
        if(!user.getAddress().equalsIgnoreCase(order.getOwner())){
            return R.fail("order owner incorrect");
        }
        if(null == order.getType()){
            return R.fail("order type is empty");
        }

        if(!DappCryptoUtil.validate(order.getSignature(), order.getMessage(), user.getAddress())) {
            return R.fail("signature is incorrect");
        }
        return subOrderService.addOrder(order);
    }

    @PostMapping(value = "/prepare")
    @Operation(summary="Prepare order", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object prepare(PrepareOrderInfo order) throws Exception {
        String userAddress = (String) request.getAttribute("userAddress");
        if(StringUtils.isEmpty(userAddress)) {
            return R.fail("Not login");
        }
        SubUser user = subUserService.getUserByAddress(userAddress);
        if(null == user) {
            return R.fail("Not login");
        }

        if(!this.checkOrder(order, user.getAddress())) {
            return R.fail("invalid order");
        }
        return subOrderService.prepareOrder(order, user);
    }

    @PostMapping(value = "/buyerFee")
    @Operation(summary="Buy order", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object buyPrepare(PrepareOrderInfo order) throws Exception {
        if((StringUtils.isEmpty(order.getBuyToken()) ||
                null == order.getBuyTokenId() ||
                StringUtils.isEmpty(order.getOwner()) ||
                StringUtils.isEmpty(order.getSalt()) ||
                null == order.getType()) &&
                ((StringUtils.isEmpty(order.getSellToken()) ||
                        null == order.getSellTokenId() ||
                        StringUtils.isEmpty(order.getOwner()) ||
                        StringUtils.isEmpty(order.getSalt()) ||
                        null == order.getType())))
        {
            return R.fail("parameter invalid");
        }
        String userAddress = (String) request.getAttribute("userAddress");
        if(null == userAddress) {
            return R.fail("Not login");
        }
        SubUser user = subUserService.getUserByAddress(userAddress);
        if(null == user) {
            return R.fail("Not login");
        }
        return subOrderService.buyPrepare(order);
    }

    @PostMapping(value = "/get")
    @Operation(summary="Order query", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object get(String caddress, String tokenId, String owner, Integer type) {
        return subOrderService.get(caddress, tokenId, owner, type);
    }

    /**
     * @param order
     * @param address
     * @return
     */
    private boolean checkOrder(PrepareOrderInfo order, String address) {
        if(null == order
                || StringUtils.isEmpty(order.getOwner())
                || !address.equalsIgnoreCase(order.getOwner())
                || StringUtils.isEmpty(order.getBuyToken())
                || StringUtils.isEmpty(order.getBuyValue())
                || StringUtils.isEmpty(order.getBuyTokenId())
                || StringUtils.isEmpty(order.getSellToken())
                || StringUtils.isEmpty(order.getSellTokenId())
                || StringUtils.isEmpty(order.getSellValue())
        ) {
            return false;
        }

        int temp = new BigDecimal(order.getBuyValue()).compareTo(new BigDecimal("0"));
        if(temp <= 0) {
            return false;
        }
        temp = new BigDecimal(order.getSellValue()).compareTo(new BigDecimal("0"));
        if(temp <= 0) {
            return false;
        }
        return true;
    }
}
