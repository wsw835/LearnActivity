package com.wsw.actlearn.event;

import com.sun.istack.internal.NotNull;

import org.activiti.engine.delegate.event.ActivitiEvent;

/**
 * @author wsw
 * 事件绑定业务处理
 */
public interface EventHandler {

    /**
     * 事件绑定业务处理
     * @param event 具体事件
     */
    void handleEvent(ActivitiEvent event);

    /**
     * 任务签收
     * @param businessKey 业务key
     * @param optUser 操作人
     */
    default void claimTask(@NotNull String businessKey, @NotNull Object optUser) {

    }
}
