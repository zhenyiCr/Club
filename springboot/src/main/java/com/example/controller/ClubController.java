package com.example.controller;


import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Club;
import com.example.service.ClubService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/club")
public class ClubController {

    @Resource
    ClubService clubService;


    @PostMapping("/add")
    public Result add(@RequestBody Club club) { // @RequestBody 接受前端传来的json数据
        clubService.add(club);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        Account currentUser = TokenUtils.getCurrentUser();
        if (!"ADMIN".equals(currentUser.getRole())) {
            return Result.error("只有管理员可以删除社团");
        }
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
