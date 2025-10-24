package org.example.peermatch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.peermatch.model.domain.Tag;
import org.example.peermatch.service.TagService;
import org.example.peermatch.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author LinZeyuan
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2025-10-24 11:03:06
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




