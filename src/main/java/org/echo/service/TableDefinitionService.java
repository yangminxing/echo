package org.echo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.echo.entity.TableDefinition;
import org.echo.entity.TableFieldDef;
import org.echo.mapper.TableDefinitionMapper;
import org.echo.mapper.TableFieldMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TableDefinitionService {

    @Autowired
    private TableDefinitionMapper tableDefinitionMapper;

    @Autowired
    private TableFieldMapper tableFieldMapper;

    public TableDefinition getByTableName(String tableName) {
        QueryWrapper<TableDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("table_name", tableName);
        TableDefinition tableDefinition = tableDefinitionMapper.selectOne(queryWrapper);
        if (tableDefinition != null) {
            tableDefinition.setFields(getFieldsByTableName(tableName));
        }
        return tableDefinition;
    }

    public List<TableDefinition> list() {
        List<TableDefinition> definitions = tableDefinitionMapper.selectList(null);
        for (TableDefinition definition : definitions) {
            definition.setFields(getFieldsByTableName(definition.getTableName()));
        }
        return definitions;
    }

    public IPage<TableDefinition> page(Integer current, Integer size) {
        IPage<TableDefinition> page = new Page<>(current, size);
        IPage<TableDefinition> result = tableDefinitionMapper.selectPage(page, null);
        for (TableDefinition definition : result.getRecords()) {
            definition.setFields(getFieldsByTableName(definition.getTableName()));
        }
        return result;
    }

    public List<TableFieldDef> getFieldsByTableName(String tableName) {
        QueryWrapper<TableFieldDef> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("table_name", tableName);
        queryWrapper.orderByAsc("sort_order");
        return tableFieldMapper.selectList(queryWrapper);
    }

    @Transactional
    public boolean save(TableDefinition tableDefinition) {
        tableDefinition.setCreateTime(new Date());
        tableDefinition.setVersion(1);
        tableDefinition.setSyncStatus("未同步");
        int result = tableDefinitionMapper.insert(tableDefinition);
        if (result > 0 && tableDefinition.getFields() != null) {
            saveFields(tableDefinition.getTableName(), tableDefinition.getFields());
        }
        return result > 0;
    }

    @Transactional
    public boolean update(TableDefinition tableDefinition) {
        tableDefinition.setUpdateTime(new Date());
        tableDefinition.setSyncStatus("未同步");
        tableDefinition.setVersion(tableDefinition.getVersion() + 1);
        int result = tableDefinitionMapper.updateById(tableDefinition);
        if (result > 0 && tableDefinition.getFields() != null) {
            QueryWrapper<TableFieldDef> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("table_name", tableDefinition.getTableName());
            tableFieldMapper.delete(queryWrapper);
            saveFields(tableDefinition.getTableName(), tableDefinition.getFields());
        }
        return result > 0;
    }

    private void saveFields(String tableName, List<TableFieldDef> fields) {
        for (int i = 0; i < fields.size(); i++) {
            TableFieldDef field = fields.get(i);
            field.setId(null);
            field.setTableName(tableName);
            field.setSortOrder(i);
            field.setCreateTime(new Date());
            tableFieldMapper.insert(field);
        }
    }

    @Transactional
    public boolean delete(String tableName) {
        QueryWrapper<TableFieldDef> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("table_name", tableName);
        tableFieldMapper.delete(queryWrapper);
        QueryWrapper<TableDefinition> defQueryWrapper = new QueryWrapper<>();
        defQueryWrapper.eq("table_name", tableName);
        return tableDefinitionMapper.delete(defQueryWrapper) > 0;
    }

    @Transactional
    public boolean addField(String tableName, TableFieldDef field) {
        QueryWrapper<TableFieldDef> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("table_name", tableName);
        long count = tableFieldMapper.selectCount(queryWrapper);
        field.setTableName(tableName);
        field.setSortOrder((int) count);
        field.setCreateTime(new Date());
        return tableFieldMapper.insert(field) > 0;
    }

    @Transactional
    public boolean deleteFields(String tableName, List<String> fieldIds) {
        QueryWrapper<TableFieldDef> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("table_name", tableName);
        queryWrapper.in("id", fieldIds);
        return tableFieldMapper.delete(queryWrapper) > 0;
    }
}