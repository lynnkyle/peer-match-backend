package org.example.peermatch.constant;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/12 16:31
 */


public interface CacheConstant {
    //  分布式锁
    String recommendLock = "peer-match:user-job:recommend:lock";
    String recommendCache = "peer-match:user:recommend:";
    //  分布式锁
    String joinTeamLock = "peer-match:team:join:lock";
    String joinTeamCache = "peer-match:team:join:";
}
