package org.example.peermatch.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.example.peermatch.common.ErrorCode;
import org.example.peermatch.common.ResultUtils;
import org.example.peermatch.constant.TeamStatus;
import org.example.peermatch.exception.BusinessException;
import org.example.peermatch.mapper.TeamMapper;
import org.example.peermatch.model.domain.Team;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.domain.UserTeam;
import org.example.peermatch.model.dto.TeamQuery;
import org.example.peermatch.model.request.TeamJoinRequest;
import org.example.peermatch.model.request.TeamUpdateRequest;
import org.example.peermatch.model.vo.TeamUserVO;
import org.example.peermatch.model.vo.UserVO;
import org.example.peermatch.service.TeamService;
import org.example.peermatch.service.UserService;
import org.example.peermatch.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author LinZeyuan
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2025-11-13 11:10:28
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1. 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3. 校验信息
        final long userId = loginUser.getId();
        //i. 队伍人数>1 且 <=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        //ii. 队伍名称 <=20
        String teamName = team.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不符合要求");
        }
        //iii. 队伍描述 <=512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不符合要求");
        }
        //iv. 队伍状态 0-2
        int teamStatus = Optional.ofNullable(team.getTeamStatus()).orElse(0);
        TeamStatus teamStatusEnum = TeamStatus.getTeamStatusByValue(teamStatus);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不符合要求");
        }
        //v. 队伍状态 =2 && 队伍密码 <=32
        String password = team.getPassword();
        if (TeamStatus.SECRET == teamStatusEnum && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍密码设置不符合要求");
        }
        //vi. 超时时间 >当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍创建超时时间小于当前时间");
        }
        //vii. 用户最多创建5个队伍
        // TODO 存在BUG, 可能同时创建 100 个队伍 (synchronized、分布式锁)
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        long teamNum = this.count(queryWrapper);
        if (teamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多允许创建5个用户");
        }
        //4. 插入数据库队伍信息
        //i.插入队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        if (!save) {
            throw new RuntimeException("数据库Team插入队伍异常");
        }
        //ii.插入用户队伍表
        Long teamId = team.getId();
        if (teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍ID异常");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        save = userTeamService.save(userTeam);
        if (!save) {
            throw new RuntimeException("数据库UserTeam插入用户队伍异常");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //1. 请求参数(队伍名称)是否为空
        TeamStatus teamStatusEnum = null;
        if (teamQuery != null) {
            Long teamId = teamQuery.getId();
            if (teamId != null && teamId > 0) {
                queryWrapper.eq("id", teamId);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("team_name", searchText).or().like("description", searchText));
            }
            String teamName = teamQuery.getTeamName();
            if (StringUtils.isNotBlank(teamName)) {
                queryWrapper.like("team_name", teamName);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("max_num", maxNum);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("user_id", userId);
            }
            Integer teamStatus = teamQuery.getTeamStatus();
            teamStatusEnum = TeamStatus.getTeamStatusByValue(teamStatus);
            if (teamStatus != null && teamStatusEnum != null) {
                queryWrapper.eq("team_status", teamStatus);
            }
        }
        //2. 查询队伍信息
        //i.条件查询队伍属性作为查询条件
        //ii.条件查询未过期队伍
        queryWrapper.and(qw -> qw.isNull("expire_time").or().gt("expire_time", new Date()));
        //iii.条件查询管理员才能查看加密房间
        if (teamStatusEnum == null) {
            teamStatusEnum = TeamStatus.PUBLIC;
        }
        if (!isAdmin && teamStatusEnum != TeamStatus.PUBLIC) {
            throw new BusinessException(ErrorCode.NO_AUTH, "非管理员不允许访问私密房间");
        }
        queryWrapper.eq("team_status", teamStatusEnum.getValue());
        //iv.条件查询根据关键词（name、description）查询队伍
        List<Team> teamList = this.list(queryWrapper);
        //v.关联查询已加入队伍的用户信息
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        //1. 请求参数是否为空
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 校验信息
        Long id = teamUpdateRequest.getId();
        //ii.过期时间是否大于当前时间
        Date expireTime = teamUpdateRequest.getExpireTime();
        if (expireTime != null && new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍更新超时时间小于当前时间");
        }
        //iii.队伍状态与密码（状态改为加密，必须有密码）
        Integer teamStatus = teamUpdateRequest.getTeamStatus();
        TeamStatus teamStatusEnum = TeamStatus.getTeamStatusByValue(teamStatus);
        if (teamStatusEnum == TeamStatus.SECRET) {
            String password = teamUpdateRequest.getPassword();
            if (StringUtils.isBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍为加密状态,密码不允许为空");
            }
        }
        //i.查询队伍是否存在
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team teamFromDb = this.getById(id);
        if (teamFromDb == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        //3. 管理员或者队伍创建者可以修改
        if (!teamFromDb.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //TODO 4. 修改信息新旧值一致，则无需更新操作
        //5. 更新成功
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        boolean res = this.updateById(updateTeam);
        if (!res) {
            throw new RuntimeException("数据库Team更新用户队伍异常");
        }
        return res;
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        //1.请求参数是否为空
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.校验信息
        Long userId = loginUser.getId();
        //ii.只允许加入未满、未过期的队伍
        //iv. 禁止加入私有队伍
        //v. 加入私密队伍要求密码匹配
        Long teamId = teamJoinRequest.getId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team teamFromDb = this.getById(teamId);
        if (teamFromDb == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Date expireTime = teamFromDb.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
        }
        Integer teamStatus = teamFromDb.getTeamStatus();
        TeamStatus teamStatusEnum = TeamStatus.getTeamStatusByValue(teamStatus);
        if (teamStatusEnum == TeamStatus.PRIVATE) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (teamStatusEnum == TeamStatus.SECRET) {
            if (StringUtils.isBlank(password) || !password.equals(teamFromDb.getPassword())) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "无法加入私密队伍");
            }
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper();
        userTeamQueryWrapper.eq("team_id", teamId);
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (teamHasJoinNum >= teamFromDb.getMaxNum()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍人数已满");
        }
        //i.用户最多加入5个队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", userId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count > 5) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户最多加入5个队伍");
        }
        //iii.不能重复加入已加入的队伍（幂等性）
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", userId);
        userTeamQueryWrapper.eq("team_id", teamId);
        long hasUserJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasUserJoinNum > 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户已加入该队伍");
        }
        //3.修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean save = userTeamService.save(userTeam);
        if (!save) {
            throw new RuntimeException("数据库UserTeam插入用户队伍异常");
        }
        return save;
    }
}




