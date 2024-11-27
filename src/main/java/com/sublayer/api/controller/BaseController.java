package com.sublayer.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sublayer.api.constants.Constants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController {
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    protected HttpServletRequest request;

    private Long getPageNum() {
        String page = request.getParameter(Constants.PAGE);
        if(null == page) {
            return 1L;
        } else {
            return Long.parseLong(page);
        }
    }

    private Long getPageSize() {
        String limit = request.getParameter(Constants.LIMIT);
        if(null == limit) {
            return 10L;
        } else {
            return Long.parseLong(limit);
        }
    }

    protected <T> Page<T> getPageInfo() {
        Page<T> page = new Page<>(this.getPageNum(), this.getPageSize());
        return page;
    }
}
