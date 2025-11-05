package com.example.controller;


import com.example.common.Result;
import com.example.entity.Introduction;
import com.example.service.IntroductionService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/introduction")
public class IntroductionController {

    @Resource
    IntroductionService introductionService;

    @GetMapping("/introduction")
    public Result Introduction(String title) {
        String introduction = introductionService.Introduction(title);
        return Result.success(introduction);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Introduction introduction) { // @RequestBody 接受前端传来的json数据
        introductionService.add(introduction);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        introductionService.deleteById(id);
        return Result.success();
    }
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Introduction> list) {
        introductionService.deleteBatch(list);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Introduction introduction) {
        introductionService.update(introduction);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Introduction introduction) {
        List<Introduction> introductionList = introductionService.selectAll(introduction);
        return Result.success(introductionList);
    }

    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                             Introduction introduction) {
        PageInfo<Introduction> pageInfo = introductionService.selectPage(pageNum,pageSize,introduction);
        return Result.success(pageInfo);
    }

}
