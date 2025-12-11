package org.example.peermatch.job.es;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.peermatch.esdao.UserEsDao;
import org.example.peermatch.mapper.UserMapper;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.dto.user.UserEsDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LinZeyuan
 * @description 增量同步用户到es
 * @createDate 2025/12/5 14:26
 */
@Slf4j
//@Component
public class IncSyncUserToEs {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserEsDao userEsDao;

    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        Date fiveMinutesAgo = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
        List<User> userList = userMapper.listUserWithDelete(fiveMinutesAgo);
        if (CollUtil.isEmpty(userList)) {
            log.info("no inc post");
            return;
        }
        List<UserEsDTO> userEsDTOList = userList.stream()
                .map(UserEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = userEsDTOList.size();
        log.info("IncSyncUserToEs start,total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            userEsDao.saveAll(userEsDTOList.subList(i, end));
        }
        log.info("IncSyncUserToEs end,total {}", total);
    }
}
