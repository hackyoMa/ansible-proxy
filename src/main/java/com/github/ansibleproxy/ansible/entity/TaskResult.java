package com.github.ansibleproxy.ansible.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 任务结果
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult implements Serializable {

    /**
     * 任务开始时间
     */
    private Long startTime;
    /**
     * 任务结束时间
     */
    private Long endTime;
    /**
     * 是否执行成功
     */
    private Boolean success;
    /**
     * 任务标准输出
     */
    private List<String> stdout;
    /**
     * 任务标准错误
     */
    private List<String> stderr;

}
