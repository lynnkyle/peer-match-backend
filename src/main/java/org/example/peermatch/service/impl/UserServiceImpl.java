package org.example.peermatch.service.impl;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.example.peermatch.common.ErrorCode;
import org.example.peermatch.constant.UserConstant;
import org.example.peermatch.exception.BusinessException;
import org.example.peermatch.mapper.UserMapper;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.vo.UserVO;
import org.example.peermatch.service.UserService;
import org.example.peermatch.utils.AlgorithmUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author LinZeyuan
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-09-18 17:35:27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 加密盐值
    private static final String SALT = "MD5";

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param userCode      校验编码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String userCode) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.校验用户账号
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        String validPattern = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号格式不正确");
        }
        // 2.校验用户密码
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 3.校验校验编码
        if (userCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验编码过长");
        }
        // 校验用户账号-重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        long count = userMapper.selectCount(queryWrapper.eq("user_account", userAccount));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号重复");
        }
        // 校验校验编码-重复
        queryWrapper = new QueryWrapper<User>();
        count = userMapper.selectCount(queryWrapper.eq("code", userCode));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验编码重复");
        }
        // 4.密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 5.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setCode(userCode);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库User插入失败");
        }
        // 5.返回用户ID
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param req
     * @return
     */
    @Override
    public UserVO doLogin(String userAccount, String userPassword, HttpServletRequest req) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 校验用户账号
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        String validPattern = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号格式不正确");
        }
        // 2. 校验用户密码
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 3. 校验密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User userVOFromDb = userMapper.selectOne(queryWrapper);
        if (userVOFromDb == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号或密码错误");
        }
        // 5. 返回用户信息(脱敏)
        UserVO safetyUserVO = getSafetyUser(userVOFromDb);
        // 4. 保存用户登录态
        HttpSession session = req.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, safetyUserVO);
        return safetyUserVO;
    }

    /**
     * 用户更新
     *
     * @param userVO
     * @param loginUserVO
     * @return
     */
    @Override
    public boolean updateUser(User userVO, UserVO loginUserVO) {
        long userId = userVO.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 补充校验, 用户没有任何更新的值, 直接抛出异常
        // 管理员, 允许更新任意用户; 不是管理员，只允许更新自己信息
        if (!isAdmin(loginUserVO) && userId != loginUserVO.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUserVO = userMapper.selectById(userId);
        if (oldUserVO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户不存在");
        }
        return userMapper.updateById(userVO) > 0;
    }

    /*
        用户脱敏
    */
    public UserVO getSafetyUser(User userVOFromDb) {
        UserVO safetyUserVO = new UserVO();
        safetyUserVO.setId(userVOFromDb.getId());
        safetyUserVO.setUserName(userVOFromDb.getUserName());
        safetyUserVO.setUserAccount(userVOFromDb.getUserAccount());
        safetyUserVO.setAvatarUrl(userVOFromDb.getAvatarUrl());
        safetyUserVO.setGender(userVOFromDb.getGender());
        safetyUserVO.setPhone(userVOFromDb.getPhone());
        safetyUserVO.setEmail(userVOFromDb.getEmail());
        safetyUserVO.setUserRole(userVOFromDb.getUserRole());
        safetyUserVO.setUserStatus(userVOFromDb.getUserStatus());
        safetyUserVO.setCode(userVOFromDb.getCode());
        safetyUserVO.setProfile(userVOFromDb.getProfile());
        safetyUserVO.setTags(userVOFromDb.getTags());
        safetyUserVO.setCreateTime(userVOFromDb.getCreateTime());
        safetyUserVO.setUpdateTime(userVOFromDb.getUpdateTime());
        return safetyUserVO;
    }

    /**
     * 用户注销
     *
     * @param req
     */
    @Override
    public void userLogout(HttpServletRequest req) {
        req.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    }

    /**
     * 根据标签搜索用户(内存)
     *
     * @param tagNameList
     * @return
     */
    @Override
    public List<UserVO> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper queryWrapper = new QueryWrapper<>();
        List<User> userVOListFromDb = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        return userVOListFromDb.stream().filter(user -> {
            Set<String> tagNameSet = gson.fromJson(user.getTags(), new TypeToken<Set<String>>() {
            }.getType());
            tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public List<UserVO> matchUser(long num, UserVO loginUserVO) {
        Gson gson = new Gson();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUserVO.getTags();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        List<Pair<User, Long>> list = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User userVO = userList.get(i);
            String userTags = userVO.getTags();
            if (StringUtils.isBlank(userTags) || Objects.equals(userVO.getId(), loginUserVO.getId())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(userVO, distance));
        }
        List<Pair<User, Long>> topUserList = list.stream().sorted((x, y) -> (int) (x.getValue() - y.getValue())).limit(num).collect(Collectors.toList());
        List<Long> userIdList = topUserList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        System.out.println(topUserList);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        Map<Long, List<UserVO>> unOrderUserVOList = this.list(userQueryWrapper).stream().map(this::getSafetyUser).collect(Collectors.groupingBy(UserVO::getId));
        List<UserVO> userVOVOList = userIdList.stream().map(id -> unOrderUserVOList.get(id).get(0)).collect(Collectors.toList());
        return userVOVOList;
    }

    /**
     * 根据标签搜索用户(SQL)
     *
     * @param tagNameList
     * @return
     */
    @Deprecated
    private List<UserVO> searchUsersByTagsFromSQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userVOList = userMapper.selectList(queryWrapper);
        return userVOList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 判断用户是否为管理员
     *
     * @param req
     * @return
     */
    public boolean isAdmin(HttpServletRequest req) {
        // 用户鉴权
        UserVO userVO = (UserVO) req.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return userVO != null && userVO.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    public boolean isAdmin(UserVO loginUserVO) {
        // 用户鉴权
        return loginUserVO != null && loginUserVO.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 获取当前用户登录信息
     *
     * @param req
     * @return
     */
    public UserVO getLoginUser(HttpServletRequest req) {
        if (req == null) {
            return null;
        }
        UserVO userVO = (UserVO) req.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userVO == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return userVO;
    }

}




