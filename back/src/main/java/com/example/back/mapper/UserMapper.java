package com.example.back.mapper;

import com.example.back.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 * <p>
 * 负责 User 实体对应数据表的持久化操作，继承 MyBatis-Plus BaseMapper 提供通用 CRUD 能力。
 * </p>
 */
public interface UserMapper extends BaseMapper<User> {

}
