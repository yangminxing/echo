package org.echo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.echo.entity.Test;
import org.echo.mapper.TestMapper;
import org.echo.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {
}