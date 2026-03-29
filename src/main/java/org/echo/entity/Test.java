package org.echo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 测试实体类
 * 对应数据库中的test表
 */
@Data
@TableName("test")
public class Test {

    /**
     * 主键ID
     * 使用UUID自动生成
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private String age;
}