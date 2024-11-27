package com.sublayer.api.config;

import com.sublayer.api.constants.Constants;
import com.sublayer.api.service.SubSystemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + "/var/www/upload/");
    }

    @Autowired
    private TokenInterceptor tokenInterceptor;
    public static final String[] excludeUrls = {
            "/swagger**/**",
            "/webjars/**",
            "/v3/**",
            "/doc.html",
            "/error",
            "/upload/**",
            "/user/login",
            "/category/list",
            "/user/login",
            "/user/reload",
            "/user/listbyaddr",
            "/user/follows",
            "/user/collections",
            "/user/nftlist",
            "/user/stat",
            "/user/info",
            "/user/onsales",
            "/user/like",
            "/user/created",
            "/user/white",
            "/user/listcontract",
            "/follow/match",
            "/follow/follows",
            "/follow/followers",
            "/category/list",
            "/contract/info",
            "/contract/getinfo",
            "/contract/stat",
            "/contract/list",
            "/contract/all",
            "/contract/listbyaddr",
            "/contract/onsales",
            "/contract/collections",
            "/contract/listitems",
            "/contract/listall",
            "/paytoken/list",
            "/home/list",
            "/home/indexlist",
            "/like/listuserlike",
            "/config/fetch",
            "/config/gasTracker",
            "/home/search",
            "/home/searchuser",
            "/nft/owners",
            "/nft/bids",
            "/nft/history",
            "/nft/detail",
            "/nft/activebids",
            "/nft/activesales",
            "/nft/getmedia",
            "/nft/getroyalties",
            "/order/get",
            "/notices/countunread",
            "/notices/list",
            "/notices/count",
            "/upload/*",
            "/static/*"};
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrls);
    }
}
