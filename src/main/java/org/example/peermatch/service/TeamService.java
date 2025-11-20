package org.example.peermatch.service;

import org.example.peermatch.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.dto.TeamQuery;
import org.example.peermatch.model.request.TeamJoinRequest;
import org.example.peermatch.model.request.TeamUpdateRequest;
import org.example.peermatch.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
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
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

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
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
