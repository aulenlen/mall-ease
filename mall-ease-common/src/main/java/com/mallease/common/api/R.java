package com.mallease.common.api;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-07 18:47
 **/
public class R<T> {
    private Integer code;
    private String message;
    private T data;

    protected R() {
    }

    protected R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回成功结果
     *
     * @param data 获取数据
     * @param <T>
     * @return
     */
    public static <T> R<T> success(T data) {
        return new R<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * @param data    获取数据
     * @param message 提示信息
     * @param <T>
     * @return
     */
    public static <T> R<T> success(T data, String message) {
        return new R<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 返回失败结果
     *
     * @param errorCode 错误码
     * @param <T>
     * @return
     */
    public static <T> R<T> failed(IErrorCode errorCode) {
        return new R<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 返回失败结果
     *
     * @param errorCode 错误码
     * @param message   提示信息
     * @param <T>
     * @return
     */
    public static <T> R<T> failed(IErrorCode errorCode, String message) {
        return new R<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 返回失败结果
     *
     * @param message 提示信息
     * @param <T>
     * @return
     */
    public static <T> R<T> failed(String message) {
        return new R<T>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 返回失败结果
     *
     * @param <T>
     * @return
     */
    public static <T> R<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> R<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> R<T> validateFailed(String message) {
        return new R<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> R<T> unauthorized(T data) {
        return new R<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> R<T> forbidden(T data) {
        return new R<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}
