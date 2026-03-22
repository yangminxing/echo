package com.example.helloworld.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.helloworld.entity.Test;
import com.example.helloworld.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    // 新增数据
    @PostMapping
    public boolean save(@RequestBody Test test) {
        return testService.save(test);
    }

    // 更新数据
    @PutMapping
    public boolean update(@RequestBody Test test) {
        return testService.updateById(test);
    }

    // 删除数据
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable String id) {
        return testService.removeById(id);
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public Test getById(@PathVariable String id) {
        return testService.getById(id);
    }

    // 查询所有
    @GetMapping("/list")
    public List<Test> list() {
        return testService.list();
    }

    // 分页查询
    @GetMapping("/page")
    public IPage<Test> page(@RequestParam Integer current, @RequestParam Integer size) {
        IPage<Test> page = new Page<>(current, size);
        QueryWrapper<Test> queryWrapper = new QueryWrapper<>();
        return testService.page(page, queryWrapper);
    }
}