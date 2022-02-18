package com.wsw.actlearn.annotation;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author wsw
 * 事件监听
 */
@Service
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    ActivitiEventType event();
    String activityType();
}
