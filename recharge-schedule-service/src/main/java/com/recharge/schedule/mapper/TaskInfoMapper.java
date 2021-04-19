package com.recharge.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.schedule.entity.TaskInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Repository
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {
    @Select(value = "select * from task_info where task_type=#{taskType} and priority=#{priority} and execute_time<=#{future,javaType=java.util.Date}")
    List<TaskInfo> queryFutureTime(@Param("taskType") int type, @Param("priority") int priority, @Param("future") Date future);

    @Select("select * from task_info where task_type = #{task_type} and priority = #{priority}")
    List<TaskInfo> queryAll(@Param("task_type") int type, @Param("priority") int priority);
}
