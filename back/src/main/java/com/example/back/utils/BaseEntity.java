package com.example.back.utils;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础实体类。
 * <p>为所有业务实体提供公共字段（如主键 id）及扩展字段（etc），并提供向扩展字段中存放额外数据的能力。</p>
 *
 * @author bill
 * @since 1.0
 */
@Accessors(chain = true)
@Data
public class BaseEntity implements Serializable {

    /** 主键标识 */
    @TableId(value = "id")
    private String id;

    /**
     * 扩展字段。
     * <p>不映射到数据库表，用于临时存放额外业务数据。</p>
     */
    @TableField(exist = false)
    private Object etc;


    /**
     * 向扩展字段 etc 中存放键值对数据。
     * <p>首次存放时会自动将 etc 初始化为 {@link HashMap}。</p>
     *
     * @param key   键
     * @param value 值
     */
    @SuppressWarnings("unchecked")
    public void put(String key, Object value) {
        // 首次使用时将 etc 初始化为 Map 结构
        if (etc == null) {
            etc = new HashMap<String, Object>();
        }
        ((Map<String, Object>) etc).put(key, value);
    }
}
