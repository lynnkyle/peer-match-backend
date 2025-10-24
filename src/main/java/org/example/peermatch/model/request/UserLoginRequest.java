package org.example.peermatch.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LinZeyuan
 * @description 用户登录请求体
 * @createDate 2025/9/19 16:44
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -212603485375410132L;
    private String userAccount;
    private String userPassword;
}
