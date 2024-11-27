package com.sublayer.api.service;

import com.sublayer.api.entity.SubUserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class SubUserLogService {
    @Autowired
    private IBaseService baseService;

    @Transactional(rollbackFor = Exception.class)
    public void add(SubUserLog userLog){
        userLog.setCreateTime(new Date());
        userLog.setUpdateTime(new Date());
        userLog.setDeleted(false);
        baseService.save(userLog);
    }
}
