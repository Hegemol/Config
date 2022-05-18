package org.hegemol.config.common.model;

/**
 * 请求返回结果包装
 *
 * @author KevinClair
 **/
public class Result<T> {

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;


    public Result() {
    }

    public Result(final int code, final String message, final T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回
     *
     * @param data 返回数据
     * @return Result
     */
    public static Result success(Object data) {
        return new Result(200, "success", data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }
}
