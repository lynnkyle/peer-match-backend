package org.example.peermatch.common;

/**
 * @author LinZeyuan
 * @description 通用返回工具类
 * @createDate 2025/10/10 10:57
 */
public class ResultUtils {
    /**
     * 成功
     *
     * @param data
     * @param description
     * @return
     */
    public static <T> BaseResponse<T> success(T data, String description) {
        return new BaseResponse<T>(20000, "ok", data, description);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @param description
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> error(int code, String message, String description) {
        return new BaseResponse<T>(code, message, description);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<T>(errorCode);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @param description
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String description) {
        return new BaseResponse<T>(errorCode, description);
    }
}
