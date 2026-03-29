package org.echo.controller;

import org.echo.service.FlowableService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flowable")
public class FlowableController {

    @Autowired
    private FlowableService flowableService;

    /**
     * 获取Flowable系统信息
     *
     * @return 包含Flowable版本、数据库类型、部署数量、流程定义数量、流程实例数量等
     */
    @GetMapping("/info")
    public Map<String, Object> getFlowableInfo() {
        return flowableService.getFlowableInfo();
    }

    /**
     * 从classpath资源部署流程
     *
     * @param resourceName classpath中的资源路径（如 processes/vacation.bpmn）
     * @param processName  部署名称
     * @param category     分类（可选）
     * @return 部署结果，包含deploymentId和deploymentName
     */
    @PostMapping("/deploy/classpath")
    public Map<String, Object> deployFromClasspath(@RequestParam String resourceName,
                                                    @RequestParam String processName,
                                                    @RequestParam(required = false) String category) {
        Map<String, Object> result = new HashMap<>();
        try {
            Deployment deployment = flowableService.deployProcess(resourceName, processName, category);
            result.put("success", true);
            result.put("deploymentId", deployment.getId());
            result.put("deploymentName", deployment.getName());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 通过文件上传部署流程
     *
     * @param file        上传的流程文件（BPMN格式）
     * @param processName 部署名称
     * @return 部署结果，包含deploymentId和deploymentName
     */
    @PostMapping("/deploy/file")
    public Map<String, Object> deployFromFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam String processName) {
        Map<String, Object> result = new HashMap<>();
        try {
            Deployment deployment = flowableService.deployProcess(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    processName
            );
            result.put("success", true);
            result.put("deploymentId", deployment.getId());
            result.put("deploymentName", deployment.getName());
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 分页查询部署记录
     *
     * @param start 起始索引，默认0
     * @param limit 每页数量，默认20
     * @return 包含data（部署列表）和total（总数）
     */
    @GetMapping("/deployments")
    public Map<String, Object> getDeployments(@RequestParam(defaultValue = "0") int start,
                                              @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Deployment> deployments = flowableService.getDeployments(start, limit);
        result.put("data", deployments);
        result.put("total", flowableService.getDeploymentCount());
        return result;
    }

    /**
     * 删除部署记录
     *
     * @param deploymentId 部署ID
     * @param cascade      是否级联删除（true:同时删除流程实例）
     * @return 删除操作结果
     */
    @DeleteMapping("/deployments/{deploymentId}")
    public Map<String, Object> deleteDeployment(@PathVariable String deploymentId,
                                                 @RequestParam(defaultValue = "true") boolean cascade) {
        Map<String, Object> result = new HashMap<>();
        try {
            flowableService.deleteDeployment(deploymentId, cascade);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 分页查询流程定义
     *
     * @param start 起始索引，默认0
     * @param limit 每页数量，默认20
     * @return 包含data（流程定义列表）
     */
    @GetMapping("/process-definitions")
    public Map<String, Object> getProcessDefinitions(@RequestParam(defaultValue = "0") int start,
                                                     @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<ProcessDefinition> definitions = flowableService.getProcessDefinitions(start, limit);
        result.put("data", definitions);
        return result;
    }

    /**
     * 获取指定流程定义信息
     *
     * @param processDefinitionId 流程定义ID
     * @return 流程定义详情
     */
    @GetMapping("/process-definitions/{processDefinitionId}")
    public Map<String, Object> getProcessDefinition(@PathVariable String processDefinitionId) {
        Map<String, Object> result = new HashMap<>();
        ProcessDefinition definition = flowableService.getProcessDefinition(processDefinitionId);
        if (definition != null) {
            result.put("data", definition);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "Process definition not found");
        }
        return result;
    }

    /**
     * 获取流程定义的BPMN模型（包含所有节点、连线等信息）
     *
     * @param processDefinitionId 流程定义ID
     * @return BPMN模型详情
     */
    @GetMapping("/process-definitions/{processDefinitionId}/bpmn")
    public Map<String, Object> getBpmnModel(@PathVariable String processDefinitionId) {
        Map<String, Object> result = new HashMap<>();
        try {
            BpmnModel bpmnModel = flowableService.getBpmnModel(processDefinitionId);
            result.put("success", true);
            result.put("data", bpmnModel);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 启动流程实例
     *
     * @param processDefinitionKey 流程定义的key
     * @param variables           流程变量（可选）
     * @return 启动结果，包含processInstanceId、processDefinitionKey、businessKey
     */
    @PostMapping("/process-instances/start")
    public Map<String, Object> startProcess(@RequestParam String processDefinitionKey,
                                            @RequestBody(required = false) Map<String, Object> variables) {
        Map<String, Object> result = new HashMap<>();
        try {
            ProcessInstance processInstance;
            if (variables != null && !variables.isEmpty()) {
                processInstance = flowableService.startProcess(processDefinitionKey, variables);
            } else {
                processInstance = flowableService.startProcess(processDefinitionKey, new HashMap<>());
            }
            result.put("success", true);
            result.put("processInstanceId", processInstance.getId());
            result.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
            result.put("businessKey", processInstance.getBusinessKey());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 分页查询运行中的流程实例
     *
     * @param start 起始索引，默认0
     * @param limit 每页数量，默认20
     * @return 包含data（流程实例列表）和total（总数）
     */
    @GetMapping("/process-instances")
    public Map<String, Object> getProcessInstances(@RequestParam(defaultValue = "0") int start,
                                                    @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<ProcessInstance> instances = flowableService.getProcessInstances(start, limit);
        result.put("data", instances);
        result.put("total", flowableService.getProcessInstanceCount());
        return result;
    }

    /**
     * 获取指定流程实例详情
     *
     * @param processInstanceId 流程实例ID
     * @return 流程实例详情
     */
    @GetMapping("/process-instances/{processInstanceId}")
    public Map<String, Object> getProcessInstance(@PathVariable String processInstanceId) {
        Map<String, Object> result = new HashMap<>();
        ProcessInstance instance = flowableService.getProcessInstance(processInstanceId);
        if (instance != null) {
            result.put("data", instance);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "Process instance not found");
        }
        return result;
    }

    /**
     * 删除运行中的流程实例
     *
     * @param processInstanceId 流程实例ID
     * @param reason            删除原因（可选，默认"Cancelled by user"）
     * @return 删除操作结果
     */
    @DeleteMapping("/process-instances/{processInstanceId}")
    public Map<String, Object> deleteProcessInstance(@PathVariable String processInstanceId,
                                                      @RequestParam(required = false) String reason) {
        Map<String, Object> result = new HashMap<>();
        try {
            flowableService.deleteProcessInstance(processInstanceId, reason != null ? reason : "Cancelled by user");
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取流程实例的所有变量
     *
     * @param processInstanceId 流程实例ID
     * @return 变量集合
     */
    @GetMapping("/process-instances/{processInstanceId}/variables")
    public Map<String, Object> getProcessVariables(@PathVariable String processInstanceId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> variables = flowableService.getProcessVariables(processInstanceId);
            result.put("success", true);
            result.put("data", variables);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 设置流程实例的单个变量
     *
     * @param processInstanceId 流程实例ID
     * @param variableName      变量名
     * @param value             变量值
     * @return 设置结果
     */
    @PutMapping("/process-instances/{processInstanceId}/variables")
    public Map<String, Object> setProcessVariable(@PathVariable String processInstanceId,
                                                   @RequestParam String variableName,
                                                   @RequestParam Object value) {
        Map<String, Object> result = new HashMap<>();
        try {
            flowableService.setProcessVariable(processInstanceId, variableName, value);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 根据办理人分页查询任务
     *
     * @param assignee 办理人（用户名）
     * @param start    起始索引，默认0
     * @param limit    每页数量，默认20
     * @return 包含data（任务列表）和total（总数）
     */
    @GetMapping("/tasks")
    public Map<String, Object> getTasks(@RequestParam String assignee,
                                         @RequestParam(defaultValue = "0") int start,
                                         @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = flowableService.getTasks(assignee, start, limit);
        result.put("data", tasks);
        result.put("total", flowableService.getTaskCount(assignee));
        return result;
    }

    /**
     * 根据候选用户分页查询任务
     *
     * @param candidateUser 候选用户
     * @param start         起始索引，默认0
     * @param limit         每页数量，默认20
     * @return 包含data（任务列表）
     */
    @GetMapping("/tasks/candidate/{candidateUser}")
    public Map<String, Object> getTasksByCandidateUser(@PathVariable String candidateUser,
                                                        @RequestParam(defaultValue = "0") int start,
                                                        @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = flowableService.getTasksByCandidateUser(candidateUser, start, limit);
        result.put("data", tasks);
        return result;
    }

    /**
     * 获取指定任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    @GetMapping("/tasks/{taskId}")
    public Map<String, Object> getTask(@PathVariable String taskId) {
        Map<String, Object> result = new HashMap<>();
        Task task = flowableService.getTask(taskId);
        if (task != null) {
            result.put("data", task);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "Task not found");
        }
        return result;
    }

    /**
     * 完成任务
     *
     * @param taskId    任务ID
     * @param variables 流程变量（可选，会设置到流程实例中）
     * @return 操作结果
     */
    @PostMapping("/tasks/{taskId}/complete")
    public Map<String, Object> completeTask(@PathVariable String taskId,
                                             @RequestBody(required = false) Map<String, Object> variables) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (variables != null && !variables.isEmpty()) {
                flowableService.completeTask(taskId, variables);
            } else {
                flowableService.completeTask(taskId);
            }
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 指定任务的办理人
     *
     * @param taskId   任务ID
     * @param assignee 新的办理人
     * @return 操作结果
     */
    @PutMapping("/tasks/{taskId}/assignee")
    public Map<String, Object> assignTask(@PathVariable String taskId,
                                            @RequestParam String assignee) {
        Map<String, Object> result = new HashMap<>();
        try {
            flowableService.assignTask(taskId, assignee);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取任务的所有变量
     *
     * @param taskId 任务ID
     * @return 变量集合
     */
    @GetMapping("/tasks/{taskId}/variables")
    public Map<String, Object> getTaskVariables(@PathVariable String taskId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> variables = flowableService.getTaskVariables(taskId);
            result.put("success", true);
            result.put("data", variables);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 设置任务的单个变量
     *
     * @param taskId      任务ID
     * @param variableName 变量名
     * @param value        变量值
     * @return 操作结果
     */
    @PutMapping("/tasks/{taskId}/variables")
    public Map<String, Object> setTaskVariable(@PathVariable String taskId,
                                                 @RequestParam String variableName,
                                                 @RequestParam Object value) {
        Map<String, Object> result = new HashMap<>();
        try {
            flowableService.setTaskVariable(taskId, variableName, value);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 分页查询历史流程实例
     *
     * @param start 起始索引，默认0
     * @param limit 每页数量，默认20
     * @return 包含data（历史流程实例列表）
     */
    @GetMapping("/history/process-instances")
    public Map<String, Object> getHistoricProcessInstances(@RequestParam(defaultValue = "0") int start,
                                                            @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<HistoricProcessInstance> instances = flowableService.getHistoricProcessInstances(start, limit);
        result.put("data", instances);
        return result;
    }

    /**
     * 获取指定历史流程实例详情
     *
     * @param processInstanceId 流程实例ID
     * @return 历史流程实例详情
     */
    @GetMapping("/history/process-instances/{processInstanceId}")
    public Map<String, Object> getHistoricProcessInstance(@PathVariable String processInstanceId) {
        Map<String, Object> result = new HashMap<>();
        HistoricProcessInstance instance = flowableService.getHistoricProcessInstance(processInstanceId);
        if (instance != null) {
            result.put("data", instance);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "Historic process instance not found");
        }
        return result;
    }

    /**
     * 根据流程定义Key搜索历史流程实例
     *
     * @param processDefinitionKey 流程定义Key
     * @param start                起始索引，默认0
     * @param limit                每页数量，默认20
     * @return 包含data（历史流程实例列表）
     */
    @GetMapping("/history/process-instances/search")
    public Map<String, Object> searchHistoricProcessInstances(@RequestParam String processDefinitionKey,
                                                               @RequestParam(defaultValue = "0") int start,
                                                               @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<HistoricProcessInstance> instances = flowableService.searchHistoricProcessInstances(
                processDefinitionKey, start, limit
        );
        result.put("data", instances);
        return result;
    }
}