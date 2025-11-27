package org.example.peermatch.service;

import org.example.peermatch.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.peermatch.model.dto.TeamQuery;
import org.example.peermatch.model.request.TeamJoinRequest;
import org.example.peermatch.model.request.TeamQuitRequest;
import org.example.peermatch.model.request.TeamUpdateRequest;
import org.example.peermatch.model.vo.TeamUserVO;
import org.example.peermatch.model.vo.UserVO;

import java.util.List;

/**
 * @author LinZeyuan
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2025-11-13 11:10:28
 */
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     *
     * @param team
     * @param loginUserVO
     * @return
     */
    long addTeam(Team team, UserVO loginUserVO);

    /**
     * 查询队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param loginUserVO
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, UserVO loginUserVO);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUserVO
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, UserVO loginUserVO);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUserVO
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, UserVO loginUserVO);

    /**
     * 解散队伍
     *
     * @param teamId
     * @param loginUserVO
     * @return
     */
    boolean deleteTeam(Long teamId, UserVO loginUserVO);
}
