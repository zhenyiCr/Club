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


    // 新增：查询社团时根据角色过滤
    public List<Club> selectAll(Club club) {
//        Account currentUser = TokenUtils.getCurrentUser();
//        // 学生只能查询自己的社团
//        if ("STUDENT".equals(currentUser.getRole())) {
//            // 查询当前学生是否属于某个社团（通过社团成员表关联）
//            ClubMember currentMember = clubMemberMapper.selectByStudentId(currentUser.getId());
//            // 若不是任何社团的成员，无权限
//            if (currentMember == null) {
//                throw new CustomerException("权限不足，仅社团成员可查看自己所在的社团信息");
//            }
//            // 限制只能查询自己所在的社团（通过社团ID过滤）
//            club.setId(currentMember.getClubId()); // 强制查询当前学生所在的社团ID
//        }
        return clubMapper.selectAll(club);
    }

    // 新增：校验社团操作权限（ADMIN无限制，LEADER只能操作自己的社团）
    private void checkClubPermission(String clubId) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 1. 管理员拥有所有权限
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        // 2. 学生角色需要校验是否为该社团的LEADER
        if ("STUDENT".equals(currentUser.getRole())) {
            // 查询当前用户在该社团的角色
            ClubMember currentMember = clubMemberMapper.selectByStudentId(currentUser.getId());
            if (currentMember == null
                    || !"LEADER".equals(currentMember.getRole())
                    || !currentMember.getClubId().equals(clubId)) {
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
        if (!TokenUtils.getCurrentUser().getRole().equals("ADMIN")) {
            throw new CustomerException("只有管理员可以删除社团");
        }
        clubMapper.deleteById(id);
    }



    public void deleteBatch(List<Club> list) {
        if (!TokenUtils.getCurrentUser().getRole().equals("ADMIN")) {
            throw new CustomerException("只有管理员可以删除社团");
        }
        for (Club club : list) {
            this.deleteById(club.getId());
        }
    }

}
