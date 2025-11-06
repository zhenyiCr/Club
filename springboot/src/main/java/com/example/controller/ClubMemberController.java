package com.example.controller;

import com.example.common.Result;
import com.example.entity.Club;
import com.example.entity.ClubMember;
import com.example.service.ClubMemberService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubMember")
public class ClubMemberController {

    @Resource
    private ClubMemberService clubMemberService;

    // 根据社团ID查询成员
    @GetMapping("/getByClubId")
    public Result getByClubId(String clubId) {
        return Result.success(clubMemberService.getMembersByClubId(clubId));
    }

    // 分页查询
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             ClubMember clubMember) {
        PageInfo<ClubMember> pageInfo = clubMemberService.selectPage(pageNum, pageSize, clubMember);
        return Result.success(pageInfo);
    }

    // 添加成员
    @PostMapping("/add")
    public Result add(@RequestBody ClubMember clubMember) {
        clubMemberService.addMember(clubMember);
        return Result.success();
    }

    // 移除成员
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        clubMemberService.removeMember(id);
        return Result.success();
    }

    // 更新角色
    @PutMapping("/updateRole")
    public Result updateRole(@RequestBody ClubMember clubMember) {
        clubMemberService.updateMemberRole(clubMember);
        return Result.success();
    }
}