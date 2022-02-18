package com.wsw.actlearn.config.listener;

import com.wsw.actlearn.annotation.EventListener;
import com.wsw.actlearn.annotation.FlowEventListener;
import com.wsw.actlearn.constants.Constants;
import com.wsw.actlearn.event.EventHandler;
import com.wsw.actlearn.util.ApplicationContextHolder;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wsw
 * 全局的事件监听器
 */
@Component
public class GlobalActEventListener implements ActivitiEventListener {

    private static final Logger log = LoggerFactory.getLogger(GlobalActEventListener.class);

    @Override
    public void onEvent(ActivitiEvent event) {
        String eventType = event.getType().name();
        if (eventType.startsWith(Constants.ENGINE_)) {
            log.debug("引擎启动...");
            return;
        }
        ApplicationContext wac = ApplicationContextHolder.getApplicationContext();
        if (wac == null) {
            return;
        }
        // 对应每个流程状态下，执行每个任务节点、流程节点对应事件下的业务逻辑实现（EventHandler -> 间入业务操作执行）
        // 找到对应的事件监听注解标注的事件，按流程分支事件或者流程主干事件处理业务逻辑
        Map eventListener = wac.getBeansWithAnnotation(EventListener.class);
        Map flowEventListener = wac.getBeansWithAnnotation(FlowEventListener.class);
        // 事件的类型是针对流程实体的走这里，获取历史活动实例实体信息，执行对应业务操作
        if (eventType.startsWith(Constants.ENTITY_)) {
            ActivitiEntityEventImpl eventImpl = (ActivitiEntityEventImpl) event;
            if (eventImpl.getEntity() instanceof HistoricActivityInstanceEntityImpl) {
                HistoricActivityInstanceEntityImpl en = (HistoricActivityInstanceEntityImpl) eventImpl.getEntity();
                String activityType = en.getActivityType();
                eventListener.forEach((k, v) -> {
                    EventListener eventListenerAno = AopUtils.getTargetClass(v).getAnnotation(EventListener.class);
                    if (eventListenerAno.event() == event.getType() && activityType.equals(eventListenerAno.activityType())) {
                        ((EventHandler) v).handleEvent(event);
                    }
                });
                flowEventListener.forEach((k, v) -> {
                    FlowEventListener eventListenerAno = AopUtils.getTargetClass(v).getAnnotation(FlowEventListener.class);
                    String pdId = event.getProcessDefinitionId();
                    String pdKey = StringUtils.isBlank(pdId) ? "" : pdId.split(":")[0];
                    if (eventListenerAno.event() == event.getType() && pdKey.equals(eventListenerAno.flowDefineKey())
                        && activityType.equals(eventListenerAno.activityType())) {
                        ((EventHandler) v).handleEvent(event);
                    }
                });
            }
            return;
        }
        eventListener.forEach((k, v) -> {
            EventListener eventListenerAno = AopUtils.getTargetClass(v).getAnnotation(EventListener.class);
            if (eventListenerAno.event() == event.getType()) {
                ((EventHandler) v).handleEvent(event);
            }
        });
        flowEventListener.forEach((k, v) -> {
            FlowEventListener flowEventListenerAno = AopUtils.getTargetClass(v).getAnnotation(FlowEventListener.class);
            String pdId = event.getProcessDefinitionId();
            String pdKey = StringUtils.isBlank(pdId) ? "" : pdId.split(":")[0];
            boolean taskKey = true;
            if (!StringUtils.isBlank(flowEventListenerAno.taskKey())
                && event instanceof ActivitiEntityEventImpl) {
                Object task = ((ActivitiEntityEventImpl) event).getEntity();
                if (task instanceof TaskEntity) {
                    String tdk = ((TaskEntity) task).getTaskDefinitionKey();
                    if (tdk.equals(flowEventListenerAno.taskKey())) {
                        taskKey = true;
                    } else {
                        taskKey = false;
                    }
                }
            }
            if (flowEventListenerAno.event() == event.getType() && pdKey.equals(flowEventListenerAno.flowDefineKey()) && taskKey) {
                ((EventHandler) v).handleEvent(event);
            }
        });
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
