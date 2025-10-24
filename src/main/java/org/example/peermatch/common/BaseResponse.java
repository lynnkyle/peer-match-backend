package org.example.peermatch.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LinZeyuan
 * @description 通用返回对象
 * @createDate 2025/10/10 10:46
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private String message;
    private T data;
    private String description;

    public BaseResponse() {
    }

    /**
     * 成功响应
     *
     * @param code
     * @param message
     * @param data
     * @param description
     */
    public BaseResponse(int code, String message, T data, String description) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.description = description;
    }

    /**
     * 失败响应
     *
     * @param code
     * @param message
     * @param description
     */
    public BaseResponse(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage(), null, errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode, String description) {
        this(errorCode.getCode(), errorCode.getMessage(), null, description);
    }
}
