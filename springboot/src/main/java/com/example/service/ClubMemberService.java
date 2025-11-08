package com.example.service;

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
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        if ("STUDENT".equals(currentUser.getRole()) &&
                "LEADER".equals(clubMember.getRole()) &&
                clubMember.getClubId().equals(clubMapper.selectById(currentUser.getId()).getId())
        ){} else {
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
        Account currentUser = TokenUtils.getCurrentUser();
        if (!"ADMIN".equals(currentUser.getRole())) {
            String clubId = clubMemberMapper.selectByStudentId(currentUser.getId()).getClubId();
            clubMember.setClubId(clubId);
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
        clubMember.setJoinTime(new Date().toString());
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