package org.example.peermatch.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.peermatch.common.BaseResponse;
import org.example.peermatch.common.ErrorCode;
import org.example.peermatch.common.ResultUtils;
import org.example.peermatch.constant.CacheConstant;
import org.example.peermatch.constant.UserConstant;
import org.example.peermatch.exception.BusinessException;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.request.UserLoginRequest;
import org.example.peermatch.model.request.UserRegisterRequest;
import org.example.peermatch.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author LinZeyuan
 * @description 用户接口
 * @createDate 2025/9/19 16:07
 */
@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class UserController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;

    /*
        用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userCode = userRegisterRequest.getUserCode();
        long userId = userService.userRegister(userAccount, userPassword, checkPassword, userCode);
        return ResultUtils.success(userId, "用户注册成功, 并返回用户ID");
    }

    /*
        用户登录
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest req) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        User user = userService.doLogin(userAccount, userPassword, req);
        return ResultUtils.success(user, "用户登录成功, 并返回用户信息");
    }

    @PostMapping("/logout")
    public BaseResponse<Void> userLogout(HttpServletRequest req) {
        userService.userLogout(req);
        return ResultUtils.success(null, "用户退出登录成功");
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest req) {
        User currentUser = (User) req.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long id = currentUser.getId();
        // todo 校验用户是否合法
        User user = userService.getById(id);
        user = userService.getSafetyUser(user);
        return ResultUtils.success(user, "当前用户信息获取成功,并返回当前用户信息");
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(@RequestParam(value = "name", required = false) String userName, HttpServletRequest req) {
        // 用户鉴权
        if (!userService.isAdmin(req)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like("user_name", userName);
        }
        List<User> userList = userService.list(queryWrapper);
        userList = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userList, "用户列表获取成功, 并返回用户列表");
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList, "成功根据标签返回用户列表");
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody User user, HttpServletRequest req) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(req);
        boolean res = userService.updateUser(user, loginUser);
        return ResultUtils.success(res, "用户信息更新成功");
    }

    @GetMapping("/recommend")
    public BaseResponse<IPage<User>> recommendUsers(int pageNum, int pageSize, HttpServletRequest req) {
        // 缓存存在, 直接读缓存
        User loginUser = userService.getLoginUser(req);
        String redisKey = String.format(CacheConstant.recommendCache + "%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        IPage<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage, "用户列表获取成功, 并返回用户列表");
        }
        // 缓存不存在, 读取数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        try {
            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis error", e.getMessage());
        }
        return ResultUtils.success(userPage, "用户列表获取成功, 并返回用户列表");
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestParam("id") long id, HttpServletRequest req) {
        if (!userService.isAdmin(req)) throw new BusinessException(ErrorCode.NO_AUTH);
        if (id <= 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        boolean res = userService.removeById(id);
        if (!res) {
            throw new RuntimeException("数据库User删除用户异常");
        }
        return ResultUtils.success(res, "用户删除成功");
    }

}
