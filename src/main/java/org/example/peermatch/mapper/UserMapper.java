package org.example.peermatch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.peermatch.model.domain.User;

import java.util.Date;
import java.util.List;


/**
 * @author LinZeyuan
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2025-09-18 17:35:27
 * @Entity org.example.usercenter.model.domain.User
 */
public interface UserMapper extends BaseMapper<User> {
    /*
        查询用户列表(包括已被删除的数据)
     */
    List<User> listUserWithDelete(Date minUpdateTime);
}
