package com.wsw.actlearn.event.state;

import com.wsw.actlearn.annotation.EventListener;
import com.wsw.actlearn.constants.Constants;
import com.wsw.actlearn.event.EventHandler;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;

/**
 * @author wsw
 * 包含网关
 */
@EventListener(event = ActivitiEventType.ENTITY_CREATED, activityType = Constants.INCLUSIVE_GATEWAY)
public class InclusiveGateway implements EventHandler {

    @Override
    public void handleEvent(ActivitiEvent event) {

    }

}
