package com.wsw.actlearn.ext;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wsw
 * 任务节点跳转扩展
 */
public class ActivityJumpCommand implements Command<Object> {

    private Logger logger = LogManager.getLogger(ActivityJumpCommand.class);

    /**
     * 业务id
     */
    private String businessKey;
    /**
     * 目标任务节点
     */
    private String targetFlowNodeId;
    /**
     * 当前任务id
     */
    private String curTaskId;

    /**
     * 任务回退时的回退原因
     */
    private String backReason;

    /**
     * 流程变量
     */
    private Map<String, Object> params;

    public ActivityJumpCommand(String businessKey, String curTaskId, String targetFlowNodeId,
                               Map<String, Object> params, String backReason) {
        this.businessKey = businessKey;
        this.targetFlowNodeId = targetFlowNodeId;
        this.curTaskId = curTaskId;
        this.params = params;
        this.backReason = backReason;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        logger.debug("跳转到目标流程节点：{}", targetFlowNodeId);
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(curTaskId);
        if (Objects.isNull(taskEntity)) {
            logger.error("cannot find task of taskId: ", curTaskId);
        }
        ExecutionEntity executionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
        // 获取当前任务的来源任务及来源节点信息
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        List<Task> taskList = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).list();
        taskList.stream().forEach(task -> {
            TaskEntity excludeTask = taskEntityManager.findById(task.getId());
            taskEntityManager.deleteTask(excludeTask, backReason, false, false);
        });
        // 获取要跳转的目标节点
        FlowElement targetFlowElement = process.getFlowElement(targetFlowNodeId);
        executionEntity.setCurrentFlowElement(targetFlowElement);
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        if (params != null) {
            params.forEach((k, v) -> {
                executionEntity.setVariable(k, v);
            });
        }
        agenda.planContinueProcessInCompensation(executionEntity);
        return null;
    }
}
