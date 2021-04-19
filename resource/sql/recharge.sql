DROP DATABASE IF EXISTS `recharge_schedule0`;
CREATE DATABASE `recharge_schedule0` DEFAULT CHARACTER SET utf8mb4;
USE `recharge_schedule0`;

CREATE TABLE `task_info_0`
(
    `task_id`      bigint(20)  NOT NULL comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters`   longblob comment '参数',
    `priority`     int(11)     NOT NULL comment '优先级',
    `task_type`    int(11)     NOT NULL comment '任务类型',
    PRIMARY KEY (`task_id`),
    KEY `index_taskinfo_time` (`task_type`, `priority`, `execute_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `task_info_1`
(
    `task_id`      bigint(20)  NOT NULL comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters`   longblob comment '参数',
    `priority`     int(11)     NOT NULL comment '优先级',
    `task_type`    int(11)     NOT NULL comment '任务类型',
    PRIMARY KEY (`task_id`),
    KEY `index_taskinfo_time` (`task_type`, `priority`, `execute_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `task_info_logs_0`
(
    `task_id`      bigint(20)  NOT NULL comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters`   longblob comment '参数',
    `priority`     int(11)     NOT NULL comment '优先级',
    `task_type`    int(11)     NOT NULL comment '任务类型',
    `version`      int(11)     NOT NULL comment '版本号,用乐观锁',
    `status`       int(11) DEFAULT '0' COMMENT '状态 0=初始化状态 1=EXECUTED 2=CANCELLED',
    PRIMARY KEY (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `task_info_logs_1`
(
    `task_id`      bigint(20)  NOT NULL comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters`   longblob comment '参数',
    `priority`     int(11)     NOT NULL comment '优先级',
    `task_type`    int(11)     NOT NULL comment '任务类型',
    `version`      int(11)     NOT NULL comment '版本号,用乐观锁',
    `status`       int(11) DEFAULT '0' comment '状态 0=初始化状态 1=EXECUTED 2=CANCELLED',
    PRIMARY KEY (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP DATABASE IF EXISTS `recharge_schedule1`;
CREATE DATABASE `recharge_schedule1` DEFAULT CHARACTER SET utf8mb4;
USE `recharge_schedule1`;

CREATE TABLE `task_info_0`
(
    `task_id`      bigint(20)  NOT NULL comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters`   longblob comment '参数',
    `priority`     int(11)     NOT NULL comment '优先级',
    `task_type`    int(11)     NOT NULL comment '任务类型',
    PRIMARY KEY (`task_id`),
    KEY `index_taskinfo_time` (`task_type`, `priority`, `execute_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `task_info_1`
(
    `task_id`      bigint(20)  NOT NULL comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters`   longblob comment '参数',
    `priority`     int(11)     NOT NULL comment '优先级',
    `task_type`    int(11)     NOT NULL comment '任务类型',
    PRIMARY KEY (`task_id`),
    KEY `index_taskinfo_time` (`task_type`, `priority`, `execute_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `task_info_logs_0`
(
    `task_id`      bigint(20)  NOT NULL comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters`   longblob comment '参数',
    `priority`     int(11)     NOT NULL comment '优先级',
    `task_type`    int(11)     NOT NULL comment '任务类型',
    `version`      int(11)     NOT NULL comment '版本号,用乐观锁',
    `status`       int(11) DEFAULT '0' comment '状态 0=初始化状态 1=EXECUTED 2=CANCELLED',
    PRIMARY KEY (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `task_info_logs_1`
(
    `task_id`      bigint(20)  NOT NULL comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters`   longblob comment '参数',
    `priority`     int(11)     NOT NULL comment '优先级',
    `task_type`    int(11)     NOT NULL comment '任务类型',
    `version`      int(11)     NOT NULL comment '版本号,用乐观锁',
    `status`       int(11) DEFAULT '0' comment '状态 0=初始化状态 1=EXECUTED 2=CANCELLED',
    PRIMARY KEY (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP DATABASE IF EXISTS `recharge_order_trade`;
CREATE DATABASE `recharge_order_trade` DEFAULT CHARACTER SET utf8mb4;
USE `recharge_order_trade`;

CREATE TABLE `order_trade`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT '相当于流水号',
    `gmt_create`   datetime       DEFAULT NULL,
    `gmt_modified` datetime       DEFAULT NULL,
    `order_status` tinyint(3)     DEFAULT NULL COMMENT ' 1创建 处理中 2 成功 3 失败 9 未确认',
    `user_id`      bigint(20)     DEFAULT NULL COMMENT '下游用户ID',
    `category_id`  tinyint(3)     DEFAULT NULL COMMENT '商品类别（1充值，2兑换）',
    `brand_id`     varchar(50)    DEFAULT NULL COMMENT '商品编号',
    `sales_price`  decimal(11, 2) DEFAULT NULL COMMENT '销售价格',
    `face_price`   double(20, 0)  DEFAULT NULL COMMENT '面值',
    `cost_price`   double(11, 0)  DEFAULT NULL COMMENT '成本价',
    `mobile`       char(11)       DEFAULT NULL COMMENT '手机',
    `trade_no`     bigint(20)     DEFAULT NULL COMMENT '交易订单',
    `order_no`     varchar(100)   DEFAULT NULL COMMENT '订单号',
    `order_time`   datetime       DEFAULT NULL COMMENT '订单时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
