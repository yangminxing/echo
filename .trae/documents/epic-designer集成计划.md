# Epic-Designer 表单设计器集成计划

## 概述
在 Echo 项目中集成 epic-designer 表单设计器，实现表单管理列表和表单设计功能，包含完整的前后端实现。

## 技术选型
- **前端表单设计器**: epic-designer (基于 Vue3，支持 Element Plus)
- **UI 框架**: Element Plus
- **后端**: Spring Boot 3 + MyBatis-Plus
- **数据库**: MySQL

---

## 一、前端实现

### 1.1 安装依赖
在 `frontend/package.json` 中添加：
- `epic-designer`
- `@epic-designer/element-plus`
- `element-plus`

### 1.2 配置 Element Plus 和 Epic-Designer
修改 `frontend/src/main.ts`:
- 引入 Element Plus 样式和组件
- 引入 epic-designer 样式
- 注册 Element Plus UI

### 1.3 创建 API 接口
新建 `frontend/src/api/formDesigner.ts`:
- `getFormList()` - 获取表单列表
- `getFormById(id)` - 获取表单详情
- `saveForm(data)` - 保存表单
- `updateForm(data)` - 更新表单
- `deleteForm(id)` - 删除表单

### 1.4 创建类型定义
新建 `frontend/src/types/formDesigner.ts`:
- `FormDefinition` 接口定义

### 1.5 创建表单管理页面
修改 `frontend/src/views/designer/FormDesigner.vue`:
- 表单列表展示（表格形式）
- 显示字段：表单名称、表单版本、创建时间、更新时间、状态
- 新增按钮：打开空白设计器
- 表单设计按钮：打开对应表单的设计器
- 删除按钮

### 1.6 创建表单设计器页面
新建 `frontend/src/views/designer/FormEditor.vue`:
- 集成 EDesigner 组件
- 支持新建和编辑模式
- 保存/返回按钮
- 通过路由参数区分新建/编辑

### 1.7 更新路由配置
修改 `frontend/src/router/index.ts`:
- `/home/form-designer` - 表单管理列表
- `/home/form-editor/:id?` - 表单设计器（id 可选）

---

## 二、后端实现

### 2.1 创建实体类
新建 `src/main/java/org/echo/entity/FormDefinition.java`:
```java
- id: String (UUID)
- formName: String (表单名称)
- formVersion: Integer (表单版本)
- formSchema: String (表单JSON配置，TEXT类型)
- status: String (状态：草稿/已发布)
- description: String (表单描述)
- createBy: String
- createTime: Date
- updateBy: String
- updateTime: Date
```

### 2.2 创建 Mapper
新建 `src/main/java/org/echo/mapper/FormDefinitionMapper.java`:
- 继承 BaseMapper<FormDefinition>

### 2.3 创建 Service
新建 `src/main/java/org/echo/service/FormDefinitionService.java`:
- `list()` - 查询所有表单
- `page(current, size)` - 分页查询
- `getById(id)` - 根据ID查询
- `save(formDefinition)` - 保存表单
- `update(formDefinition)` - 更新表单（版本号+1）
- `delete(id)` - 删除表单

### 2.4 创建 Controller
新建 `src/main/java/org/echo/controller/FormDefinitionController.java`:
- `GET /form-definition/list` - 获取列表
- `GET /form-definition/page` - 分页查询
- `GET /form-definition/{id}` - 获取详情
- `POST /form-definition` - 新增表单
- `PUT /form-definition` - 更新表单
- `DELETE /form-definition/{id}` - 删除表单

### 2.5 数据库建表 SQL
```sql
CREATE TABLE echo_form_definition (
    id VARCHAR(36) PRIMARY KEY,
    form_name VARCHAR(100) NOT NULL COMMENT '表单名称',
    form_version INT DEFAULT 1 COMMENT '表单版本',
    form_schema TEXT COMMENT '表单JSON配置',
    status VARCHAR(20) DEFAULT '草稿' COMMENT '状态',
    description VARCHAR(500) COMMENT '表单描述',
    create_by VARCHAR(50),
    create_time DATETIME,
    update_by VARCHAR(50),
    update_time DATETIME
) COMMENT '表单定义表';
```

---

## 三、文件清单

### 前端新建文件
1. `frontend/src/api/formDesigner.ts` - API 接口
2. `frontend/src/types/formDesigner.ts` - 类型定义
3. `frontend/src/views/designer/FormEditor.vue` - 表单设计器页面

### 前端修改文件
1. `frontend/package.json` - 添加依赖
2. `frontend/src/main.ts` - 配置 Element Plus
3. `frontend/src/views/designer/FormDesigner.vue` - 改为表单管理列表
4. `frontend/src/router/index.ts` - 添加路由

### 后端新建文件
1. `src/main/java/org/echo/entity/FormDefinition.java`
2. `src/main/java/org/echo/mapper/FormDefinitionMapper.java`
3. `src/main/java/org/echo/service/FormDefinitionService.java`
4. `src/main/java/org/echo/controller/FormDefinitionController.java`

---

## 四、实施顺序

1. **后端开发**
   - 创建实体类
   - 创建 Mapper
   - 创建 Service
   - 创建 Controller

2. **前端开发**
   - 安装依赖
   - 配置 main.ts
   - 创建类型定义
   - 创建 API 接口
   - 创建表单管理列表页面
   - 创建表单设计器页面
   - 更新路由配置

3. **测试验证**
   - 启动后端服务
   - 启动前端服务
   - 测试表单 CRUD 功能
   - 测试表单设计器功能
