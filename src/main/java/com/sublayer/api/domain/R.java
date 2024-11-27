package com.sublayer.api.domain;

import com.sublayer.api.constants.Constants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class R<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int SUCCESS = 0;

    /** 失败 */
    public static final int FAIL = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR;

    private int code;

    private String msg;

    private T data;

    public static <T> R<T> ok()
    {
        return restResult(null,  Constants.HTTP_STATUS_SUCCESS, "Operation success");
    }

    public static <T> R<T> ok(int code, T data)
    {
        return restResult(data, code, null);
    }

    public static <T> R<T> ok(T data)
    {
        return restResult(data,  Constants.HTTP_STATUS_SUCCESS, null);
    }

    public static <T> R<T> ok(T data, String msg)
    {
        return restResult(data,  Constants.HTTP_STATUS_SUCCESS, msg);
    }

    public static <T> R<T> fail()
    {
        return restResult(null, FAIL, "Operation failed");
    }

    public static <T> R<T> fail(String msg)
    {
        return restResult(null, FAIL, msg);
    }

    public static <T> R<T> fail(T data)
    {
        return restResult(data, FAIL, null);
    }

    public static <T> R<T> fail(T data, String msg)
    {
        return restResult(data, FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg)
    {
        return restResult(null, code, msg);
    }

    public static <T> R<T> fail(T data, int code, String msg)
    {
        return restResult(data, code, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg)
    {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == SUCCESS || code == Constants.HTTP_STATUS_SUCCESS;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("code", code)
                .append("msg", msg)
                .append("data", data)
                .toString();
    }
}
