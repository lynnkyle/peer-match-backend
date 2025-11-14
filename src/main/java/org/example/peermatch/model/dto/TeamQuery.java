package org.example.peermatch.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.peermatch.common.PageRequest;

import java.util.Date;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/13 11:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     * 队伍id
     */
    @TableId(type = IdType.AUTO)
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
     * 队伍状态(0-公开, 1-私有, 2-加密)
     */
    private Integer teamStatus;
}
