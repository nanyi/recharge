<#list 0..sqlbean.dbCount-1 as db_no>
DROP database if exists `recharge_schedule${db_no}`;
CREATE DATABASE `recharge_schedule${db_no}` DEFAULT CHARACTER SET utf8mb4;
USE `recharge_schedule${db_no}`;

<#list 0..sqlbean.taskInfoCount-1 as taskinfo_no>
CREATE TABLE `task_info_${taskinfo_no}` (
    `task_id` bigint(20) NOT NULL    comment '任务id',
    `execute_time` datetime(3) NOT NULL comment '执行时间',
    `parameters` longblob   comment '参数',
    `priority` int(11) NOT NULL      comment '优先级',
    `task_type` int(11) NOT NULL     comment '任务类型',
    PRIMARY KEY (`task_id`),
    KEY `index_taskinfo_time` (`task_type`,`priority`,`execute_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

</#list>

<#list sqlbean.taskInfoLogsMonthList as month>
CREATE TABLE `task_info_logs_${month}` (
    `task_id` bigint(20) NOT NULL COMMENT '任务id',
    `execute_time` datetime(3) COMMENT '执行时间',
    `parameters` longblob  COMMENT '参数',
    `priority` int(11) NOT NULL COMMENT '优先级',
    `task_type` int(11) NOT NULL COMMENT '任务类型',
    `version` int(11) NOT NULL COMMENT '版本号,用乐观锁',
    `status` int(11) DEFAULT '0' COMMENT '状态 0=初始化状态 1=EXECUTED 2=CANCELLED',
    PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

</#list>
</#list>
