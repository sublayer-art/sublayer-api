package com.sublayer.api.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.constants.ContractType;
import com.sublayer.api.domain.vo.ERCTokenInfo;
import com.sublayer.api.entity.BaseEntity;
import com.sublayer.api.entity.SubPayToken;
import com.sublayer.api.utils.DappWeb3jUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubPayTokenService {

    @Autowired
    IBaseService baseService;

    @Autowired
    SubSystemService systemConfigManager;

    public List<SubPayToken> all() {
        QueryWrapper<SubPayToken> wrapper = new QueryWrapper<>();
        wrapper.eq(BaseEntity.DELETED, false);
        return this.baseService.findByCondition(SubPayToken.class, wrapper);
    }

    public SubPayToken getPayTokenInfo(SubPayToken payToken){
        if(payToken.getType().equals(ContractType.ETH.getType())){
            // paytoken type == mainnet coin
            String ConfigNetwork = this.systemConfigManager.getKeyValue(Constants.CONFIG_NETWORK);
            payToken.setDecimals(18);
            if(ConfigNetwork.isEmpty()) {
                payToken.setName("ETH");
                payToken.setSymbol("ETH");
                payToken.setAddress(Constants.ZERO_ADDRESS);
            }else{
                Map<String, Object> networkMap = JSON.parseObject(ConfigNetwork);
                String symbol = (String) networkMap.get("symbol");
                payToken.setName(symbol);
                payToken.setSymbol(symbol);
                payToken.setAddress(Constants.ZERO_ADDRESS);
            }
            return payToken;
        }
        try {
            if(payToken.getType().equals(ContractType.ERC20.getType())){
                // get erc20 name + symbol + decimals at chain
                ERCTokenInfo info = DappWeb3jUtil.getErc20Info(payToken.getAddress());
                payToken.setName(info.getContractName());
                payToken.setDecimals(info.getContractDecimals());
                payToken.setSymbol(info.getContractSymbol());
                return payToken;
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

    public SubPayToken getETH(){
        QueryWrapper<SubPayToken> wrapper = new QueryWrapper<>();
        wrapper.eq(SubPayToken.TYPE, ContractType.ETH.getType());
        wrapper.eq(SubPayToken.DELETED, false);
        return this.baseService.getByCondition(SubPayToken.class, wrapper);
    }

    public SubPayToken getDefault(){
        QueryWrapper<SubPayToken> wrapper = new QueryWrapper<>();
        wrapper.eq(SubPayToken.IS_DEFAULT, true);
        wrapper.eq(SubPayToken.DELETED, false);
        return this.baseService.getByCondition(SubPayToken.class, wrapper);
    }

    public SubPayToken get(String address){
        QueryWrapper<SubPayToken> wrapper = new QueryWrapper<>();
        wrapper.eq(SubPayToken.ADDRESS, address)
                .eq(SubPayToken.DELETED, false);
        return this.baseService.getByCondition(SubPayToken.class, wrapper);
    }

    public Integer delete(String address){
        UpdateWrapper<SubPayToken> wrapper = new UpdateWrapper<>();
        wrapper.eq(SubPayToken.ADDRESS, address);
        return this.baseService.deleteByCondition(SubPayToken.class, wrapper);
    }

    public Integer update(SubPayToken payToken){
        return this.baseService.update(payToken);
    }

    public Integer save(SubPayToken payToken){
        return this.baseService.save(payToken);
    }
}
