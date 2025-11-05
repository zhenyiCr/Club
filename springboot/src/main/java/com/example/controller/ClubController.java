package com.example.controller;


import com.example.common.Result;
import com.example.entity.Club;
import com.example.service.ClubService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/club")
public class ClubController {

    @Resource
    ClubService clubService;

    @GetMapping("/club")
    public Result club(String title) {
        String club = clubService.Club(title);
        return Result.success(club);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Club club) { // @RequestBody 接受前端传来的json数据
        clubService.add(club);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        clubService.deleteById(id);
        return Result.success();
    }
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Club> list) {
        clubService.deleteBatch(list);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Club club) {
        clubService.update(club);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Club club) {
        List<Club> clubList = clubService.selectAll(club);
        return Result.success(clubList);
    }

    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            Club club) {
        PageInfo<Club> pageInfo = clubService.selectPage(pageNum,pageSize,club);
        return Result.success(pageInfo);
    }

}
