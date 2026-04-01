package org.echo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.echo.entity.FormDefinition;
import org.echo.mapper.FormDefinitionMapper;
import org.echo.util.FormSchemaParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class FormDefinitionService {

    @Autowired
    private FormDefinitionMapper formDefinitionMapper;

    @Autowired
    private FormSchemaParser formSchemaParser;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<FormDefinition> list() {
        return formDefinitionMapper.selectList(null);
    }

    public IPage<FormDefinition> page(Integer current, Integer size) {
        IPage<FormDefinition> page = new Page<>(current, size);
        return formDefinitionMapper.selectPage(page, null);
    }

    public FormDefinition getById(String id) {
        return formDefinitionMapper.selectById(id);
    }

    public boolean save(FormDefinition formDefinition) {
        formDefinition.setCreateTime(new Date());
        formDefinition.setFormVersion(1);
        if (formDefinition.getStatus() == null) {
            formDefinition.setStatus("草稿");
        }
        if (formDefinition.getSyncStatus() == null) {
            formDefinition.setSyncStatus("未同步");
        }
        return formDefinitionMapper.insert(formDefinition) > 0;
    }

    public boolean update(FormDefinition formDefinition) {
        FormDefinition existing = formDefinitionMapper.selectById(formDefinition.getId());
        if (existing != null) {
            formDefinition.setUpdateTime(new Date());
            formDefinition.setFormVersion(existing.getFormVersion() + 1);
            return formDefinitionMapper.updateById(formDefinition) > 0;
        }
        return false;
    }

    public boolean delete(String id) {
        return formDefinitionMapper.deleteById(id) > 0;
    }

    @Transactional
    public boolean syncToPhysicalTable(String id) {
        FormDefinition formDefinition = formDefinitionMapper.selectById(id);
        if (formDefinition == null) {
            throw new RuntimeException("表单不存在");
        }

        String formSchema = formDefinition.getFormSchema();
        if (formSchema == null || formSchema.isEmpty()) {
            throw new RuntimeException("表单Schema为空，无法同步");
        }

        String formName = formDefinition.getFormName();
        String tableName = "form_" + generateTableName(formName);
        
        String createTableSQL = formSchemaParser.generateCreateTableSQL(
            tableName, 
            formSchema, 
            formName
        );

        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS `" + tableName + "`");
            jdbcTemplate.execute(createTableSQL);
        } catch (Exception e) {
            throw new RuntimeException("创建物理表失败: " + e.getMessage(), e);
        }

        formDefinition.setTableName(tableName);
        formDefinition.setSyncStatus("已同步");
        formDefinition.setSyncTime(new Date());
        formDefinition.setUpdateTime(new Date());

        return formDefinitionMapper.updateById(formDefinition) > 0;
    }

    private String generateTableName(String formName) {
        String tableName = formName
            .replaceAll("[\\s\\-]+", "_")
            .replaceAll("[^a-zA-Z0-9_\\u4e00-\\u9fa5]", "");
        
        if (tableName.length() > 50) {
            tableName = tableName.substring(0, 50);
        }
        
        return tableName.toLowerCase();
    }
}
