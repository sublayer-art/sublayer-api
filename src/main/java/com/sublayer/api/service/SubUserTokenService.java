package com.sublayer.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sublayer.api.entity.SubUserToken;
import com.sublayer.api.utils.JwtHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubUserTokenService {

    @Autowired
    IBaseService baseService;
    public SubUserToken getUserToken(String address){
        QueryWrapper<SubUserToken> wrapper = new QueryWrapper<>();
        wrapper.eq(SubUserToken.USER_ADDRESS, address).eq(SubUserToken.DELETED, false);
        return this.baseService.getByCondition(SubUserToken.class, wrapper);
    }

    public String getToken(String address) {
        QueryWrapper<SubUserToken> wrapper = new QueryWrapper<>();
        wrapper.eq(SubUserToken.USER_ADDRESS, address).eq(SubUserToken.DELETED, false);
        SubUserToken userToken = this.baseService.getByCondition(SubUserToken.class, wrapper);
        if (null == userToken){
            return null;
        }
        return userToken.getUserToken();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void saveOrUpdate(String address, String token) {
        SubUserToken userToken = new SubUserToken();
        userToken.setUserAddress(address);
        userToken.setUserToken(token);
        String sqlToken = getToken(address);
        if (StringUtils.isEmpty(sqlToken)){
            this.baseService.save(userToken);
        }else {
            UpdateWrapper<SubUserToken> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(SubUserToken.USER_ADDRESS, address).eq(SubUserToken.DELETED, false);
            updateWrapper.set(SubUserToken.USER_TOKEN, token);
            this.baseService.updateByCondition(SubUserToken.class, updateWrapper);
        }
    }

    public static String generateToken( String address) {
        return JwtHelper.createToken(address);
    }
}
