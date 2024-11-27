package com.sublayer.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.entity.BaseEntity;
import com.sublayer.api.entity.SubNftCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubNftCategoryService {

    @Autowired
    IBaseService baseService;

    public List<SubNftCategory> findAll(){
        QueryWrapper<SubNftCategory> wrapper = new QueryWrapper<>();
        wrapper.eq(BaseEntity.DELETED, false);
        return baseService.findByCondition(SubNftCategory.class, wrapper);
    }

    public SubNftCategory getDefault(){
        QueryWrapper<SubNftCategory> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc(SubNftCategory.ORDER);
        wrapper.last("limit 1");
        List<SubNftCategory> categoryList = this.baseService.findByCondition(SubNftCategory.class, wrapper);
        if(categoryList.isEmpty()){
            return null;
        }
        return categoryList.get(0);
    }
}
