package org.echo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.echo.entity.TableDefinition;

@Mapper
public interface TableDefinitionMapper extends BaseMapper<TableDefinition> {
}