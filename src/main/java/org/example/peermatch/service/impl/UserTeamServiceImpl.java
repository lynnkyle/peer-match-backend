package org.example.peermatch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.peermatch.model.domain.UserTeam;
import org.example.peermatch.service.UserTeamService;
import org.example.peermatch.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author LinZeyuan
* @description 针对表【user_team(用户-队伍表)】的数据库操作Service实现
* @createDate 2025-11-13 11:10:27
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




