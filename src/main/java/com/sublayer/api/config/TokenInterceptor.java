package com.sublayer.api.config;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.entity.SubUserToken;
import com.sublayer.api.service.IBaseService;
import com.sublayer.api.utils.JwtHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IBaseService baseService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(Constants.WEB_TOKEN_NAME);
        logger.info("token：{}", token);
        String userAddress = null;

        if (StringUtils.isEmpty(token) || StringUtils.isEmpty((userAddress = JwtHelper.verifyTokenAndGetUserAddress(token)))) {
            unLogin(request, response);
            return false;
        }
        QueryWrapper<SubUserToken> wrapper = new QueryWrapper<>();
        wrapper.eq(SubUserToken.USER_ADDRESS, userAddress);
        SubUserToken userToken = baseService.getByCondition(SubUserToken.class, wrapper);
        if (null == userToken || null == userToken.getUserToken() || !userToken.getUserToken().equals(token)) {
            unLogin(request, response);
            return false;
        }
        request.setAttribute("userAddress", userAddress);
        return true;
    }

    private void unLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(R.fail(501, "Not login")));
    }
}
