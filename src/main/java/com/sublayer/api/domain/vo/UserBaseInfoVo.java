package com.sublayer.api.domain.vo;

import com.sublayer.api.entity.SubUser;

public class UserBaseInfoVo {
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public UserBaseInfoVo(SubUser user){
        if(null != user) {
            this.address = user.getAddress();
        }
    }

    public UserBaseInfoVo(String address){
        this.address = address;
    }

    public UserBaseInfoVo(String address, SubUser user){
        this.address = address;
    }
}
