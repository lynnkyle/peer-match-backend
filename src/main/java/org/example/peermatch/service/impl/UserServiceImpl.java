package org.example.peermatch.service.impl;

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
import org.example.peermatch.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author LinZeyuan
 * @description 用户服务实现类
 * @createDate 2025-09-18 17:35:27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 加密盐值
    private static final String SALT = "MD5";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String userCode) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
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
        long count = userMapper.selectCount(queryWrapper.eq("account", userAccount));
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
        boolean result = save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库保存失败");
        }
        // 5.返回用户ID
        return user.getId();
    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
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
        queryWrapper.eq("account", userAccount);
        queryWrapper.eq("password", encryptPassword);
        User userFromDb = userMapper.selectOne(queryWrapper);
        if (userFromDb == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号或密码错误");
        }
        // 5. 返回用户信息(脱敏)
        User safetydUser = getSafetyUser(userFromDb);
        // 4. 保存用户登录态
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, safetydUser);
        return safetydUser;
    }

    /*
        用户脱敏
     */
    public User getSafetyUser(User userFromDb) {
        User safetydUser = new User();
        safetydUser.setId(userFromDb.getId());
        safetydUser.setUserName(userFromDb.getUserName());
        safetydUser.setUserAccount(userFromDb.getUserAccount());
        safetydUser.setAvatarUrl(userFromDb.getAvatarUrl());
        safetydUser.setGender(userFromDb.getGender());
        safetydUser.setEmail(userFromDb.getEmail());
        safetydUser.setPhone(userFromDb.getPhone());
        safetydUser.setUserRole(userFromDb.getUserRole());
        safetydUser.setUserStatus(userFromDb.getUserStatus());
        safetydUser.setCode(userFromDb.getCode());
        safetydUser.setCreateTime(userFromDb.getCreateTime());
        return safetydUser;
    }

    @Override
    public void userLogout(HttpServletRequest req) {
        req.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        for (String tagName : tagNameList) {
//            queryWrapper = queryWrapper.like("tags", tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);

        QueryWrapper queryWrapper = new QueryWrapper<>();
        List<User> userListFromDb = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        userListFromDb.stream().filter(user -> {
            Set<String> tagNameSet = gson.fromJson(user.getTags(), new TypeToken<Set<String>>() {
            }.getType());
            for (String tagName : tagNameList) {
                if (!tagNameSet.contains(tagName)) {

                }
            }
            return true;
        }).collect(Collectors.toList());
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
}




