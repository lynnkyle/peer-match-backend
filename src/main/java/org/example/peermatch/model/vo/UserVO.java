package org.example.peermatch.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息封装类(脱敏)
 *
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/13 11:07
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户性别
     */
    private Integer gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户角色(0-普通用户, 1-管理员)
     */
    private Integer userRole;

    /**
     * 用户状态(0-正常)
     */
    private Integer userStatus;

    /**
     * 用户校验编号
     */
    private String code;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
