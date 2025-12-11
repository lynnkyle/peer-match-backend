package org.example.peermatch.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍和用户信息封装类(脱敏)
 *
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/13 11:07
 */
@Data
public class TeamUserVO implements Serializable {
    private static final long serialVersionUID = -7950606533773751672L;

    /**
     * 队伍id
     */
    private Long id;

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
     * 用户列表
     */
    private UserVO createUser;

    /**
     * 加入用户信息
     */
    private List<UserVO> members;

    /**
     * 是否已加入队伍
     */
    private Boolean hasJoin;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
