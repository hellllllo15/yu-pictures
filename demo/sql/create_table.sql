-- 用户表

create table if not exists user
(
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `userPassword` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `userName` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `userProfile` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户简介',
  `userRole` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色: user/admin',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `editText` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_userAccount` (`userAccount`),
  KEY `idx_userName` (`userName`)
) ENGINE=InnoDB AUTO_INCREMENT=1955638411056488450 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- 图片表
create table if not exists picture
(
    id bigint auto_increment comment 'id' primary key,
    url varchar(512) not null comment '图片 url',
    name varchar(128) not null comment '图片名称',
    introduction varchar(512) null comment '简介',
    category varchar(64) null comment '分类',
    tags varchar(512) null comment '标签（JSON 数组）',
    picSize bigint null comment '图片体积',
    picWidth int null comment '图片宽度',
    picHeight int null comment '图片高度',
    picScale double null comment '图片宽高比例',
    picFormat varchar(32) null comment '图片格式',
    userId bigint not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete tinyint default 0 not null comment '是否删除',
    INDEX idx_name(name), -- 提升基于图片名称的查询性能
    INDEX idx_introduction(introduction), -- 用于模糊搜索图片简介
    INDEX idx_category(category), -- 提升基于分类的查询性能
    INDEX idx_tags(tags), -- 提升基于标签的查询性能
    INDEX idx_userid(userId) -- 提升基于用户 ID 的查询性能
) comment '图片' collate = utf8mb4_unicode_ci;


   --图片审核字段

   ALTER TABLE picture
   -- 添加新列
   ADD COLUMN reviewStatus INT DEFAULT 0 NOT NULL COMMENT '审核状态：0-待审核;1-通过;2-拒绝',
   ADD COLUMN reviewMessage VARCHAR(512) NULL COMMENT '审核信息',
   ADD COLUMN reviewerId BIGINT NULL COMMENT '审核人ID',
   ADD COLUMN reviewTime DATETIME NULL COMMENT '审核时间';

   -- 创建基于 reviewStatus 列的索引
   CREATE INDEX idx_reviewStatus ON picture (reviewStatus);