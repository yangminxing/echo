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

    @GetMapping("/info")
    public Map<String, Object> getFlowableInfo() {
        return flowableService.getFlowableInfo();
    }

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

    @GetMapping("/deployments")
    public Map<String, Object> getDeployments(@RequestParam(defaultValue = "0") int start,
                                              @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Deployment> deployments = flowableService.getDeployments(start, limit);
        result.put("data", deployments);
        result.put("total", flowableService.getDeploymentCount());
        return result;
    }

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

    @GetMapping("/process-definitions")
    public Map<String, Object> getProcessDefinitions(@RequestParam(defaultValue = "0") int start,
                                                     @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<ProcessDefinition> definitions = flowableService.getProcessDefinitions(start, limit);
        result.put("data", definitions);
        return result;
    }

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

    @GetMapping("/process-instances")
    public Map<String, Object> getProcessInstances(@RequestParam(defaultValue = "0") int start,
                                                    @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<ProcessInstance> instances = flowableService.getProcessInstances(start, limit);
        result.put("data", instances);
        result.put("total", flowableService.getProcessInstanceCount());
        return result;
    }

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

    @GetMapping("/tasks/candidate/{candidateUser}")
    public Map<String, Object> getTasksByCandidateUser(@PathVariable String candidateUser,
                                                        @RequestParam(defaultValue = "0") int start,
                                                        @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = flowableService.getTasksByCandidateUser(candidateUser, start, limit);
        result.put("data", tasks);
        return result;
    }

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

    @GetMapping("/history/process-instances")
    public Map<String, Object> getHistoricProcessInstances(@RequestParam(defaultValue = "0") int start,
                                                            @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<HistoricProcessInstance> instances = flowableService.getHistoricProcessInstances(start, limit);
        result.put("data", instances);
        return result;
    }

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