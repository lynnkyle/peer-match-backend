package org.example.peermatch.job.es;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.peermatch.esdao.UserEsDao;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.dto.user.UserEsDTO;
import org.example.peermatch.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/12/5 14:49
 */
@Slf4j
@Component
public class FullSyncUserToEs implements CommandLineRunner {
    @Resource
    private UserService userService;
    @Resource
    private UserEsDao userEsDao;

    @Override
    public void run(String... args) {
        List<User> userList = userService.list();
        if (CollUtil.isEmpty(userList)) {
            return;
        }
        List<UserEsDTO> userEsDTOList = userList.stream()
                .map(UserEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = userEsDTOList.size();
        log.info("FullSyncPostToEs start,total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            userEsDao.saveAll(userEsDTOList.subList(i, end));
        }
        log.info("FullSyncPostToEs end,total {}", total);
    }
}
