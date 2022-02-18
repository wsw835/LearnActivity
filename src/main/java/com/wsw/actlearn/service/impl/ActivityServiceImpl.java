package com.wsw.actlearn.service.impl;

import com.wsw.actlearn.entity.vo.ProcessDefOptionVo;
import com.wsw.actlearn.service.api.ActivityService;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * @author wsw
 * 流程业务实现
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ActivityServiceImpl implements ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Resource
    private RepositoryService repositoryService;

    @Override
    public List<ProcessDefOptionVo> listAllProcessDef() {
        // 根据流程定义版本升序
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
            .orderByProcessDefinitionVersion()
            .asc()
            .list();
        Map<String, ProcessDefinition> map = new LinkedHashMap<>();
        // 遍历集合，根据key来覆盖前面的值，来保证最新的key覆盖前面所有老的key的值
        for (ProcessDefinition pd : list) {
            map.put(pd.getKey(), pd);
        }
        return map.values()
            .stream()
            .map(pd -> new ProcessDefOptionVo(pd.getKey(), pd.getName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<ProcessDefOptionVo> listAllTaskByPdKey(String processDefKey) {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(processDefKey)
            .orderByProcessDefinitionVersion()
            .desc()
            .list();
        //todo 传入processDefKey未获取到对应流程定义时异常处理
        String precessDefId = list.get(0).getId();
        BpmnModel model = repositoryService.getBpmnModel(precessDefId);
        if (model == null) {
            return new ArrayList<>();
        }
        //找到所有类型为用户任务的工作流元素
        Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
        return flowElements.stream()
            .filter(fe -> fe instanceof UserTask)
            .map(element -> new ProcessDefOptionVo(element.getId(), element.getName()))
            .collect(Collectors.toList());
    }


}
