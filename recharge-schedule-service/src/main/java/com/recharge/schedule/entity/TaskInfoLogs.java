package com.recharge.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
@TableName("task_info_logs")
public class TaskInfoLogs implements Serializable {
    private static final long serialVersionUID = 5817248527508163697L;

    @TableId(type = IdType.ID_WORKER)
    private Long taskId;

    @TableField
    private Date executeTime;

    @TableField
    private Integer priority;

    @TableField
    private Integer taskType;

    @TableField
    private byte[] parameters;

    @Version
    private Integer version;

    @TableField
    private Integer status;
}
