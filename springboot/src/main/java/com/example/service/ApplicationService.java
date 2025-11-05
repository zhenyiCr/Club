package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.Application;
import com.example.exception.CustomerException;
import com.example.mapper.ApplicationMapper;
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


    public PageInfo<Application> selectPage(Integer pageNum, Integer pageSize, Application application) {
        // 开始分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Application> applications = applicationMapper.selectAll(application);
        return PageInfo.of(applications);
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
