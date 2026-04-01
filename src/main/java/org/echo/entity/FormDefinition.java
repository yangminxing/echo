package org.echo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("echo_form_definition")
public class FormDefinition {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String formName;

    private Integer formVersion;

    private String formSchema;

    private String status;

    private String description;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;
}
