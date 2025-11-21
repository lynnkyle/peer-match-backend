package org.example.peermatch.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LinZeyuan
 * @description 队伍退出请求
 * @createDate 2025/11/21 9:28
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = -1170546269133898271L;
    /**
     * 队伍id
     */
    private Long teamId;
}
