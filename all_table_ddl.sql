CREATE TABLE `test` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `age` varchar(255) DEFAULT NULL COMMENT '年龄',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试表';