package org.echo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("echo_online_table_field")
public class TableFieldDef {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String tableName;

    private String fieldName;

    private String fieldComment;

    private Integer fieldLength;

    private Integer decimalPoint;

    private String defaultValue;

    private String fieldType;

    private Boolean isPrimaryKey;

    private Boolean allowNull;

    private Boolean syncDatabase;

    private Integer sortOrder;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;
}