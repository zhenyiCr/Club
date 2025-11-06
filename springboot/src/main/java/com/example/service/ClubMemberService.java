package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Club;
import com.example.entity.ClubMember;
import com.example.exception.CustomerException;
import com.example.mapper.ClubMapper;
import com.example.mapper.ClubMemberMapper;
import com.example.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubMemberService {

    @Resource
    ClubMemberMapper clubMemberMapper;

    @Resource
    ClubMapper clubMapper;

    // 添加成员（自动设置为普通成员）
    public void add(ClubMember clubMember) {
        // 校验社团是否存在
        Club club = clubMapper.selectById(clubMember.getClubId());
        if (club == null) {
            throw new CustomerException("社团不存在");
        }
        // 校验是否已加入
        ClubMember exist = clubMemberMapper.selectByClubAndStudent(
                clubMember.getClubId(), clubMember.getStudentId());
        if (exist != null) {
            throw new CustomerException("已加入该社团");
        }
        clubMember.setRole("MEMBER"); // 普通成员
        clubMember.setJoinTime(DateUtil.now());
        clubMemberMapper.insert(clubMember);
    }

    // 移除成员（只有创始人或管理员可操作）
    public void delete(String clubId, String studentId) {
        String currentUserId = TokenUtils.getCurrentUser().getId();
        Club club = clubMapper.selectById(clubId);
        // 校验是否为创始人或管理员
        if (club == null || (!club.getFounderId().equals(currentUserId) &&
                !"ADMIN".equals(clubMemberMapper.selectByClubAndStudent(clubId, currentUserId).getRole()))) {
            throw new CustomerException("无权限操作");
        }
        // 校验是否为普通成员
        ClubMember member = clubMemberMapper.selectByClubAndStudent(clubId, studentId);
        if (member == null || !"MEMBER".equals(member.getRole())) {
            throw new CustomerException("非普通成员，无法移除");
        }
        // 校验是否为创始人
        if (!club.getFounderId().equals(currentUserId)) {
            // 校验是否为管理员
            ClubMember admin = clubMemberMapper.selectByClubAndStudent(clubId, currentUserId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                throw new CustomerException("无权限操作");
            }
        }
        clubMemberMapper.delete(clubId, studentId);
    }

    // 查询社团成员
    public List<ClubMember> selectByClubId(String clubId) {
        return clubMemberMapper.selectByClubId(clubId);
    }

    // 设置管理员
    public void setAdmin(String clubId, String studentId) {
        // 只有创始人可设置管理员
        String founderId = clubMapper.selectById(clubId).getFounderId();
        if (!founderId.equals(TokenUtils.getCurrentUser().getId())) {
            throw new CustomerException("只有创始人可设置社团管理员");
        }
        clubMemberMapper.updateRole(clubId, studentId, "MANAGER");
    }
}