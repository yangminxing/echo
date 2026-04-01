package org.echo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.echo.entity.FormDefinition;
import org.echo.mapper.FormDefinitionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FormDefinitionService {

    @Autowired
    private FormDefinitionMapper formDefinitionMapper;

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
}
