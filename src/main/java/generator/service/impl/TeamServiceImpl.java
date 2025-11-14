package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Team;
import generator.service.TeamService;
import generator.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author LinZeyuan
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2025-11-14 16:01:07
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




