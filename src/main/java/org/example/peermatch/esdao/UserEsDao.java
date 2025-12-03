package org.example.peermatch.esdao;

import org.example.peermatch.model.dto.user.UserEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/28 17:16
 */
public interface UserEsDao extends ElasticsearchRepository<UserEsDTO, String> {
    UserEsDTO findByUserName(String userName);

    UserEsDTO findByProfile(String profile);
}
