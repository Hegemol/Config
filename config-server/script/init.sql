CREATE DATABASE  IF NOT EXISTS  `config`  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;

USE `config`;

CREATE TABLE IF NOT EXISTS `config` (
    `id`          bigint       auto_increment            not null comment '主键',
    `app`         varchar(25)  default ''                not null comment '应用名称',
    `content`     varchar(500) default ''                not null comment '配置信息',
    `create_time` datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `update_time` datetime     default CURRENT_TIMESTAMP not null comment '更新时间',
    PRIMARY KEY (`id`),
    constraint unique_app unique (`app`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci comment '配置表';