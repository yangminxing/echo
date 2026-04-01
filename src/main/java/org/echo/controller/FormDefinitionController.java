package org.echo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.echo.entity.FormDefinition;
import org.echo.service.FormDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/form-definition")
public class FormDefinitionController {

    @Autowired
    private FormDefinitionService formDefinitionService;

    @GetMapping("/list")
    public List<FormDefinition> list() {
        return formDefinitionService.list();
    }

    @GetMapping("/page")
    public IPage<FormDefinition> page(@RequestParam Integer current,
                                       @RequestParam Integer size) {
        return formDefinitionService.page(current, size);
    }

    @GetMapping("/{id}")
    public FormDefinition getById(@PathVariable String id) {
        return formDefinitionService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody FormDefinition formDefinition) {
        return formDefinitionService.save(formDefinition);
    }

    @PutMapping
    public boolean update(@RequestBody FormDefinition formDefinition) {
        return formDefinitionService.update(formDefinition);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable String id) {
        return formDefinitionService.delete(id);
    }

    @PostMapping("/sync/{id}")
    public boolean syncToPhysicalTable(@PathVariable String id) {
        return formDefinitionService.syncToPhysicalTable(id);
    }
}
