package org.example.peermatch.model.dto.user;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.peermatch.model.domain.User;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author LinZeyuan
 * @description ES 包装类
 * @createDate 2025/11/28 16:22
 */
@Data
@Document(indexName = "user_v1")
public class UserEsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String profile;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 对象转包装类
     *
     * @param user
     * @return
     */
    public static UserEsDTO objToDto(User user) {
        if (user == null) return null;
        UserEsDTO userEsDTO = new UserEsDTO();
        BeanUtils.copyProperties(user, userEsDTO);
        String tagStr = user.getTags();
        if (StringUtils.isNotBlank(tagStr)) {
            userEsDTO.setTags(JSONUtil.toList(tagStr, String.class));
        }
        return userEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param userEsDTO
     * @return
     */
    public static User dtoToObj(UserEsDTO userEsDTO) {
        if (userEsDTO == null) return null;
        User user = new User();
        BeanUtils.copyProperties(userEsDTO, user);
        List<String> tagList = userEsDTO.getTags();
        if (CollectionUtils.isNotEmpty(tagList)) {
            user.setTags(JSONUtil.toJsonStr(tagList));
        }
        return user;
    }
}
