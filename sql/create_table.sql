-- auto-generated definition
create table user
(
    id            bigint auto_increment comment '用户id'
        primary key,
    user_name     varchar(256)                       null comment '用户昵称',
    user_account  varchar(256)                       null comment '用户账号',
    user_password varchar(512)                       null comment '用户密码',
    avatar_url    varchar(1024)                      null comment '用户头像',
    gender        tinyint                            null comment '用户性别',
    phone         varchar(128)                       null comment '手机号码',
    email         varchar(512)                       null comment '用户邮箱',
    user_role     tinyint  default 0                 null comment '用户角色(0-普通用户,1-管理员)',
    user_status   tinyint  default 0                 null comment '用户状态(0-正常)',
    code          varchar(256)                       null comment '校验编码',
    profile       varchar(512)                       null comment '用户简介',
    tags          varchar(1024)                      null comment '标签列表',
    is_delete     tinyint  default 0                 null comment '逻辑删除(1-删除)',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '用户表';

create table tag
(
    id          bigint auto_increment comment '标签id'
        primary key,
    tag_name    varchar(256) null comment '标签名称',
    user_id     bigint null comment '用户id',
    parent_id   bigint null comment '父标签id',
    is_parent   tinyint  default 0 null comment '是否为父标签(0-否,1-是)',
    is_delete   tinyint  default 0 null comment '逻辑删除(1-删除)',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint idx_tag_name
        unique (tag_name) comment '标签名唯一索引'
) comment '标签表';

create index idx_user_id
    on tag (user_id) comment '用户id索引';

create table team
(
    id          bigint auto_increment comment '队伍id'
        primary key,
    team_name   varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment '队伍描述',
    user_id     bigint                             not null comment '用户id(队长id)',
    max_num     int      default 1                 null comment '队伍最大人数',
    expire_time datetime                           null comment '队伍过期时间',
    team_status tinyint  default 0                 null comment '队伍状态(0-公开, 1-私有, 2-加密)',
    password    varchar(64)                        null comment '队伍密码',
    is_delete   tinyint  default 0                 null comment '逻辑删除(1-删除)',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '队伍表';

create table user_team
(
    id          bigint auto_increment comment '用户-队伍id'
        primary key,
    user_id     bigint not null comment '用户id',
    team_id     bigint not null comment '队伍id',
    join_time   datetime null comment '创建时间',
    is_delete   tinyint  default 0 null comment '逻辑删除(1-删除)',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '用户-队伍表';
