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
        ClubMember existingMembers = clubMemberMapper.selectByStudentId(studentId);
        if (existingMembers != null) {
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
        Account currentUser = TokenUtils.getCurrentUser(); // 获取当前用户
        String userRole = currentUser.getRole(); // 全局角色（ADMIN/STUDENT）

        // 1. 管理员（ADMIN）：无限制，可查所有申请
        if ("ADMIN".equals(userRole)) {
            // 不额外限制，直接查询
        }
        // 2. 学生（STUDENT）：区分社长和普通成员
        else if ("STUDENT".equals(userRole)) {
            // 2.1 判断当前学生是否为某社团的社长
            ClubMember leader = clubMemberMapper.selectLeaderByStudentId(currentUser.getId());
            if (leader != null) {
                // 社长：仅能查看自己社团的所有申请（用clubId限制）
                application.setClubId(leader.getClubId());
            } else {
                // 普通成员：仅能查看自己提交的申请（用studentId限制）
                application.setStudentId(currentUser.getId());
            }
        }
        // 3. 其他角色：无权限
        else {
            throw new CustomerException("权限不足，无法查看申请");
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Application> list = applicationMapper.selectAll(application);
        return PageInfo.of(list);
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
        // 1. 系统管理员：拥有所有权限
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        // 2. 学生角色：需校验是否为该社团的社长（LEADER）
        if ("STUDENT".equals(currentUser.getRole())) {
            // 查询该学生在申请的社团中是否为社长
            ClubMember leaderMember = clubMemberMapper.selectByStudentIdAndClubId(
                    currentUser.getId(), clubId
            );
            if (leaderMember == null || !"LEADER".equals(leaderMember.getRole())) {
                throw new CustomerException("无权限审核该社团申请");
            }
        } else {
            // 其他角色：无权限
            throw new CustomerException("权限不足，无法审核申请");
        }
    }


    // 审核申请（同意/拒绝）
    public void approveApplication(String applicationId, String status) {
        // 1. 查询申请详情（获取申请的社团ID）
        Application application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new CustomerException("申请不存在");
        }
        String clubId = application.getClubId(); // 申请的社团ID

        // 2. 校验审核权限
        checkApprovePermission(clubId);

        // 3. 执行审核（更新状态）
        application.setStatus(status);
        application.setApproverId(TokenUtils.getCurrentUser().getId());
        application.setApproveTime(DateUtil.now());
        applicationMapper.updateById(application);
    }

}
