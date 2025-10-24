package org.example.peermatch.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 标签表
 * @TableName tag
 */
@TableName(value ="tag")
@Data
public class Tag {
    /**
     * 标签id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 父标签id
     */
    private Long parentId;

    /**
     * 是否为父标签(0-否,1-是)
     */
    private Integer isParent;

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