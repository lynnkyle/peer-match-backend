package org.example.peermatch.service.impl;

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
import org.example.peermatch.service.TeamService;
import org.example.peermatch.service.UserTeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * @author LinZeyuan
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2025-11-13 11:10:28
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1). 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2). 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3). 校验信息
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
        if (TeamStatus.SECRET.equals(teamStatusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
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
        if (teamNum > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多允许创建5个用户");
        }
        //4). 插入数据库队伍信息
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
}




