package com.example.controller;

import com.example.common.Result;
import com.example.entity.ClubMember;
import com.example.service.ClubMemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubMember")
public class ClubMemberController {

    @Resource
    ClubMemberService clubMemberService;

    // 添加成员（审核通过时调用）
    @PostMapping("/add")
    public Result add(@RequestBody ClubMember clubMember) {
        clubMemberService.add(clubMember);
        return Result.success();
    }

    // 移除成员
    @DeleteMapping("/delete/{clubId}/{studentId}")
    public Result delete(@PathVariable String clubId, @PathVariable String studentId) {
        clubMemberService.delete(clubId, studentId);
        return Result.success();
    }

    // 查询社团成员
    @GetMapping("/selectByClubId/{clubId}")
    public Result selectByClubId(@PathVariable String clubId) {
        List<ClubMember> members = clubMemberService.selectByClubId(clubId);
        return Result.success(members);
    }

    // 设置管理员
    @PutMapping("/setAdmin")
    public Result setAdmin(@RequestBody ClubMember clubMember) {
        clubMemberService.setAdmin(clubMember.getClubId(), clubMember.getStudentId());
        return Result.success();
    }
}
