package org.example.peermatch.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LinZeyuan
 * @description 用户注册请求体
 * @createDate 2025/9/19 16:20
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -7730235126938061648L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String userCode;
}
