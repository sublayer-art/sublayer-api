package com.sublayer.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.domain.vo.EventValuesExt;
import com.sublayer.api.domain.vo.TxOrderInfo;
import com.sublayer.api.entity.SubTxOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SubTxOrderService {
    @Autowired
    IBaseService baseService;

    public Integer saveBatch(List<EventValuesExt> list){
        if(list.isEmpty()){
            return 0;
        }
        List<SubTxOrder> txOrderList = new ArrayList<>();
        SubTxOrder txOrder = null;
        for(EventValuesExt valuesExt: list){
            txOrder = new SubTxOrder();
            txOrder.setTxHash(valuesExt.getTxHash());
            Integer blockNumber = Integer.valueOf(valuesExt.getBlockNumber().toString());
            txOrder.setBlockNumber(blockNumber);
            Integer time = Integer.valueOf(valuesExt.getBlockTimestamp().toString());
            txOrder.setBlockTimestamp(time);
            // txOrder.setIsSync(true);

            txOrder.setUpdateTime(new Date());
            txOrder.setCreateTime(new Date());
            txOrderList.add(txOrder);
        }
        if(null == txOrderList || txOrderList.size()==0){
            return null;
        }
        //对txOrderList根据TxHash去重
        List<SubTxOrder> collect = txOrderList.stream().filter(distinctByKey(SubTxOrder::getTxHash)).collect(Collectors.toList());
        return this.baseService.saveBatch(collect);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public TxOrderInfo info(){
        TxOrderInfo orderInfo = new TxOrderInfo();
        SubTxOrder txOrder = this.lastOne(true);
        if(null != txOrder){
            orderInfo.setEarlyBlockNumber(txOrder.getBlockNumber());
            txOrder = this.lastOne(false);
            orderInfo.setLastBlockNumber(txOrder.getBlockNumber());
        }
        Long total = this.total();
        orderInfo.setTxAmount(total);
        return orderInfo;
    }

    public SubTxOrder lastOne(Boolean asc){
        QueryWrapper<SubTxOrder> wrapper = new QueryWrapper<>();
        wrapper.last("limit 1");
        if(!asc){
            wrapper.orderByDesc(SubTxOrder.BLOCK_NUMBER);
        }else{
            wrapper.orderByAsc(SubTxOrder.BLOCK_NUMBER);
        }
        List<SubTxOrder>  txOrderList = this.baseService.findByCondition(SubTxOrder.class, wrapper);
        if(txOrderList.isEmpty()){
            return null;
        }
        return txOrderList.get(0);
    }

    public Long total(){
        QueryWrapper<SubTxOrder> wrapper = new QueryWrapper<>();
        return baseService.counts(SubTxOrder.class, wrapper);
    }

    public List<SubTxOrder> getList(Integer start, Integer end){
        QueryWrapper<SubTxOrder> wrapper = new QueryWrapper<>();
        wrapper.ge(SubTxOrder.BLOCK_NUMBER, start)
                .le(SubTxOrder.BLOCK_NUMBER, end);
        return this.baseService.findByCondition(SubTxOrder.class, wrapper);
    }
}
