package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
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

import java.util.List;

@Service
public class ClubService {

    @Resource
    ClubMapper clubMapper;
    @Resource
    ClubMemberMapper clubMemberMapper;


    // 新增：查询社团时根据角色过滤（LEADER只能看自己的社团）
    public List<Club> selectAll(Club club) {
        Account currentUser = TokenUtils.getCurrentUser();
        // LEADER只能查询自己创建的社团
        if ("LEADER".equals(currentUser.getRole())) {
            club.setLeaderId(currentUser.getId());
        } else if ("MEMBER".equals(currentUser.getRole())) {
            // MEMBER只能查询已加入的社团
//            List<ClubMember> members = clubMemberMapper.selectByClubId(club.getId());
//            club.setIsJoined(members.stream().anyMatch(member -> member.getId().equals(currentUser.getId())));
        }
        return clubMapper.selectAll(club);
    }

    // 新增：校验社团操作权限（ADMIN无限制，LEADER只能操作自己的社团）
    private void checkClubPermission(String clubId) {
        Account currentUser = TokenUtils.getCurrentUser();
        if ("ADMIN".equals(currentUser.getRole())) {
            return; // 管理员拥有所有权限
        }
        if ("LEADER".equals(currentUser.getRole())) {
            Club club = clubMapper.selectById(clubId);
            if (club == null || !club.getLeaderId().equals(currentUser.getId())) {
                throw new CustomerException("无权限操作该社团");
            }
        } else {
            throw new CustomerException("权限不足，无法操作社团");
        }
    }


    public void add(Club club) {
        checkClubPermission(club.getId());
        // 判断用户名是否已存在
        Club dbClub = clubMapper.selectByName(club.getName());
        if (dbClub != null) {
            throw new CustomerException("标题已存在");
        }
        club.setStatus("ACTIVE");
        clubMapper.insert(club);
    }


    public PageInfo<Club> selectPage(Integer pageNum, Integer pageSize, Club club) {
        // 开始分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Club> clubs = clubMapper.selectAll(club);
        return PageInfo.of(clubs);
    }


    public void update(Club club) {
        checkClubPermission(club.getId());
        // 校验权限（只有创始人可修改）
        String currentUserId = TokenUtils.getCurrentUser().getId();
        Club oldClub = clubMapper.selectById(club.getId());
        if (!oldClub.getLeaderId().equals(currentUserId)) {
            throw new CustomerException("无权限修改社团信息");
        }

        // 原有名称校验逻辑
        if (!clubMapper.selectName(club).equals(club.getName())) {
            Club dbClub = clubMapper.selectByName(club.getName());
            if (dbClub != null) {
                throw new CustomerException("标题已存在");
            }
        }
        clubMapper.update(club);
    }

    public void deleteById(String id) {
        checkClubPermission(id);
        clubMapper.deleteById(id);
    }



    public void deleteBatch(List<Club> list) {
        for (Club club : list) {
            this.deleteById(club.getId());
        }
    }


}
