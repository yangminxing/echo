package org.echo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.echo.entity.TableDefinition;
import org.echo.entity.TableFieldDef;
import org.echo.service.TableDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/table-definition")
public class TableDefinitionController {

    @Autowired
    private TableDefinitionService tableDefinitionService;

    @GetMapping("/list")
    public List<TableDefinition> list() {
        return tableDefinitionService.list();
    }

    @GetMapping("/page")
    public IPage<TableDefinition> page(@RequestParam Integer current,
                                        @RequestParam Integer size) {
        return tableDefinitionService.page(current, size);
    }

    @GetMapping("/{tableName}")
    public TableDefinition getByTableName(@PathVariable String tableName) {
        return tableDefinitionService.getByTableName(tableName);
    }

    @PostMapping
    public boolean save(@RequestBody TableDefinition tableDefinition) {
        return tableDefinitionService.save(tableDefinition);
    }

    @PutMapping
    public boolean update(@RequestBody TableDefinition tableDefinition) {
        return tableDefinitionService.update(tableDefinition);
    }

    @DeleteMapping("/{tableName}")
    public boolean delete(@PathVariable String tableName) {
        return tableDefinitionService.delete(tableName);
    }

    @GetMapping("/fields/{tableName}")
    public List<TableFieldDef> getFields(@PathVariable String tableName) {
        return tableDefinitionService.getFieldsByTableName(tableName);
    }

    @PostMapping("/fields")
    public boolean addField(@RequestBody TableFieldDef field) {
        return tableDefinitionService.addField(field.getTableName(), field);
    }

    @DeleteMapping("/fields/{tableName}")
    public boolean deleteFields(@PathVariable String tableName,
                                @RequestBody List<String> fieldIds) {
        return tableDefinitionService.deleteFields(tableName, fieldIds);
    }
}