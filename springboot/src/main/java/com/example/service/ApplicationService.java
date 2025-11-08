package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.Application;
import com.example.entity.Club;
import com.example.entity.ClubMember;
import com.example.exception.CustomerException;
import com.example.mapper.ApplicationMapper;
import com.example.mapper.ClubMapper;
import com.example.mapper.ClubMemberMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    @Resource
    ApplicationMapper applicationMapper;
    @Resource
    ClubMapper clubMapper;

    @Resource
    private ClubMemberMapper clubMemberMapper;

    public List<Application> selectAll(Application  application) {
        return applicationMapper.selectAll(application);
    }


    public void add(Application application) {
        // 1. 获取当前登录学生ID
        String studentId = TokenUtils.getCurrentUser().getId();
        application.setStudentId(studentId);

        // 2. 校验社团是否存在
        Club club = clubMapper.selectById(application.getClubId());
        if (club == null) {
            throw new CustomerException("该社团不存在");
        }
        // 3. 新增校验：检查学生是否已加入任何社团
        List<ClubMember> existingMembers = clubMemberMapper.selectByStudentId(studentId);
        if (existingMembers != null && !existingMembers.isEmpty()) {
            throw new CustomerException("一个学生只能加入一个社团，无法提交新申请");
        }

        // 4. 新增校验：检查该学生是否已申请过该社团（核心逻辑）
        Application existing = applicationMapper.selectByStudentAndClub(
                studentId,
                application.getClubId()
        );
        if (existing != null) {
            throw new CustomerException("你已申请过该社团，无需重复申请");
        }
        application.setStatus("PENDING");
        application.setCreateTime(DateUtil.now());
        applicationMapper.insert(application);
    }


    // 分页查询申请（管理员/学生视角）
    public PageInfo<Application> selectPage(Integer pageNum, Integer pageSize, Application application) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 学生只能看自己的申请，管理员可以看所有
        if ("STUDENT".equals(currentUser.getRole())) {
            application.setStudentId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Application> list = applicationMapper.selectAll(application);
        return PageInfo.of(list);
    }



    public void update(Application application) {
        applicationMapper.update(application);

    }

    public void deleteById(String id) {
        applicationMapper.deleteById(id);
    }



    public void deleteBatch(List<Application> list) {
        for (Application application : list) {
            this.deleteById(application.getId());
        }
    }

    // 审核申请权限校验
    private void checkApprovePermission(String clubId) {
        Account currentUser = TokenUtils.getCurrentUser();
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        if ("LEADER".equals(currentUser.getRole())) {
            Club club = clubMapper.selectById(clubId);
            if (club == null || !club.getFounderId().equals(currentUser.getId())) {
                throw new CustomerException("无权限审核该社团申请");
            }
        } else {
            throw new CustomerException("权限不足，无法审核申请");
        }
    }

    // 审核申请
    public void approve(Application application) {
        checkApprovePermission(application.getClubId()); // 校验权限
        applicationMapper.updateStatus(application);
    }

}
