package org.echo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.echo.entity.Test;
import org.echo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping
    public boolean save(@RequestBody Test test) {
        return testService.save(test);
    }

    @PutMapping
    public boolean update(@RequestBody Test test) {
        return testService.updateById(test);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable String id) {
        return testService.removeById(id);
    }

    @GetMapping("/{id}")
    public Test getById(@PathVariable String id) {
        return testService.getById(id);
    }

    @GetMapping("/list")
    public List<Test> list() {
        return testService.list();
    }

    @GetMapping("/page")
    public IPage<Test> page(@RequestParam Integer current, @RequestParam Integer size) {
        IPage<Test> page = new Page<>(current, size);
        QueryWrapper<Test> queryWrapper = new QueryWrapper<>();
        return testService.page(page, queryWrapper);
    }
}