package com.example.back.service.impl;

import com.example.back.entity.Media;
import com.example.back.mapper.MediaMapper;
import com.example.back.service.IMediaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 媒体资源服务实现类。
 *
 * <p>实现 {@link IMediaService} 接口，当前仅作为 MyBatis-Plus 基础 CRUD 实现占位，由商品媒体关联服务调用。</p>
 */
@Service
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media> implements IMediaService {

}
