package com.recharge.schedule.utils;

import lombok.Data;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
public class SqlBean {
    /**
     * 数据库数量
     */
    private int dbCount;

    /**
     * task_info表数量
     */
    private int taskInfoCount;

    /**
     * tak_info_logs表后缀集合
     */
    private List<String> taskInfoLogsMonthList;
}
