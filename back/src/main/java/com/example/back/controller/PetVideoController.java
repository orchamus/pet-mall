package com.example.back.controller;

import com.example.back.Result.ResponseResult;
import com.example.back.entity.PetVideo;
import com.example.back.service.IPetVideoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 宠物视频模块 Controller。
 *
 * <p>提供宠物视频列表浏览与视频详情查询能力，基础路径为 /petVideo。</p>
 *

 */
@RestController
@RequestMapping("/petVideo")
public class PetVideoController {

    @Resource
    private IPetVideoService petVideoService;

    /**
     * 查询宠物视频列表。
     *
     * @return 宠物视频集合
     */
    @GetMapping("/list")
    public ResponseResult<List<PetVideo>> list() {
        List<PetVideo> list = petVideoService.listVideos();
        return ResponseResult.success("获取成功", list);
    }

    /**
     * 查询单个宠物视频详情。
     *
     * @param videoId 视频 ID
     * @return 视频详情对象
     */
    @GetMapping("/detail")
    public ResponseResult<PetVideo> detail(@RequestParam("videoId") Integer videoId) {
        PetVideo video = petVideoService.getVideoById(videoId);
        if (video == null) {
            return ResponseResult.fail("视频不存在");
        }
        return ResponseResult.success("获取成功", video);
    }
}
