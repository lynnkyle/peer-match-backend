package org.example.peermatch.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LinZeyuan
 * @description 队伍加入请求
 * @createDate 2025/11/20 11:32
 */
@Data
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = -6721396282953346737L;
    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 队伍密码
     */
    private String password;
}
