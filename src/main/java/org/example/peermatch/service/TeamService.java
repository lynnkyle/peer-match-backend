package org.example.peermatch.service;

import org.example.peermatch.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.peermatch.model.domain.User;

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
     * @return
     */
    long addTeam(Team team, User loginUser);
}
