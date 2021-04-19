package com.recharge.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
@TableName("task_info")
public class TaskInfo implements Serializable {
    private static final long serialVersionUID = -4081668718235817424L;

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
}
