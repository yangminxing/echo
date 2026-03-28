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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlowableService {

    @Autowired
    private ProcessEngine processEngine;

    public Deployment deployProcess(InputStream inputStream, String fileName, String processName) {
        return processEngine.getRepositoryService()
                .createDeployment()
                .name(processName)
                .addInputStream(fileName, inputStream)
                .deploy();
    }

    public Deployment deployProcess(String resourceName, String processName, String category) {
        return processEngine.getRepositoryService()
                .createDeployment()
                .name(processName)
                .category(category)
                .addClasspathResource(resourceName)
                .deploy();
    }

    public List<Deployment> getDeployments(int start, int limit) {
        DeploymentQuery query = processEngine.getRepositoryService().createDeploymentQuery();
        return query.listPage(start, limit);
    }

    public long getDeploymentCount() {
        return processEngine.getRepositoryService().createDeploymentQuery().count();
    }

    public void deleteDeployment(String deploymentId, boolean cascade) {
        processEngine.getRepositoryService().deleteDeployment(deploymentId, cascade);
    }

    public ProcessDefinition getProcessDefinition(String processDefinitionId) {
        return processEngine.getRepositoryService().getProcessDefinition(processDefinitionId);
    }

    public BpmnModel getBpmnModel(String processDefinitionId) {
        return processEngine.getRepositoryService().getBpmnModel(processDefinitionId);
    }

    public List<ProcessDefinition> getProcessDefinitions(int start, int limit) {
        return processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .listPage(start, limit);
    }

    public ProcessInstance startProcess(String processDefinitionKey, Map<String, Object> variables) {
        return processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, variables);
    }

    public ProcessInstance startProcess(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        return processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    public List<ProcessInstance> getProcessInstances(int start, int limit) {
        return processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .listPage(start, limit);
    }

    public long getProcessInstanceCount() {
        return processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .count();
    }

    public ProcessInstance getProcessInstance(String processInstanceId) {
        return processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }

    public void deleteProcessInstance(String processInstanceId, String reason) {
        processEngine.getRuntimeService()
                .deleteProcessInstance(processInstanceId, reason);
    }

    public Map<String, Object> getProcessVariables(String processInstanceId) {
        return processEngine.getRuntimeService()
                .getVariables(processInstanceId);
    }

    public void setProcessVariable(String processInstanceId, String variableName, Object value) {
        processEngine.getRuntimeService()
                .setVariable(processInstanceId, variableName, value);
    }

    public List<Task> getTasks(String assignee, int start, int limit) {
        TaskQuery query = processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee(assignee);
        return query.listPage(start, limit);
    }

    public List<Task> getTasksByCandidateUser(String candidateUser, int start, int limit) {
        TaskQuery query = processEngine.getTaskService()
                .createTaskQuery()
                .taskCandidateUser(candidateUser);
        return query.listPage(start, limit);
    }

    public long getTaskCount(String assignee) {
        return processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee(assignee)
                .count();
    }

    public Task getTask(String taskId) {
        return processEngine.getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
    }

    public void completeTask(String taskId) {
        processEngine.getTaskService().complete(taskId);
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        processEngine.getTaskService().complete(taskId, variables);
    }

    public void assignTask(String taskId, String assignee) {
        processEngine.getTaskService().setAssignee(taskId, assignee);
    }

    public Map<String, Object> getTaskVariables(String taskId) {
        return processEngine.getTaskService().getVariables(taskId);
    }

    public void setTaskVariable(String taskId, String variableName, Object value) {
        processEngine.getTaskService().setVariable(taskId, variableName, value);
    }

    public List<HistoricProcessInstance> getHistoricProcessInstances(int start, int limit) {
        return processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .listPage(start, limit);
    }

    public HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        return processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }

    public List<HistoricProcessInstance> searchHistoricProcessInstances(String processDefinitionKey, int start, int limit) {
        return processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .listPage(start, limit);
    }

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