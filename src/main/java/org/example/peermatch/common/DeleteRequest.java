package org.example.peermatch.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LinZeyuan
 * @description 通用删除请求
 * @createDate 2025/11/27 10:39
 */
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = 929947410979679120L;
    /**
     * 队伍id
     */
    private Long id;
}
