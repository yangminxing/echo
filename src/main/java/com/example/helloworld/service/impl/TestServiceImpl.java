package com.example.helloworld.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.helloworld.entity.Test;
import com.example.helloworld.mapper.TestMapper;
import com.example.helloworld.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {
}