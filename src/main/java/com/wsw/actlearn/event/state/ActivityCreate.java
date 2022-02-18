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
 * 流程活动创建
 */
@EventListener(event = ActivitiEventType.ACTIVITY_STARTED, activityType = Constants.ACTIVITY)
public class ActivityCreate implements EventHandler {

    private Logger logger = LogManager.getLogger(ActivityCreate.class);

    @Override
    public void handleEvent(ActivitiEvent event) {
        ActivitiEventImpl entity = (ActivitiEventImpl) event;
        logger.debug("活动创建 : activityType = {}", entity.getType().name());
    }

}
