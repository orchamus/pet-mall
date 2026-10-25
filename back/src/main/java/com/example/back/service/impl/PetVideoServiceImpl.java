package com.example.back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.back.entity.PetVideo;
import com.example.back.mapper.PetVideoMapper;
import com.example.back.service.IPetVideoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetVideoServiceImpl extends ServiceImpl<PetVideoMapper, PetVideo> implements IPetVideoService {

    @Override
    public List<PetVideo> listVideos() {
        return baseMapper.selectVideoListWithProduct();
    }

    @Override
    public PetVideo getVideoById(Integer videoId) {
        return baseMapper.selectVideoByIdWithProduct(videoId);
    }
}
