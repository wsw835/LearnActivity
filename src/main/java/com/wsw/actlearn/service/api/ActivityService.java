package com.wsw.actlearn.service.api;

import com.wsw.actlearn.entity.vo.ProcessDefOptionVo;

import java.util.List;

/**
 * @author wsw
 * 流程业务相关
 */
public interface ActivityService {

    /**
     * 获得所有流程定义信息
     * @return 所有的流程定义信息
     */
    List<ProcessDefOptionVo> listAllProcessDef();

    /**
     * 根据流程定义KEY获得所有任务信息
     * @param processDefKey 流程定义KEY
     * @return 所有的任务信息
     */
    List<ProcessDefOptionVo> listAllTaskByPdKey(String processDefKey);


}
