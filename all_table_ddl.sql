-- 测试表
CREATE TABLE `test` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `age` varchar(255) DEFAULT NULL COMMENT '年龄',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试表';

-- 固定表单表定义
CREATE TABLE `echo_online_table_definition` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `table_name` varchar(100) NOT NULL COMMENT '表名',
  `table_desc` varchar(255) DEFAULT NULL COMMENT '表描述',
  `table_type` varchar(20) NOT NULL DEFAULT '单表' COMMENT '表类型：单表/主表/从表',
  `form_category` varchar(20) DEFAULT '业务表' COMMENT '表单分类：业务表/系统表/临时表',
  `primary_key_strategy` varchar(20) DEFAULT 'UUID' COMMENT '主键策略：UUID/自增/雪花ID',
  `show_checkbox` tinyint(1) DEFAULT 0 COMMENT '是否显示复选框',
  `theme_template` varchar(50) DEFAULT 'default' COMMENT '主题模板',
  `form_style` varchar(50) DEFAULT 'default' COMMENT '表单风格',
  `scroll_bar` tinyint(1) DEFAULT 0 COMMENT '是否显示滚动条',
  `pagination` tinyint(1) DEFAULT 1 COMMENT '是否分页',
  `is_tree` tinyint(1) DEFAULT 0 COMMENT '是否树形结构',
  `version` int(11) DEFAULT 1 COMMENT '版本号',
  `sync_status` varchar(20) DEFAULT '未同步' COMMENT '同步状态',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `sys_org_code` varchar(64) DEFAULT NULL COMMENT '所属部门',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固定表单表定义';

-- 固定表单字段配置
CREATE TABLE `echo_online_table_field` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `table_name` varchar(100) NOT NULL COMMENT '表名',
  `field_name` varchar(50) NOT NULL COMMENT '字段名称',
  `field_comment` varchar(255) DEFAULT NULL COMMENT '字段备注',
  `field_length` int(11) DEFAULT 50 COMMENT '字段长度',
  `decimal_point` int(11) DEFAULT 0 COMMENT '小数点位数',
  `default_value` varchar(255) DEFAULT NULL COMMENT '默认值',
  `field_type` varchar(50) NOT NULL DEFAULT 'String' COMMENT '字段类型',
  `is_primary_key` tinyint(1) DEFAULT 0 COMMENT '是否主键',
  `allow_null` tinyint(1) DEFAULT 1 COMMENT '是否允许空值',
  `sync_database` tinyint(1) DEFAULT 1 COMMENT '是否同步数据库',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固定表单字段配置';

CREATE TABLE `echo_form_definition` (
                                        `id` varchar(36) NOT NULL COMMENT '主键ID',
                                        `form_name` varchar(100) NOT NULL COMMENT '表单名称',
                                        `form_version` int DEFAULT 1 COMMENT '表单版本',
                                        `form_schema` text COMMENT '表单JSON配置',
                                        `status` varchar(20) DEFAULT '草稿' COMMENT '状态(草稿/已发布)',
                                        `description` varchar(500) DEFAULT NULL COMMENT '表单描述',
                                        `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_form_name` (`form_name`),
                                        KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单定义表';

ALTER TABLE echo_form_definition
    ADD COLUMN table_name VARCHAR(100) COMMENT '物理表名' AFTER form_schema,
ADD COLUMN sync_status VARCHAR(20) DEFAULT '未同步' COMMENT '同步状态' AFTER table_name,
ADD COLUMN sync_time DATETIME COMMENT '同步时间' AFTER sync_status;
