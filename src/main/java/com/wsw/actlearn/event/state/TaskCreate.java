package com.wsw.actlearn.event.state;

import com.wsw.actlearn.annotation.EventListener;
import com.wsw.actlearn.constants.Constants;
import com.wsw.actlearn.event.EventHandler;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author wsw
 * 任务创建
 */
@EventListener(event = ActivitiEventType.TASK_CREATED, activityType = Constants.USER_TASK)
public class TaskCreate implements EventHandler {

    private Logger logger = LogManager.getLogger(TaskCreate.class);

    @Override
    public void handleEvent(ActivitiEvent event) {
        ActivitiEventImpl entity = (ActivitiEventImpl) event;
        logger.debug("任务创建 : taskType = {}", entity.getType().name());
    }

}
