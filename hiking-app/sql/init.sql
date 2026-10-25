CREATE DATABASE IF NOT EXISTS hiking_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS hiking_line DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE hiking_user;

CREATE TABLE IF NOT EXISTS t_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    status      INT          DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    deleted     INT          DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

USE hiking_line;

CREATE TABLE IF NOT EXISTS t_line (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    title       VARCHAR(100) NOT NULL COMMENT '线路标题',
    description TEXT         DEFAULT NULL COMMENT '线路描述',
    distance    DECIMAL(10,2) DEFAULT NULL COMMENT '距离(公里)',
    difficulty  INT          DEFAULT 1 COMMENT '难度 1-简单 2-中等 3-困难 4-极难',
    start_point VARCHAR(255) DEFAULT NULL COMMENT '起点',
    end_point   VARCHAR(255) DEFAULT NULL COMMENT '终点',
    cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    status      INT          DEFAULT 1 COMMENT '状态 1-上线 0-下线',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    deleted     INT          DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线路表';