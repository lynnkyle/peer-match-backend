package org.example.peermatch.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.example.peermatch.common.BaseResponse;
import org.example.peermatch.common.DeleteRequest;
import org.example.peermatch.common.ErrorCode;
import org.example.peermatch.common.ResultUtils;
import org.example.peermatch.exception.BusinessException;
import org.example.peermatch.model.domain.Team;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.domain.UserTeam;
import org.example.peermatch.model.dto.team.TeamQuery;
import org.example.peermatch.model.request.TeamAddRequest;
import org.example.peermatch.model.request.TeamJoinRequest;
import org.example.peermatch.model.request.TeamQuitRequest;
import org.example.peermatch.model.request.TeamUpdateRequest;
import org.example.peermatch.model.vo.TeamUserVO;
import org.example.peermatch.model.vo.UserVO;
import org.example.peermatch.service.TeamService;
import org.example.peermatch.service.UserService;
import org.example.peermatch.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LinZeyuan
 * @description 队伍接口
 * @createDate 2025/11/13 11:18
 */
@Slf4j
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;


    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest req) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUserVO = userService.getLoginUser(req);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUserVO);
        return ResultUtils.success(teamId, "成功创建队伍");
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest req) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUserVO = userService.getLoginUser(req);
        boolean res = teamService.updateTeam(teamUpdateRequest, loginUserVO);
        return ResultUtils.success(res, "成功更新队伍");
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest req) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUserVO = userService.getLoginUser(req);
        boolean res = teamService.joinTeam(teamJoinRequest, loginUserVO);
        return ResultUtils.success(res, "成功加入队伍");
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest req) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUserVO = userService.getLoginUser(req);
        boolean res = teamService.quitTeam(teamQuitRequest, loginUserVO);
        return ResultUtils.success(res, "成功退出队伍");
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest req) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long teamId = deleteRequest.getId();
        UserVO loginUserVO = userService.getLoginUser(req);
        boolean res = teamService.deleteTeam(teamId, loginUserVO);
        return ResultUtils.success(res, "成功删除队伍");
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(@RequestParam("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new RuntimeException("数据库Team查询队伍为空");
        }
        return ResultUtils.success(team, "成功查询队伍");
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest req) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(req);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(teamList)) {
            return ResultUtils.success(teamList, "成功查询队伍列表");
        }
        // 判断当前用户是否已经加入队伍
        try {
            UserVO loginUser = userService.getLoginUser(req);
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id", loginUser.getId());
            queryWrapper.in("team_id", teamIdList);
            List<UserTeam> userTeamByDbList = userTeamService.list(queryWrapper);
            Set<Long> hasJoinTeamIdSet = userTeamByDbList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(teamUserVO -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(teamUserVO.getId());
                teamUserVO.setHasJoin(hasJoin);
            });
        } catch (Exception e) {

        }
        // 查询已加入队伍的用户信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper();
        queryWrapper.in("team_id", teamIdList);
        List<UserTeam> userTeamListFromIdByDb = userTeamService.list(queryWrapper);
        Map<Long, List<UserTeam>> userTeamListGroupById = userTeamListFromIdByDb.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(teamUserVO -> {
            List<UserTeam> userTeamList = userTeamListGroupById.getOrDefault(teamUserVO.getId(), new ArrayList<>());
            List<Long> userIdList = userTeamList.stream().map(UserTeam::getUserId).collect(Collectors.toList());
            QueryWrapper<User> userQueryWrapper = new QueryWrapper();
            userQueryWrapper.in("id", userIdList);
            List<UserVO> userList = userService.list(userQueryWrapper).stream().map(user -> {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                return userVO;
            }).collect(Collectors.toList());
            teamUserVO.setMembers(userList);
        });
        return ResultUtils.success(teamList, "成功查询队伍列表");
    }

    @GetMapping("/page")
    public BaseResponse<IPage<Team>> listTeamByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        IPage<Team> pageRes = teamService.page(page, queryWrapper);
        return ResultUtils.success(pageRes, "成功查询队伍列表");
    }

    /**
     * 获取当前用户创建的队伍
     *
     * @param teamQuery
     * @param req
     * @return
     */
    @GetMapping("/list/current/create")
    public BaseResponse<List<TeamUserVO>> listCurrentUserCreateTeams(TeamQuery teamQuery, HttpServletRequest req) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUserVO = userService.getLoginUser(req);
        teamQuery.setUserId(loginUserVO.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList, "成功查询队伍列表");
    }

    /**
     * 获取当前用户加入的队伍
     *
     * @param teamQuery
     * @param req
     * @return
     */
    @GetMapping("/list/current/join")
    public BaseResponse<List<TeamUserVO>> listCurrentUserJoinTeams(TeamQuery teamQuery, HttpServletRequest req) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUserVO = userService.getLoginUser(req);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", loginUserVO.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList, "成功查询队伍列表");
    }

}
