package org.example.peermatch.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户-队伍表
 *
 * @TableName user_team
 */
@TableName(value = "user_team")
@Data
public class UserTeam implements Serializable {
    /**
     * 用户-队伍id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 创建时间
     */
    private Date joinTime;

    /**
     * 逻辑删除(1-删除)
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}