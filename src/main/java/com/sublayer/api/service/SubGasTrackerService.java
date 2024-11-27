package com.sublayer.api.service;

import com.alibaba.fastjson2.JSON;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.dto.GasTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class SubGasTrackerService {
    @Autowired
    SubSystemService subSystemService;

    public void getGasTracker(List<BigInteger> gasPriceList, BigInteger lastBlock) {
        if(gasPriceList.isEmpty()){
            return;
        }
        int length = gasPriceList.size();
        int lowIndex = length / 10;
        int highIndex = (length / 5) * 3;
        BigInteger low = gasPriceList.get(lowIndex);
        BigInteger max = gasPriceList.get(highIndex);
        BigInteger diff = max.subtract(low).divide(new BigInteger("4"));

        GasTracker gasTracker = new GasTracker(
                low,
                low.add(diff.multiply(new BigInteger("2"))),
                low.add(diff.multiply(new BigInteger("3"))),
                lastBlock
        );
        String value = JSON.toJSONString(gasTracker);
        subSystemService.save(Constants.GAS_TRACKER, value);
    }
}
