package org.echo.service;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlowableService {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 通过输入流部署流程
     *
     * @param inputStream 流程文件的输入流（如BPMN文件的字节流）
     * @param fileName    流程文件的名称（如process.bpmn）
     * @param processName 部署名称，用于标识此次部署
     * @return Deployment 部署对象，包含部署ID等信息
     */
    public Deployment deployProcess(InputStream inputStream, String fileName, String processName) {
        return processEngine.getRepositoryService()
                .createDeployment()
                .name(processName)
                .addInputStream(fileName, inputStream)
                .deploy();
    }

    /**
     * 从classpath资源部署流程
     *
     * @param resourceName classpath中的资源路径（如 processes/vacation.bpmn）
     * @param processName  部署名称
     * @param category     分类（可选，用于分组管理）
     * @return Deployment 部署对象
     */
    public Deployment deployProcess(String resourceName, String processName, String category) {
        return processEngine.getRepositoryService()
                .createDeployment()
                .name(processName)
                .category(category)
                .addClasspathResource(resourceName)
                .deploy();
    }

    /**
     * 分页查询部署记录
     *
     * @param start 起始索引
     * @param limit 每页数量
     * @return List<Deployment> 部署列表
     */
    public List<Deployment> getDeployments(int start, int limit) {
        DeploymentQuery query = processEngine.getRepositoryService().createDeploymentQuery();
        return query.listPage(start, limit);
    }

    /**
     * 获取部署总数
     *
     * @return long 部署数量
     */
    public long getDeploymentCount() {
        return processEngine.getRepositoryService().createDeploymentQuery().count();
    }

    /**
     * 删除部署记录
     *
     * @param deploymentId 部署ID
     * @param cascade      是否级联删除（true:同时删除流程实例，false:仅删除部署）
     */
    public void deleteDeployment(String deploymentId, boolean cascade) {
        processEngine.getRepositoryService().deleteDeployment(deploymentId, cascade);
    }

    /**
     * 获取流程定义信息
     *
     * @param processDefinitionId 流程定义ID
     * @return ProcessDefinition 流程定义对象
     */
    public ProcessDefinition getProcessDefinition(String processDefinitionId) {
        return processEngine.getRepositoryService().getProcessDefinition(processDefinitionId);
    }

    /**
     * 获取流程定义的BPMN模型
     *
     * @param processDefinitionId 流程定义ID
     * @return BpmnModel BPMN模型对象，可用于获取流程的所有节点、连线等信息
     */
    public BpmnModel getBpmnModel(String processDefinitionId) {
        return processEngine.getRepositoryService().getBpmnModel(processDefinitionId);
    }

    /**
     * 分页查询流程定义
     *
     * @param start 起始索引
     * @param limit 每页数量
     * @return List<ProcessDefinition> 流程定义列表
     */
    public List<ProcessDefinition> getProcessDefinitions(int start, int limit) {
        return processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .listPage(start, limit);
    }

    /**
     * 根据流程定义Key启动流程实例
     *
     * @param processDefinitionKey 流程定义的key（流程文件的id）
     * @param variables            流程变量（如 assignee: "zhangsan"）
     * @return ProcessInstance 流程实例对象
     */
    public ProcessInstance startProcess(String processDefinitionKey, Map<String, Object> variables) {
        return processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, variables);
    }

    /**
     * 根据流程定义Key启动流程实例（带业务键）
     *
     * @param processDefinitionKey 流程定义的key
     * @param businessKey          业务键（用于与业务系统关联，如订单号）
     * @param variables            流程变量
     * @return ProcessInstance 流程实例对象
     */
    public ProcessInstance startProcess(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        return processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    /**
     * 分页查询运行中的流程实例
     *
     * @param start 起始索引
     * @param limit 每页数量
     * @return List<ProcessInstance> 流程实例列表
     */
    public List<ProcessInstance> getProcessInstances(int start, int limit) {
        return processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .listPage(start, limit);
    }

    /**
     * 获取运行中的流程实例总数
     *
     * @return long 流程实例数量
     */
    public long getProcessInstanceCount() {
        return processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .count();
    }

    /**
     * 根据流程实例ID查询流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return ProcessInstance 流程实例对象
     */
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }

    /**
     * 删除运行中的流程实例
     *
     * @param processInstanceId 流程实例ID
     * @param reason            删除原因
     */
    public void deleteProcessInstance(String processInstanceId, String reason) {
        processEngine.getRuntimeService()
                .deleteProcessInstance(processInstanceId, reason);
    }

    /**
     * 获取流程实例的所有变量
     *
     * @param processInstanceId 流程实例ID
     * @return Map<String, Object> 变量集合
     */
    public Map<String, Object> getProcessVariables(String processInstanceId) {
        return processEngine.getRuntimeService()
                .getVariables(processInstanceId);
    }

    /**
     * 设置流程实例的单个变量
     *
     * @param processInstanceId 流程实例ID
     * @param variableName      变量名
     * @param value             变量值
     */
    public void setProcessVariable(String processInstanceId, String variableName, Object value) {
        processEngine.getRuntimeService()
                .setVariable(processInstanceId, variableName, value);
    }

    /**
     * 根据办理人分页查询任务
     *
     * @param assignee 办理人（用户名）
     * @param start    起始索引
     * @param limit    每页数量
     * @return List<Task> 任务列表
     */
    public List<Task> getTasks(String assignee, int start, int limit) {
        TaskQuery query = processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee(assignee);
        return query.listPage(start, limit);
    }

    /**
     * 根据候选用户分页查询任务
     *
     * @param candidateUser 候选用户
     * @param start         起始索引
     * @param limit         每页数量
     * @return List<Task> 任务列表
     */
    public List<Task> getTasksByCandidateUser(String candidateUser, int start, int limit) {
        TaskQuery query = processEngine.getTaskService()
                .createTaskQuery()
                .taskCandidateUser(candidateUser);
        return query.listPage(start, limit);
    }

    /**
     * 获取指定办理人的任务总数
     *
     * @param assignee 办理人
     * @return long 任务数量
     */
    public long getTaskCount(String assignee) {
        return processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee(assignee)
                .count();
    }

    /**
     * 根据任务ID查询任务
     *
     * @param taskId 任务ID
     * @return Task 任务对象
     */
    public Task getTask(String taskId) {
        return processEngine.getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
    }

    /**
     * 完成任务（不带变量）
     *
     * @param taskId 任务ID
     */
    public void completeTask(String taskId) {
        processEngine.getTaskService().complete(taskId);
    }

    /**
     * 完成任务（带变量）
     *
     * @param taskId    任务ID
     * @param variables 流程变量（会设置到流程实例中）
     */
    public void completeTask(String taskId, Map<String, Object> variables) {
        processEngine.getTaskService().complete(taskId, variables);
    }

    /**
     * 指定任务的办理人
     *
     * @param taskId   任务ID
     * @param assignee 新的办理人
     */
    public void assignTask(String taskId, String assignee) {
        processEngine.getTaskService().setAssignee(taskId, assignee);
    }

    /**
     * 获取任务的所有变量
     *
     * @param taskId 任务ID
     * @return Map<String, Object> 变量集合
     */
    public Map<String, Object> getTaskVariables(String taskId) {
        return processEngine.getTaskService().getVariables(taskId);
    }

    /**
     * 设置任务的单个变量
     *
     * @param taskId      任务ID
     * @param variableName 变量名
     * @param value        变量值
     */
    public void setTaskVariable(String taskId, String variableName, Object value) {
        processEngine.getTaskService().setVariable(taskId, variableName, value);
    }

    /**
     * 分页查询历史流程实例
     *
     * @param start 起始索引
     * @param limit 每页数量
     * @return List<HistoricProcessInstance> 历史流程实例列表
     */
    public List<HistoricProcessInstance> getHistoricProcessInstances(int start, int limit) {
        return processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .listPage(start, limit);
    }

    /**
     * 根据流程实例ID查询历史流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return HistoricProcessInstance 历史流程实例对象
     */
    public HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        return processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }

    /**
     * 根据流程定义Key搜索历史流程实例
     *
     * @param processDefinitionKey 流程定义Key
     * @param start                起始索引
     * @param limit                每页数量
     * @return List<HistoricProcessInstance> 历史流程实例列表
     */
    public List<HistoricProcessInstance> searchHistoricProcessInstances(String processDefinitionKey, int start, int limit) {
        return processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .listPage(start, limit);
    }

    /**
     * 获取Flowable系统信息
     *
     * @return Map<String, Object> 包含版本、数据库类型、部署数量、流程定义数量、流程实例数量等
     */
    public Map<String, Object> getFlowableInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", processEngine.getName());
        info.put("databaseType", processEngine.getManagementService()
                .getProperties().get("databaseType"));
        info.put("deploymentCount", getDeploymentCount());
        info.put("processDefinitionCount", processEngine.getRepositoryService()
                .createProcessDefinitionQuery().count());
        info.put("processInstanceCount", getProcessInstanceCount());
        return info;
    }
}