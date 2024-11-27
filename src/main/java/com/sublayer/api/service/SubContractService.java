package com.sublayer.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sublayer.api.domain.R;
import com.sublayer.api.domain.dto.ConfigDeploy;
import com.sublayer.api.domain.dto.VSRSignInfo;
import com.sublayer.api.domain.vo.SubContractVo;
import com.sublayer.api.entity.BaseEntity;
import com.sublayer.api.entity.SubContract;
import com.sublayer.api.mapper.SubContractExtMapper;
import com.sublayer.api.utils.DappCryptoUtil;
import com.sublayer.api.utils.DappWeb3jUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SubContractService {

    @Autowired
    IBaseService baseService;

    @Autowired
    private SubSystemService subSystemService;

    @Autowired
    SubContractExtMapper contractExtMapper;

    public SubContract getByAddress(String address) {
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContract.ADDRESS, address).eq(BaseEntity.DELETED, false);
        return this.baseService.getByCondition(SubContract.class, wrapper);
    }

    public SubContract getInfo(String address) {
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContract.ADDRESS, address).eq(BaseEntity.DELETED, false);
        SubContract contract = this.baseService.getByCondition(SubContract.class, wrapper);
        if(null != contract && (StringUtils.isEmpty(contract.getSymbol()) || StringUtils.isEmpty(contract.getName())) && contract.getGetInfoTimes() < 13) {
            UpdateWrapper<SubContract> uwrapper = new UpdateWrapper<>();
            if(StringUtils.isEmpty(contract.getSymbol())) {
                String symbol = DappWeb3jUtil.getSymbol(contract.getAddress());
                contract.setSymbol(symbol);
                uwrapper.set(SubContract.SYMBOL, symbol);
            }
            if(StringUtils.isEmpty(contract.getName())) {
                String name = DappWeb3jUtil.getName(contract.getAddress());
                contract.setName(name);
                uwrapper.set(SubContract.NAME, name);
            }
            uwrapper.setSql(SubContract.GET_INFO_TIMES + " = " + SubContract.GET_INFO_TIMES + " + 1");
            uwrapper.eq(BaseEntity.ID, contract.getId());
            this.baseService.updateByCondition(SubContract.class, uwrapper);
        }
        return contract;
    }

    public List<SubContract> findAll() {
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(BaseEntity.DELETED, false).eq(SubContract.IS_ADMIN, true);
        return this.baseService.findByCondition(SubContract.class, wrapper);
    }

    public Integer save(SubContract contract) {
        return this.baseService.save(contract);
    }

    public List<SubContract> findByUserAddress(String address) {
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContract.OWNER, address).eq(BaseEntity.DELETED, false);
        wrapper.or(i -> i.eq(SubContract.IS_ADMIN, true).eq(BaseEntity.DELETED, false));
        return this.baseService.findByCondition(SubContract.class, wrapper);
    }

    public List<SubContract> findSystemContract() {
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContract.IS_ADMIN, true).eq(BaseEntity.DELETED, false);
        return this.baseService.findByCondition(SubContract.class, wrapper);
    }

    public SubContract findById(Long id) {
        return this.baseService.getById(SubContract.class, id);
    }

    public  List<SubContract> findBySymbol(String symbol){
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContract.SYMBOL, symbol).eq(BaseEntity.DELETED, false);
        return this.baseService.findByCondition(SubContract.class, wrapper);
    }


    public R<VSRSignInfo> MinerTokenId(String address){
        String tokenId = this.getMinerTokenId(address);
        if(null == tokenId){
            return R.fail("tokenId is null");
        }
        ConfigDeploy configDeploy = subSystemService.getConfigDeploy();
        String minerKey = configDeploy.getMinerKey();
        VSRSignInfo info = DappCryptoUtil.minerSign(address, tokenId, minerKey);
        if(null == info){
            return R.fail("sign TokenId fail");
        }
        info.setTokenId(tokenId);
        return R.ok(info);
    }

    private String getMinerTokenId(String address) {
        String tokenId = contractExtMapper.getContractWithLock(address);
        if(null == tokenId) {
            tokenId = "1";
        } else {
            tokenId = new BigInteger(tokenId).add(BigInteger.ONE).toString();
        }
        contractExtMapper.updateLastTokenId(tokenId, address);
        return tokenId;
    }

    public List<String> findAllAddress() {
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(BaseEntity.DELETED, false);
        List<SubContract> list = baseService.findByCondition(SubContract.class, wrapper);
        return list.stream().map(contract->contract.getAddress()).collect(Collectors.toList());
    }


    /**
     * @param addrList
     * @return
     */
    public List<SubContract> findListByAdress(List<String> addrList) {
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.in(SubContract.ADDRESS, addrList).eq(BaseEntity.DELETED, false);
        return this.baseService.findByCondition(SubContract.class, wrapper);
    }



    public IPage<SubContract> listAll(IPage<SubContract> pageInfo) {
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(BaseEntity.DELETED, false).isNotNull(SubContract.ADDRESS);
        return this.baseService.findByPage(SubContract.class, wrapper, pageInfo);
    }

    public SubContract setAdminContract(String name, String symbol, String address, Integer type){
        SubContract contract = this.getAdminContract();
        if(null != contract){
            return contract;
        }
        contract = new SubContract();
        contract.setName(name);
        contract.setSymbol(symbol);
        contract.setAddress(address.toLowerCase(Locale.ROOT));
        contract.setIsSync(true);
        contract.setIsAdmin(true);
        contract.setVerify(true);
        this.save(contract);
        return contract;
    }

    public SubContract getAdminContract(){
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContract.IS_SYNC, true)
                .eq(SubContract.IS_ADMIN, true)
                .eq(SubContract.DELETED, false);
        return this.baseService.getByCondition(SubContract.class, wrapper);
    }

    public SubContract get(String address){
        QueryWrapper<SubContract> wrapper = new QueryWrapper<>();
        wrapper.eq(SubContract.ADDRESS, address)
                .eq(SubContract.DELETED, false);
        return this.baseService.getByCondition(SubContract.class, wrapper);
    }

    public Integer add(String address){
        SubContract contract = new SubContract();
        contract.setAddress(address);
        contract.setIsSync(true);
        return save(contract);
    }

    public Integer update(SubContract contract){
        return baseService.update(contract);
    }

}
