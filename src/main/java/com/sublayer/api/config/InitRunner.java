package com.sublayer.api.config;

import com.alibaba.fastjson2.JSON;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.dto.ConfigDeploy;
import com.sublayer.api.domain.dto.ConfigNetwork;
import com.sublayer.api.service.StorageService;
import com.sublayer.api.service.SubSystemService;
import com.sublayer.api.storage.IpfsStorage;
import com.sublayer.api.utils.DappWeb3jUtil;
import com.sublayer.api.utils.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.sublayer.api.utils.DappCryptoUtil;

@Component
public class InitRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitRunner.class);

    @Autowired
    SubSystemService subSystemService;

    @Override
    public void run(String... args) throws Exception {
        initKey();
        initIpfs();
        initWeb3j();
    }

    private void initKey() {
        ConfigDeploy configDeploy = subSystemService.getConfigDeploy();
        if(null == configDeploy){
            return;
        }
        String mintKey = configDeploy.getMinerKey();
        String buyerFeeKey = configDeploy.getBuyerFeeKey();
        DappCryptoUtil.setMintKey(mintKey);
        DappCryptoUtil.setBuyerFeeKey(buyerFeeKey);
    }


    private void initIpfs() {
        String host = subSystemService.getKeyValue(Constants.IPFS_SERVER_IP);
        String port = subSystemService.getKeyValue(Constants.IPFS_SERVER_PORT);
        String remoteServer = subSystemService.getKeyValue(Constants.IPFS_REMOTE_SERVER);

        String staticPath = subSystemService.getKeyValue(Constants.STATIC_LOCAL_PATH);
        if(StringUtils.isEmpty(staticPath)){
            staticPath = "/";
        }
        staticPath = staticPath.endsWith("/") ? staticPath + "upload" : staticPath + "/upload";
        String uploadPath = "/upload";

        StorageService storageService = SpringContextUtil.getBean(StorageService.class);
        IpfsStorage storage = new IpfsStorage();
        storage.setHost(host);
        storage.setPort(StringUtils.isEmpty(port)? 0 : Integer.valueOf(port));
        storage.setLoclLocation(staticPath);
        storage.setRemoteService(remoteServer);
        storage.setRequestBase(uploadPath);
        storageService.setStorage(storage);
    }

    private void initWeb3j() {
        String value = subSystemService.getKeyValue(Constants.CONFIG_NETWORK);
        if(value.isEmpty()){
            return;
        }
        ConfigNetwork configNetwork = JSON.parseObject(value, ConfigNetwork.class);
        if(configNetwork.getRpc().isEmpty()){
            return;
        }

        DappWeb3jUtil.initWeb3j(configNetwork.getRpc());
    }
}