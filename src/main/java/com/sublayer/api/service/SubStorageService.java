package com.sublayer.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.entity.SubStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubStorageService {
    @Autowired
    private IBaseService baseService;

    public void add(SubStorage storageInfo) {
        this.baseService.save(storageInfo);
    }

    public SubStorage findByKey(String key) {
        QueryWrapper<SubStorage> wrapper = new QueryWrapper<>();
        wrapper.eq(SubStorage.KEY, key);
        return this.baseService.getByCondition(SubStorage.class, wrapper);
    }

    public int update(SubStorage storageInfo) {
        return this.baseService.update(storageInfo);
    }

    public SubStorage findById(Long id) {
        return this.baseService.getById(SubStorage.class, id);
    }
}
