package com.recharge.common.entity;

/**
 * <p>
 * 任务状态常量类-task状态
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public class Constants {
    public static final String JI_SU_API = "jisuapi";
    public static final String JU_HE_API = "juheapi";
    public static final String JI_SU_ORDER = "order_jisu";

    /**
     * 供应商排除key
     */
    public static final String EXCLUDE_SUPPLIER = "exclude_supplier";

    /**
     * 订单检查集合key
     */
    public static final String ORDER_CHECKED = "order_checked";

    /**
     * 调度常量
     */
    public static final String NEXT_SCHEDULE_TIME = "nextScheduleTime";

    /**
     * job系统选主注册目录
     */
    public static final String JOB_LEADER_PATH = "/recharge/job_master";

    /**
     * 系统选主注册目录
     */
    public static final String SCHEDULE_LEADER_PATH = "/recharge/schedule_master";

    /**
     * 未来数据队列
     */
    public static String FUTURE = "future_";

    /**
     * 主题
     */
    public static String TOPIC = "topic_";

    /**
     * 初始化状态
     */
    public static final int SCHEDULED = 0;

    /**
     * 已执行状态
     */
    public static final int EXECUTED = 1;

    /**
     * 已取消状态
     */
    public static final int CANCELLED = 2;

    /**
     * Redis数据库
     */
    public static String DB_CACHE = "db_cache";
}
