-- 四川轻化工大学校园失物招领系统
-- 在 MySQL Workbench 8.0 中打开本文件，选中全部后执行（会重建本系统的表）
-- 连接：localhost:3306  用户：root

CREATE DATABASE IF NOT EXISTS suse_lostfound
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE suse_lostfound;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reply;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS lost_item;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (
    user_id     INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(20)  NOT NULL UNIQUE COMMENT '登录账号',
    password    VARCHAR(64)  NOT NULL COMMENT '登录密码（课程设计按明文存储）',
    name        VARCHAR(20)  NOT NULL COMMENT '姓名',
    phone       VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '手机号',
    role        TINYINT      NOT NULL DEFAULT 0 COMMENT '0学生 1管理员',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE lost_item (
    item_id     INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT          NOT NULL COMMENT '发布人',
    title       VARCHAR(50)  NOT NULL COMMENT '标题',
    description VARCHAR(500) NOT NULL COMMENT '详情',
    item_type   VARCHAR(10)  NOT NULL COMMENT '丢失 / 拾到',
    location    VARCHAR(100) NOT NULL COMMENT '地点',
    contact     VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '联系方式，可为空',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0待处理 1已解决 2已关闭',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_user FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE,
    KEY idx_item_type (item_type),
    KEY idx_item_status (status),
    KEY idx_item_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物招领表';

CREATE TABLE notice (
    notice_id   INT PRIMARY KEY AUTO_INCREMENT,
    admin_id    INT          NULL COMMENT '手工公告的管理员；系统自动公告为空',
    title       VARCHAR(80)  NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notice_admin FOREIGN KEY (admin_id) REFERENCES `user`(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 预留 4 名管理员：用户名和密码都是学号
INSERT INTO `user` (username, password, name, phone, role, create_time) VALUES
('24101100527', '24101100527', '丁玥',   '13900001027', 1, '2026-09-01 08:30:00'),
('24101100534', '24101100534', '张想想', '13900001034', 1, '2026-09-01 08:32:00'),
('24101100501', '24101100501', '陈跃国', '13900001001', 1, '2026-09-01 08:34:00'),
('24101100502', '24101100502', '代京呈', '13900001002', 1, '2026-09-01 08:36:00');

-- 预置学生：12 位学号，密码与学号相同
-- 前两位入学学年，接着 1011 为专业代码，再 3 位班号，末 3 位班内学号
INSERT INTO `user` (username, password, name, phone, role, create_time) VALUES
('241011005001', '241011005001', '李明',   '13812345001', 0, '2026-09-08 09:12:00'),
('241011005018', '241011005018', '周予安', '13812345018', 0, '2026-09-08 09:18:00'),
('241011012023', '241011012023', '孙可欣', '13812341223', 0, '2026-09-09 11:20:00');

INSERT INTO lost_item (user_id, title, description, item_type, location, contact, status, create_time) VALUES
(5, '蓝色校园卡', '蓝色卡套，姓名李明，学号 241011005001。一楼还书时掉的，卡套里还有一张饭卡。', '丢失', '李白河校区图书馆一楼服务台', '13812345001', 0, '2026-10-14 09:20:00'),
(6, '黑框眼镜', '半框，右镜腿内侧刻了“周”。高数课后在教室讲台边找不到了。', '丢失', '李白河校区二教 305 讲台右侧', '13812345018', 0, '2026-10-15 11:08:00'),
(7, '宿舍钥匙一串', '共 4 把，挂着绿色校徽挂件。已放在 6 栋一楼值班室，请带学生证核对。', '拾到', '李白河校区学生宿舍 6 栋值班室', '', 0, '2026-10-16 07:46:00'),
(5, '藏青色折叠伞', '伞柄有一圈米黄色绑绳。失主已到值班室认领。', '拾到', '宜宾校区教学楼一楼大厅', '13812345001', 1, '2026-10-12 18:22:00'),
(6, '金士顿 32G U 盘', '黑色金属壳，自己已在书包夹层找到，帖子关闭。', '丢失', '汇东校区计算机机房 304', '13812345018', 2, '2026-10-10 19:05:00'),
(7, '绿色学生证', '绿色封皮，短发女生照片。暂存在校团委办公室。', '拾到', '汇东校区田径场看台', '13812341223', 0, '2026-10-16 17:18:00');

INSERT INTO notice (admin_id, title, content, create_time) VALUES
(1, '关于规范使用校园失物招领平台的通知',
 '同学们好。发布信息时请写清地点和物品特征。联系方式可以不留，拾到的同学请在详情里写明物品存放在哪里。认领时请当面核对。',
 '2026-09-20 09:00:00'),
(3, '请尽快认领滞留物品',
 '宜宾校区教学楼大厅、李白河校区宿舍值班室目前仍有伞具和钥匙滞留。请失主携带证件认领。',
 '2026-10-13 11:30:00'),
(NULL, '系统通知：平台已完成初始化',
 '数据库校验通过后，系统会在用户注册、发布信息、标记已解决、关闭或删除信息时自动写入公告。手工公告只显示“管理员发布”。',
 '2026-10-16 08:00:00');
