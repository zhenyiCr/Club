package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.Application;
import com.example.exception.CustomerException;
import com.example.mapper.ApplicationMapper;
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

    public String Application(String title) {
        if (title.equals("application"))
            return title;
        else throw new CustomerException("标题不存在");
    }

    public List<Application> selectAll(Application  application) {
        return applicationMapper.selectAll(application);
    }

    public void add(Application application) {
        application.setStatus("PENDING");
        application.setCreateTime(DateUtil.now());
        applicationMapper.insert(application);
    }


    // 分页查询申请（管理员/学生视角）
    public PageInfo<Application> selectPage(Integer pageNum, Integer pageSize, Application application) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 学生只能看自己的申请，管理员可以看所有
        if ("USER".equals(currentUser.getRole())) {
            application.setStudentId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Application> list = applicationMapper.selectAll(application);
        return PageInfo.of(list);
    }

    // 管理员审核申请
    public void approve(Application application) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new CustomerException("只有管理员可以审核");
        }
        Application dbApp = applicationMapper.selectById(application.getId());
        if (dbApp == null) {
            throw new CustomerException("申请不存在");
        }
        if (!"PENDING".equals(dbApp.getStatus())) {
            throw new CustomerException("该申请已处理，无需重复操作");
        }
        application.setApproverId(currentUser.getId());
        application.setApproveTime(DateUtil.now());
        applicationMapper.updateStatus(application);
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


}
