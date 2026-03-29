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

    /**
     * 新增测试数据
     *
     * @param test 测试实体对象
     * @return boolean 是否保存成功
     */
    @PostMapping
    public boolean save(@RequestBody Test test) {
        return testService.save(test);
    }

    /**
     * 更新测试数据
     *
     * @param test 测试实体对象（需包含id）
     * @return boolean 是否更新成功
     */
    @PutMapping
    public boolean update(@RequestBody Test test) {
        return testService.updateById(test);
    }

    /**
     * 根据ID删除测试数据
     *
     * @param id 测试数据ID
     * @return boolean 是否删除成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable String id) {
        return testService.removeById(id);
    }

    /**
     * 根据ID获取测试数据
     *
     * @param id 测试数据ID
     * @return Test 测试实体对象
     */
    @GetMapping("/{id}")
    public Test getById(@PathVariable String id) {
        return testService.getById(id);
    }

    /**
     * 获取所有测试数据列表
     *
     * @return List<Test> 测试数据列表
     */
    @GetMapping("/list")
    public List<Test> list() {
        return testService.list();
    }

    /**
     * 分页查询测试数据
     *
     * @param current 当前页码（从1开始）
     * @param size    每页数量
     * @return IPage<Test> 分页结果
     */
    @GetMapping("/page")
    public IPage<Test> page(@RequestParam Integer current, @RequestParam Integer size) {
        IPage<Test> page = new Page<>(current, size);
        QueryWrapper<Test> queryWrapper = new QueryWrapper<>();
        return testService.page(page, queryWrapper);
    }
}