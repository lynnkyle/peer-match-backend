package org.example.peermatch.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LinZeyuan
 * @description 通用分页请求参数
 * @createDate 2025/11/13 14:53
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -6951528734714344464L;
    // 当前页面
    protected int pageNum = 1;
    // 页面大小
    protected int pageSize = 10;

}
