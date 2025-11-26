package org.example.peermatch.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.peermatch.common.PageRequest;

import java.util.List;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/13 11:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {
    private static final long serialVersionUID = -6438231938603876651L;
    /**
     * 队伍id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍id列表
     */
    private List<Long> idList;

    /**
     * 关键词(队伍名称、队伍描述)
     */
    private String searchText;

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
