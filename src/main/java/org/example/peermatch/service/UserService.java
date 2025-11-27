package org.example.peermatch.service;

import org.example.peermatch.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.peermatch.model.vo.UserVO;

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
    UserVO doLogin(String userAccount, String userPassword, HttpServletRequest req);

    /**
     * 用户更新
     *
     * @param userVO
     * @param loginUserVO
     * @return
     */
    boolean updateUser(User userVO, UserVO loginUserVO);

    /**
     * 用户脱敏
     *
     * @param userVOFromDb
     * @return
     */
    UserVO getSafetyUser(User userVOFromDb);

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
    List<UserVO> searchUsersByTags(List<String> tagNameList);

    /**
     * 匹配用户
     *
     * @param num
     * @param loginUserVO
     * @return
     */
    List<UserVO> matchUser(long num, UserVO loginUserVO);

    /**
     * 判断用户是否为管理员
     *
     * @param req
     * @return
     */
    boolean isAdmin(HttpServletRequest req);

    /**
     * 判断用户是否为管理员
     *
     * @param loginUserVO
     * @return
     */
    boolean isAdmin(UserVO loginUserVO);

    /**
     * 获取当前用户登录信息
     *
     * @param req
     * @return
     */
    UserVO getLoginUser(HttpServletRequest req);
}
