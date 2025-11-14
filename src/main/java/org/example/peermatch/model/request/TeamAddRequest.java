package org.example.peermatch.model.request;

import lombok.Data;

import java.util.Date;

/**
 * @author LinZeyuan
 * @description 队伍创建请求
 * @createDate 2025/11/14 17:15
 */
@Data
public class TeamAddRequest {
    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 队伍过期时间
     */
    private Date expireTime;

    /**
     * 队伍状态(0-公开, 1-私有, 2-加密)
     */
    private Integer teamStatus;

    /**
     * 队伍密码
     */
    private String password;
}
