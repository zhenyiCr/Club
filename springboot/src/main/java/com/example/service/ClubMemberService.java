package com.example.service;

import com.example.entity.ClubMember;
import com.example.mapper.ClubMemberMapper;
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

    // 根据社团ID查询成员
    public List<ClubMember> getMembersByClubId(String clubId) {
        return clubMemberMapper.selectByClubId(clubId);
    }

    // 分页查询社团成员
    public PageInfo<ClubMember> selectPage(Integer pageNum, Integer pageSize, ClubMember clubMember) {
        PageHelper.startPage(pageNum, pageSize);
        List<ClubMember> list = clubMemberMapper.selectAll(clubMember);
        return PageInfo.of(list);
    }

    // 添加社团成员
    public void addMember(ClubMember clubMember) {
        clubMember.setJoinTime(new Date().toString());
        clubMemberMapper.insert(clubMember);
    }

    // 移除社团成员
    public void removeMember(String id) {
        clubMemberMapper.deleteById(id);
    }

    // 更新成员角色
    public void updateMemberRole(ClubMember clubMember) {
        clubMemberMapper.updateRole(clubMember);
    }
}