package com.sublayer.api.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.domain.dto.SearchNftParamDto;
import com.sublayer.api.domain.vo.NftInfoVo;
import com.sublayer.api.entity.SubContractNft;
import com.sublayer.api.entity.SubUser;
import com.sublayer.api.entity.SubUserLog;
import com.sublayer.api.entity.SubUserToken;
import com.sublayer.api.service.*;
import com.sublayer.api.utils.DappCryptoUtil;
import com.sublayer.api.utils.IpUtil;
import com.sublayer.api.utils.JwtHelper;
import com.sublayer.api.utils.Str2ListUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.sublayer.api.constants.Constants.LOG_ACTION_LOGIN;
import static com.sublayer.api.constants.Constants.LOG_TYPE_AUTH;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller", description = "User operations")
public class SubUserController extends BaseController{

    @Autowired
    private SubSystemService subSystemService;

    @Autowired
    private SubUserService subUserService;

    @Autowired
    private SubUserLogService subUserLogService;

    @Autowired
    SubUserTokenService subUserTokenService;

    @Autowired
    SubNftItemsService subNftItemsService;

    @Autowired
    SubContractNftService subContractNftService;

    @Autowired
    SubContractService subContractService;

    @PostMapping("login")
    @Operation(summary="User login with signature")
    public Object login(String userAddress, String signature, String nonce, Long timestamp) {
        if (StringUtils.isEmpty(userAddress) || StringUtils.isEmpty(signature) || null == timestamp) {
            return R.fail("invalid login params");
        }

        if (System.currentTimeMillis() / 1000 - timestamp > 1800) {
            return R.fail("invalid message");
        }

        String loginMessage = getLoginMessage();
        if (StringUtils.isEmpty(loginMessage)) {
            return R.fail("LoginMessage is empty");
        }

        loginMessage = "sublayer.art wants you to sign in with your Ethereum account:\n"+userAddress+"\n\n" + loginMessage + "\n\n";
        loginMessage = loginMessage.concat("URI: https://sublayer.art\nVersion: 1\nChain ID: "+46+"\nNonce: " + nonce);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        loginMessage = loginMessage.concat("\nIssued At: " + sdf.format(new Date(timestamp)));

        logger.info(loginMessage);
        if (!DappCryptoUtil.validate(signature, loginMessage, userAddress)) {
            this.packUserLog(LOG_TYPE_AUTH, LOG_ACTION_LOGIN, false, "Invalid signature", "");
            return R.fail("invalid sign");
        }

        List<SubUser> userList = subUserService.queryUserByAddrAndType(userAddress);
        SubUser user = null;
        if (userList.size() > 1) {
            return R.fail("More than one user");
        } else if (userList.isEmpty()) {
            user = new SubUser();
            user.setAddress(userAddress);
            user.setLoginType("1");
            user.setIsWeb(true);
            user.setLastLoginTime(new Date());
            user.setLastLoginIp(IpUtil.getIpAddr(request));
            subUserService.add(user);
            this.packUserLog(LOG_TYPE_AUTH, LOG_ACTION_LOGIN, true, "User register", userAddress);
        } else {
            user = userList.get(0);
            user.setLastLoginTime(new Date());
            user.setLastLoginIp(IpUtil.getIpAddr(request));
            user.setIsWeb(true);
            user.setLoginType("1");
            if (subUserService.updateById(user) == 0) {
                this.packUserLog(LOG_TYPE_AUTH, LOG_ACTION_LOGIN, false, "User update fail", "");
                return R.fail("Update error");
            }
            this.packUserLog(LOG_TYPE_AUTH, LOG_ACTION_LOGIN, true, "User login", userAddress);
        }

        // token
        String token = SubUserTokenService.generateToken(user.getAddress());
        subUserTokenService.saveOrUpdate(user.getAddress(), token);
        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("user", user);
        return R.ok(result);
    }

    private String getLoginMessage() {
        return subSystemService.getKeyValue(Constants.LOGIN_MESSAGE);
    }

    private void packUserLog(Integer type, String action, Boolean succeed, String result, String address) {
        SubUserLog log = new SubUserLog();

        if (address != null && !"".equals(address)) {
            log.setAddress(address);
        } else {
            log.setAddress("Unknown user");
        }

        log.setIp(IpUtil.getIpAddr(request));
        log.setType(type);
        log.setAction(action);
        log.setStatus(succeed);
        log.setResult(result);
        subUserLogService.add(log);
    }

    /**
     * @return
     */
    @PostMapping("reload")
    @Operation(summary="User token reload", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object reload() {
        String token = request.getHeader(Constants.WEB_TOKEN_NAME);
        String userAddress = null;
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty((userAddress = JwtHelper.verifyTokenAndGetUserAddress(token)))) {
            return R.fail("");
        }

        SubUserToken userToken = subUserTokenService.getUserToken(userAddress);
        if(null == userToken || null == userToken.getUserToken() || !userToken.getUserToken().equals(token)) {
            return R.fail("");
        }

        SubUser user = subUserService.getUserByAddress(userAddress);

        Map<Object, Object> result = new HashMap<Object, Object>();

        result.put("user", user);
        this.packUserLog(LOG_TYPE_AUTH, LOG_ACTION_LOGIN, true, "Login error", user.getAddress());
        return R.ok(result);
    }

    /**
     * @return
     */
    @PostMapping("profile")
    @Operation(summary="User profile", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object profile() {
        String userAddress = (String) request.getAttribute("userAddress");
        if (StringUtils.isEmpty(userAddress)) {
            return R.fail("Not login");
        }
        SubUser user = subUserService.getUserByAddress(userAddress);
        if (null == user) {
            return R.fail("unauthorized");
        }

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("user", user);
        return R.ok(result);
    }

    /**
     * @param userinfo
     * @return
     */
    @PostMapping("setprofile")
    @Operation(summary="Set user profile", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object setprofile(SubUser userinfo) {
        String userAddress = (String) request.getAttribute("userAddress");
        if (StringUtils.isEmpty(userAddress)) {
            return R.fail("Not login");
        }
        SubUser user = subUserService.getUserByAddress(userinfo.getAddress());
        if (null == user) {
            return R.fail("Not login");
        }
        SubUser userByAddress = subUserService.getUserByAddress(userAddress);
        if (userByAddress.getId().longValue() != user.getId().longValue()) {
            return R.fail("bad argument");
        }
        userinfo.setId(user.getId());
        userinfo.setLoginType("1");
        userinfo.setDeleted(false);
        subUserService.updateUserinfo(userinfo);
        return R.ok();
    }

    /**
     * @param address
     * @return
     */
    @PostMapping("info")
    @Operation(summary="User profile query", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object info(String address) {
        if (address == null) {
            return R.ok(new JSONObject());
        }
        SubUser subUser = subUserService.getUserByAddress(address);
        if (subUser == null) {
            return R.ok();
        }
        return R.ok(subUser);
    }

    /**
     * @param address
     * @return
     */
    @PostMapping("stat")
    @Operation(summary="User total NFT query", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object stat(String address) {
        if (StringUtils.isEmpty(address)) {
            return R.ok();
        }
        SubUser fcUser = subUserService.getUserByAddress(address);
        if (fcUser == null) {
            return R.ok();
        }
        Map<String, Object> result = new HashMap<String, Object>(6);
        Long saleCount = subNftItemsService.countOnsale(address);
        Long collectionCount = subNftItemsService.countCollections(address);
        Integer createCount = subContractNftService.countCreatorNft(address);
        result.put("saleCount", saleCount);
        result.put("collectionCount", collectionCount);
        result.put("createCount", createCount);
        return R.ok(result);
    }

    @PostMapping("match")
    @Operation(summary="User profile match", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object match(String address) {
        if (null == address) {
            return R.ok(new ArrayList<>());
        }
        List<String> paramsList = Str2ListUtils.sliceString2StringArray(address);
        if (paramsList.size() == 0) {
            return R.ok(new ArrayList<>());
        }
        List<SubUser> subUsers = subUserService.findListByAddrs(paramsList);
        return R.ok(subUsers);
    }

    /**
     * @param address
     * @return
     */
    @PostMapping("onsales")
    @Operation(summary="User onsale NFT query", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object onsales(String address) {
        if (address == null) {
            return R.ok(this.getPageInfo());
        }
        IPage<NftInfoVo> iPage = subContractNftService.findOnSellListByAddress(address, this.getPageInfo());
        return R.ok(iPage);
    }

    /**
     * @param address
     * @return
     */
    @PostMapping("collections")
    @Operation(summary="User NFT collection query", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object collections(String address, String token, String tokenId) {
        if (address == null) {
            return R.ok(this.getPageInfo());
        }
        SearchNftParamDto paramDto = new SearchNftParamDto();
        paramDto.setOwner(address);
        paramDto.setAddress(token);
        paramDto.setTokenId(tokenId);
        IPage<NftInfoVo> iPage = subContractNftService.findListByUserAddress(paramDto, this.getPageInfo());
        return R.ok(iPage);
    }

    /**
     * @param paramDto
     * @return
     */
    @PostMapping("nftlist")
    @Operation(summary="User NFT query", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object nftlist(SearchNftParamDto paramDto) {
        if (StringUtils.isEmpty(paramDto.getAddress()) ||
                StringUtils.isEmpty(paramDto.getOwner())) {
            return R.ok(new ArrayList<>());
        }
        List<SubContractNft> nftList = subContractNftService.nftlist(paramDto);
        return R.ok(nftList);
    }


    /**
     * @param address
     * @return
     */
    @PostMapping("created")
    @Operation(summary="User creation query", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object created(String address) {
        if (null == address) {
            return R.ok(this.getPageInfo());
        }
        IPage<NftInfoVo> iPage = subContractNftService.findByCreators(address, this.getPageInfo());
        return R.ok(iPage);
    }

    /**
     * @param addrList
     * @return
     */
    @PostMapping("listbyaddr")
    @Operation(summary="User profile match by list", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object listByAddress(String[] addrList) {
        List<String> tempList = Arrays.asList(addrList);
        return R.ok(subUserService.findListByAddrs(tempList));
    }

    /**
     * @param address
     * @return
     */
    @PostMapping("listcontract")
    @Operation(summary="User contract query", security={@SecurityRequirement(name=Constants.WEB_TOKEN_NAME)})
    public Object listcontract(String address) {
        if (StringUtils.isEmpty(address)) {
            return R.ok(subContractService.findSystemContract());
        }
        SubUser user = subUserService.getUserByAddress(address);
        if (null == user) {
            return R.ok(subContractService.findSystemContract());
        }
        return R.ok(subContractService.findByUserAddress(user.getAddress()));
    }
}
