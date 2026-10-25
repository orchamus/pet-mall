package com.example.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.back.entity.Location;
import org.apache.ibatis.annotations.Mapper;

/**
 * 位置信息表 Mapper 接口。
 * <p>
 * 负责 Location 实体对应数据表的持久化操作，继承 MyBatis-Plus BaseMapper 提供通用 CRUD 能力。
 * </p>
 */
@Mapper
public interface LocationMapper extends BaseMapper<Location> {
}
