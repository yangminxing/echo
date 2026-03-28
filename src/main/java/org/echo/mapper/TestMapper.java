package org.echo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.echo.entity.Test;

@Mapper
public interface TestMapper extends BaseMapper<Test> {
}