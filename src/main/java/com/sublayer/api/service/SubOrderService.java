package com.sublayer.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.constants.CommonStatus;
import com.sublayer.api.constants.ContractType;
import com.sublayer.api.constants.OrderStatus;
import com.sublayer.api.domain.R;
import com.sublayer.api.domain.dto.*;
import com.sublayer.api.domain.vo.NftParamVO;
import com.sublayer.api.domain.vo.SubOrderVo;
import com.sublayer.api.entity.*;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.manager.SubContractNftManager;
import com.sublayer.api.manager.SubOrderManager;
import com.sublayer.api.mapper.SubNftItemsExtMapper;
import com.sublayer.api.mapper.SubOrderExtMapper;
import com.sublayer.api.utils.DappCryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubOrderService {

    public static final Logger logger = LoggerFactory.getLogger(SubOrderService.class);

    @Autowired
    IBaseService baseService;

    @Autowired
    private SubContractService subContractService;

    @Autowired
    private SubContractNftManager subContractNftManager;

    @Autowired
    private SubPayTokenService subPayTokenService;

    @Autowired
    private SubSystemService subSystemService;

    @Autowired
    private SubNftItemsService subNftItemsService;

    @Autowired
    private SubOrderManager subOrderManager;

    @Transactional(rollbackFor = Exception.class)
    public Object addOrder(PrepareOrderInfo order) {
        CommonStatus status = CommonStatus.getStatusByType(order.getType());
        switch (status) {
            case SALE:
            case EDIT_SALE:
                return processSell(order);
            case BID:
            case EDIT_BID:
                return processBid(order);
        }
        return R.fail("order type is incorrect");
    }

    private Object processBid(PrepareOrderInfo info) {
        SubContract contract = subContractService.get(info.getBuyToken());
        if (null == contract) {
            return R.fail("nft contract is not existed");
        }

        SubContractNft nft = subContractNftManager.get(info.getBuyToken(), info.getBuyTokenId());
        if (null == nft) {
            return R.fail("Unkown nft");
        }

        if (new BigInteger("1").compareTo(new BigInteger(info.getBuyValue())) < 0) {
            return R.fail("no enough items for bid");
        }

        SubPayToken payToken = subPayTokenService.get(info.getSellToken());
        if (null == payToken) {
            return R.fail("paytoken is not existed");
        }

        SubOrder order = subOrderManager.getBuyerOrder(info.getBuyToken(), info.getBuyTokenId(), info.getOwner());

        if (info.getType().equals(6)) {
            if (null == order) {
                return R.fail( "Bid is not existed or order is canceled");
            }
            BigDecimal oldValue = new BigDecimal(order.getSellValue()).divide(new BigDecimal(order.getBuyerValue()), RoundingMode.HALF_UP);
            BigDecimal newValue = new BigDecimal(info.getSellValue()).divide(new BigDecimal(info.getBuyValue()), RoundingMode.HALF_UP);
            if(oldValue.compareTo(newValue) >= 0){
                return R.fail( "New price must greater then the old price");
            }
        } else {
            if (null != order) {
                return R.fail("Bid is existed, please cancel it first");
            }
        }
        info.setBuyType(ContractType.ERC721.getType().toString());

        Integer count = subContractNftManager.bid(info);
        if(count.equals(0)){
            return R.fail("add bid order fail");
        }
        return R.ok();
    }

    private Object processSell(PrepareOrderInfo info) {
        SubContract contract = subContractService.get(info.getSellToken());
        if (null == contract) {
            return R.fail("bad argument");
        }

        SubContractNft nft = subContractNftManager.get(info.getSellToken(), info.getSellTokenId());
        if (null == nft) {
            return R.fail("Then token is not existed or bured");
        }

        SubNftItems nftItems = subNftItemsService.get(info.getSellToken(), info.getSellTokenId(), info.getOwner());

        if (null == nftItems) {
            return R.fail("Then token is not existed or owner is not you");
        }
        if (nftItems.getIsSync() &&
                !new BigInteger("1").equals(new BigInteger(info.getSellValue()))
        ) {
            return R.fail("no enough quantity");
        }

        SubPayToken payToken = subPayTokenService.get(info.getBuyToken());
        if (null == payToken) {
            return R.fail("Unknown pay type");
        }

        SubOrder order = subOrderManager.getSellOrder(info.getSellToken(), info.getSellTokenId(), info.getOwner());
        if (info.getType().equals(2)) {
            if (null == order) {
                return R.fail("Order is not existed or order is canceled");
            }
            BigDecimal oldValue = new BigDecimal(order.getBuyerValue()).divide(new BigDecimal(order.getSellValue()), RoundingMode.HALF_UP);
            BigDecimal newValue = new BigDecimal(info.getBuyValue()).divide(new BigDecimal(info.getSellValue()), RoundingMode.HALF_UP);
            if(oldValue.compareTo(newValue) <= 0) {
                return R.fail("New price must less then the old price");
            }

        } else {
            if (null != order) {
                return R.fail("This nft is on sale already");
            }
        }

        info.setSellType(ContractType.ERC721.getType().toString());
        Integer count = subContractNftManager.sale(info);
        if(count.equals(0)){
            return R.fail("add sale order fail");
        }
        return R.ok();
    }


    public Object prepareOrder(PrepareOrderInfo info, SubUser user) {
        String sellFee = subSystemService.getKeyValue(Constants.SELLER_FEE);
        if (null == sellFee) {
            return R.fail("unset sellFee");
        }
        if(info.getType().equals(OrderStatus.SALE.getType())){
            info.setSellType(ContractType.ERC721.getType().toString());
        }else{
            info.setBuyType(ContractType.ERC721.getType().toString());
        }

        info.setSellFee(sellFee);
        SignOrderInfo signOrder = new SignOrderInfo(info);
        signOrder = DappCryptoUtil.prepareOrder(signOrder);
        info.setMessage(signOrder.getSignature());
        info.setSalt(signOrder.getSalt());
        return R.ok(info);
    }


    public Object buyPrepare(PrepareOrderInfo orderInfo) {
        SubOrder order = null;
        if (orderInfo.getType().intValue() == 1) {
            order = subOrderManager.getSellOrder(orderInfo.getSellToken(), orderInfo.getSellTokenId(), orderInfo.getOwner(), orderInfo.getSalt());
        } else if (orderInfo.getType().intValue() == 2) {
            order = subOrderManager.getBuyerOrder(orderInfo.getBuyToken(), orderInfo.getBuyTokenId(), orderInfo.getOwner(), orderInfo.getSalt());
        }
        if (null == order) {
            return R.fail("Order is not exist");
        }
        if (null != order.getStatus() && (order.getStatus().intValue() == -1 || order.getStatus().intValue() == 2)) {
            return R.fail("Order is finished");
        }
        if (null != order.getExpired() && order.getExpired().booleanValue()) {
            return R.fail("Order is expired");
        }

        PrepareOrderInfo info = new PrepareOrderInfo(order);
        info.setSellFee(order.getSellFee().toString());
        info.setBuyFee(order.getBuyFee().toString());
        SignOrderInfo signOrder = new SignOrderInfo(info);

        ConfigDeploy configDeploy = subSystemService.getConfigDeploy();
        String buyerFeeKey = configDeploy.getBuyerFeeKey();
        signOrder = DappCryptoUtil.orderSign(signOrder, order.getBuyFee(), buyerFeeKey);
        info.setSignature(signOrder.getSignature());
        info.setR(signOrder.getR());
        info.setS(signOrder.getS());
        info.setV(signOrder.getV());
        info.setSalt(signOrder.getSalt());
        return R.ok(info);
    }


    public Object get(String caddress, String tokenId, String owner, Integer orderType) {
        SubOrder order = null;
        if(orderType.equals(OrderStatus.SALE.getType())) {
            order = subOrderManager.getActiveSellOrder(caddress, tokenId, owner, orderType);
        }else if(orderType.equals(OrderStatus.BID.getType())){
            order = subOrderManager.getActiveBuyerOrder(caddress, tokenId, owner, orderType);
        }
        if (null == order) {
            return R.fail( "order not exist or order is finished");
        }
        return R.ok(order);
    }
}
