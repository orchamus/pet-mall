package com.example.back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.back.entity.PetVideo;

import java.util.List;

public interface IPetVideoService extends IService<PetVideo> {

    List<PetVideo> listVideos();

    PetVideo getVideoById(Integer videoId);
}
