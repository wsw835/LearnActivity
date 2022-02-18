package com.wsw.actlearn.entity.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wsw
 * 任务信息展示实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskOptionVo implements Serializable {

    /**
     * 任务定义key
     */
    private String taskKey;

    /**
     * 任务名称
     */
    private String taskName;

}
