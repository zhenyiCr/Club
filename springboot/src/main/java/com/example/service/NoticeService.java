package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.Notice;
import com.example.exception.CustomerException;
import com.example.mapper.NoticeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Resource
    NoticeMapper noticeMapper;

    public String Notice(String title) {
        if (title.equals("notice"))
            return title;
        else throw new CustomerException("标题不存在");
    }

    public List<Notice> selectAll(Notice  notice) {
        return noticeMapper.selectAll(notice);
    }

    public void add(Notice notice) {
        // 判断用户名是否已存在
        Notice dbNotice = noticeMapper.selectByTitle(notice.getTitle());
        if (dbNotice != null) {
            throw new CustomerException("标题已存在");
        }
        if (StrUtil.isBlank(notice.getContent())) {
            notice.setContent("无");
        }
        notice.setTime(DateUtil.now());
        noticeMapper.insert(notice);
    }


    public PageInfo<Notice> selectPage(Integer pageNum, Integer pageSize, Notice notice) {
        // 开始分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> notices = noticeMapper.selectAll(notice);
        return PageInfo.of(notices);
    }


    public void update(Notice notice) {
        if (!noticeMapper.selectTitle(notice).equals(notice.getTitle())) {
            Notice dbNotice = noticeMapper.selectByTitle(notice.getTitle());
            if (dbNotice != null) {
                throw new CustomerException("标题已存在");
            }
        }
        notice.setTime(DateUtil.now());
        noticeMapper.update(notice);

    }

    public void deleteById(String id) {
        noticeMapper.deleteById(id);
    }



    public void deleteBatch(List<Notice> list) {
        for (Notice notice : list) {
            this.deleteById(notice.getId());
        }
    }


}
