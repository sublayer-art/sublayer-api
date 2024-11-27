package com.sublayer.api.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.dto.ConfigContract;
import com.sublayer.api.domain.dto.ConfigDeploy;
import com.sublayer.api.domain.dto.ConfigNetwork;
import com.sublayer.api.domain.dto.GasTracker;
import com.sublayer.api.entity.BaseEntity;
import com.sublayer.api.entity.SubSystem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubSystemService {
    @Autowired
    private IBaseService baseService;

    public List<SubSystem> allShow(){
        QueryWrapper<SubSystem> wrapper = new QueryWrapper<>();
        wrapper.eq(SubSystem.SHOW, true)
                .eq(BaseEntity.DELETED, false);
        return this.baseService.findByCondition(SubSystem.class, wrapper);
    }

    public List<SubSystem> all(){
        QueryWrapper<SubSystem> wrapper = new QueryWrapper<>();
        wrapper.eq(SubSystem.DELETED, false);
        return this.baseService.findByCondition(SubSystem.class, wrapper);
    }

    public String getKeyValue(String key){
        SubSystem system = this.get(key);
        if (null != system) {
            return system.getKeyValue();
        }
        return null;
    }

    public ConfigNetwork getConfigNetwork(){
        String value = this.getKeyValue(Constants.CONFIG_NETWORK);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        ConfigNetwork configNetwork = JSON.parseObject(value, ConfigNetwork.class);
        return configNetwork;
    }

    public ConfigDeploy getConfigDeploy(){
        String value = this.getKeyValue(Constants.CONFIG_DEPLOY);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        ConfigDeploy configDeploy = JSON.parseObject(value, ConfigDeploy.class);
        return configDeploy;
    }

    public ConfigContract getConfigContract(){
        String value = this.getKeyValue(Constants.CONFIG_CONTRACT);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        ConfigContract configContract = JSON.parseObject(value, ConfigContract.class);
        return configContract;
    }

    public SubSystem get(String key){
        QueryWrapper<SubSystem> wrapper = new QueryWrapper<>();
        wrapper.eq(SubSystem.KEY_NAME, key).eq(BaseEntity.DELETED, false);
        return this.baseService.getByCondition(SubSystem.class, wrapper);
    }

    public Integer update(String key, String value){
        SubSystem system = this.get(key);
        if(null == system){
            return 0;
        }
        value = this.valueToLowerCase(key, value);
        system.setKeyValue(value);
        return this.update(system);
    }

    private String valueToLowerCase(String key, String value){
        return value;
    }

    public Integer update(SubSystem system){
        return this.baseService.update(system);
    }

    public Integer save(String key, String value){
        SubSystem system = this.get(key);
        value = this.valueToLowerCase(key, value);
        if(null == system){
            system = new SubSystem();
            system.setKeyName(key);
            system.setKeyValue(value);
            return this.save(system);
        }
        system.setKeyValue(value);
        return this.update(system);
    }

    public Integer save(SubSystem system) {
        return this.baseService.save(system);
    }

    public GasTracker gasTracker(){
        String value = getKeyValue(Constants.GAS_TRACKER);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        GasTracker gasTracker = JSON.parseObject(value, GasTracker.class);
        return gasTracker;
    }
}
