package org.echo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("echo_online_table_definition")
public class TableDefinition {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String tableName;

    private String tableDesc;

    private String tableType;

    private String formCategory;

    private String primaryKeyStrategy;

    private Boolean showCheckbox;

    private String themeTemplate;

    private String formStyle;

    private Boolean scrollBar;

    private Boolean pagination;

    private Boolean isTree;

    private Integer version;

    private String syncStatus;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String sysOrgCode;

    @TableField(exist = false)
    private List<TableFieldDef> fields;
}