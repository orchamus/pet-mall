package com.example.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.back.entity.PetVideo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 宠物视频表 Mapper 接口。
 * <p>
 * 负责 pet_video 数据表的持久化操作，继承 MyBatis-Plus BaseMapper 提供通用 CRUD 能力；
 * 同时提供关联 products 表的视频列表与单条视频查询。
 * </p>
 */
public interface PetVideoMapper extends BaseMapper<PetVideo> {

    /**
     * 查询所有有效的宠物视频，并关联商品基础信息。
     *
     * @return 包含商品名称与价格的宠物视频列表
     */
    @Select("SELECT pv.*, p.name as productName, p.price as productPrice " +
            "FROM pet_video pv " +
            // 左连接 products 表，获取视频关联的商品名称与价格
            "LEFT JOIN products p ON pv.product_id = p.product_id " +
            // 仅查询状态为有效的记录
            "WHERE pv.status = 1 " +
            // 按排序值升序、创建时间降序排列
            "ORDER BY pv.sort_order ASC, pv.created_at DESC")
    List<PetVideo> selectVideoListWithProduct();

    /**
     * 根据视频 ID 查询有效的宠物视频，并关联商品基础信息。
     *
     * @param videoId 视频 ID
     * @return 包含商品名称与价格的宠物视频对象；未找到时返回 null
     */
    @Select("SELECT pv.*, p.name as productName, p.price as productPrice " +
            "FROM pet_video pv " +
            // 左连接 products 表，获取视频关联的商品名称与价格
            "LEFT JOIN products p ON pv.product_id = p.product_id " +
            // 按视频 ID 与有效状态过滤
            "WHERE pv.video_id = #{videoId} AND pv.status = 1")
    PetVideo selectVideoByIdWithProduct(@Param("videoId") Integer videoId);
}
