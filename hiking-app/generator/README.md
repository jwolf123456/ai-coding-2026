# Generator

基于 MyBatis-Plus Generator 的代码生成工具模块，用于根据数据库表结构自动生成 Entity、Mapper、Service、Controller 等代码。

## 功能特性

- 根据数据库表自动生成标准分层代码（Entity / Mapper / Service / Controller）
- 自动去除表前缀 `t_`
- Entity 继承 `BaseEntity`，自动排除公共字段（`id`、`create_time`、`update_time`、`deleted`）
- 启用 Lombok、逻辑删除、Rest 风格 Controller
- 生成完成后输出新增文件和覆盖文件列表
- 数据表实体Entity如果存在会强制覆盖

## 使用案例，配置工具类里的PROJECT_BASE_DIR，指向项目根目录；运行mvn install生成jar包到D:\generator.jar：
java -jar D:\generator.jar user-center(子模块文件夹名) hiking_user（数据库名） t_user_role（数据库表名，多个表用逗号隔开）
