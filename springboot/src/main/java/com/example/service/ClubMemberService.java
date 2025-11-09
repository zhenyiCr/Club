package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Club;
import com.example.entity.ClubMember;
import com.example.exception.CustomerException;
import com.example.mapper.ClubMapper;
import com.example.mapper.ClubMemberMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ClubMemberService {

    @Resource
    private ClubMemberMapper clubMemberMapper;
    @Resource
    private ClubMapper clubMapper;

    // 管理员和社长 操作权限校验
    private void checkMemberPermission(ClubMember clubMember) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 1. 系统管理员拥有所有权限
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        // 2. 学生角色需要校验是否为当前社团的LEADER
        if ("STUDENT".equals(currentUser.getRole())) {
            // 查询当前用户在该社团的角色
            ClubMember currentMember = clubMemberMapper.selectByStudentId(currentUser.getId());
            // 校验：必须是本社团的LEADER才能操作
            if (currentMember == null
                    || !"LEADER".equals(currentMember.getRole())
                    || !currentMember.getClubId().equals(clubMember.getClubId())) {
                throw new CustomerException("权限不足，无法管理该社团成员");
            }
        } else {
            // 其他角色（如未定义角色）无权限
            throw new CustomerException("权限不足，无法管理成员");
        }
    }

    // 根据社团ID查询成员
    public List<ClubMember> getMembersByClubId(String clubId) {
        return clubMemberMapper.selectByClubId(clubId);
    }

    // 分页查询社团成员
    public PageInfo<ClubMember> selectPage(Integer pageNum, Integer pageSize, ClubMember clubMember) {
        PageHelper.startPage(pageNum, pageSize);
        Account currentUser ;
        try {
            currentUser = TokenUtils.getCurrentUser();
        } catch (CustomerException e) {
            // 捕获TokenUtils抛出的异常并添加更明确的错误信息
            throw new CustomerException("获取当前用户失败: " + (e.getMessage() != null ? e.getMessage() : "未登录或登录已过期"));
        }
        // 1. ADMIN 权限：可查看所有社团成员
        if ("ADMIN".equals(currentUser.getRole())) {
            // 不做限制，直接查询
        }
        // 2. STUDENT 权限：仅能查看自己所在社团的成员
        else if ("STUDENT".equals(currentUser.getRole())) {
            // 查询当前学生是否属于某个社团（无论角色是LEADER还是MEMBER）
            ClubMember currentMember = clubMemberMapper.selectByStudentId(currentUser.getId());
            // 如果不是任何社团的成员，无权限
            if (currentMember == null) {
                throw new CustomerException("权限不足，仅社团成员可查看本社团成员列表");
            }
            // 限制只能查询自己所在的社团
            clubMember.setClubId(currentMember.getClubId());
        }
        // 3. 其他未定义角色：无权限
        else {
            throw new CustomerException("权限不足，无法查看成员列表");
        }

        List<ClubMember> list = clubMemberMapper.selectAll(clubMember);
        return PageInfo.of(list);
    }

    // 添加社团成员
    public void addMember(ClubMember clubMember) {
        // 新增校验：检查学生是否已加入其他社团
        ClubMember existingMembers = clubMemberMapper.selectByStudentId(clubMember.getStudentId());
        if (existingMembers != null) {
            throw new CustomerException("一个学生只能加入一个社团，无法重复加入");
        }
        if (clubMember.getRole() == null) {
            clubMember.setRole("MEMBER");
        }
        clubMember.setJoinTime(DateUtil.now());
        clubMemberMapper.insert(clubMember);
    }

    // 移除社团成员
    public void removeMember(String id) {
        // 校验权限
        ClubMember clubMember = clubMemberMapper.selectById(id);
        checkMemberPermission(clubMember);
        clubMemberMapper.deleteById(id);
    }

    // 修改：更新角色前校验权限
    public void updateMemberRole(ClubMember clubMember) {
        // 校验权限
        checkMemberPermission(clubMember);
        // 先查询成员所属社团ID
        clubMemberMapper.updateRole(clubMember);
    }
}