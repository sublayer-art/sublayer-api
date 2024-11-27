package com.sublayer.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sublayer.api.entity.BaseEntity;
import com.sublayer.api.entity.SubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubUserService {

    @Autowired
    IBaseService baseService;

    public Integer add(SubUser user) {
        return baseService.save(user);
    }

    public Integer updateById(SubUser user) {
        return this.baseService.update(user);
    }

    public List<SubUser> queryUserByAddrAndType(String address) {
        QueryWrapper<SubUser> wrapper = new QueryWrapper<>();
        wrapper.eq(SubUser.ADDRESS, address);
        return this.baseService.findByCondition(SubUser.class, wrapper);
    }

    public SubUser getUserByAddress(String address) {
        QueryWrapper<SubUser> wrapper = new QueryWrapper<>();
        wrapper.eq(SubUser.ADDRESS, address);
        return this.baseService.getByCondition(SubUser.class, wrapper);
    }

    public List<SubUser> listByMulti(List<String> addresList){
        if (addresList.size() == 0){
            return new ArrayList<SubUser>();
        }
        QueryWrapper<SubUser> wrapper = new QueryWrapper<>();
        wrapper.in(SubUser.ADDRESS, addresList)
                .eq(SubUser.DELETED, false);
        return this.baseService.findByCondition(SubUser.class, wrapper);
    }

    public SubUser get(String address){

        QueryWrapper<SubUser> wrapper = new QueryWrapper<>();
        wrapper.eq(SubUser.ADDRESS, address)
                .eq(SubUser.DELETED, false);
        return this.baseService.getByCondition(SubUser.class, wrapper);
    }

    public Object updateUserinfo(SubUser userInfo) {
        return this.baseService.update(userInfo);
    }

    public List<SubUser> findListByAddrs(List<String> addrs){
        QueryWrapper<SubUser> wrapper = new QueryWrapper<>();
        wrapper.select(SubUser.ADDRESS)
                .in(SubUser.ADDRESS, addrs);
        return this.baseService.findByCondition(SubUser.class, wrapper);
    }
}
