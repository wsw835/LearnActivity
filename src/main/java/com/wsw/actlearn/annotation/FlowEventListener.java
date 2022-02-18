package com.wsw.actlearn.annotation;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author wsw
 * 流程事件监听
 */
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface FlowEventListener {

    /**
     * 活动事件类型
     * @return
     */
    ActivitiEventType event();

    /**
     * 活动类型
     * @return
     */
    String activityType();

    /**
     * 流程定义key
     * @return
     */
    String flowDefineKey();

    /**
     * 任务key
     * @return
     */
    String taskKey();
}
