package org.example.peermatch.service;

import org.example.peermatch.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author LinZeyuan
 * @description 用户服务
 * @createDate 2025-09-18 17:35:27
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param userCode      校验编码
     * @return 用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userCode);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @return 用户信息(脱敏)
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param userFromDb
     * @return
     */
    User getSafetyUser(User userFromDb);

    /**
     * 用户注销
     *
     * @param req
     * @return
     */
    void userLogout(HttpServletRequest req);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);
}
