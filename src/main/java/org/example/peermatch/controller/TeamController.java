package org.example.peermatch.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.peermatch.common.BaseResponse;
import org.example.peermatch.common.ErrorCode;
import org.example.peermatch.common.ResultUtils;
import org.example.peermatch.exception.BusinessException;
import org.example.peermatch.model.domain.Team;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.dto.TeamQuery;
import org.example.peermatch.model.request.TeamAddRequest;
import org.example.peermatch.model.request.TeamJoinRequest;
import org.example.peermatch.model.request.TeamQuitRequest;
import org.example.peermatch.model.request.TeamUpdateRequest;
import org.example.peermatch.model.vo.TeamUserVO;
import org.example.peermatch.service.TeamService;
import org.example.peermatch.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest req) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(req);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId, "成功创建队伍");
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest req) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(req);
        boolean res = teamService.updateTeam(teamUpdateRequest, loginUser);
        return ResultUtils.success(res, "成功更新队伍");
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest req) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(req);
        boolean res = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(res, "成功加入队伍");
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest req) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(req);
        boolean res = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(res, "成功退出队伍");
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestParam("teamId") long teamId, HttpServletRequest req) {
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(req);
        boolean res = teamService.deleteTeam(teamId, loginUser);
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
}
