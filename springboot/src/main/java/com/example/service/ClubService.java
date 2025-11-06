package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.Club;
import com.example.exception.CustomerException;
import com.example.mapper.ClubMapper;
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

    public String Club(String title) {
        if (title.equals("club"))
            return title;
        else throw new CustomerException("标题不存在");
    }

    public List<Club> selectAll(Club  club) {
        return clubMapper.selectAll(club);
    }

    public void add(Club club) {
        // 判断用户名是否已存在
        Club dbClub = clubMapper.selectByName(club.getName());
        if (dbClub != null) {
            throw new CustomerException("标题已存在");
        }
        club.setFounderId(TokenUtils.getCurrentUser().getId());
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
        // 校验权限（只有创始人可修改）
        String currentUserId = TokenUtils.getCurrentUser().getId();
        Club oldClub = clubMapper.selectById(club.getId());
        if (!oldClub.getFounderId().equals(currentUserId)) {
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
        clubMapper.deleteById(id);
    }



    public void deleteBatch(List<Club> list) {
        for (Club club : list) {
            this.deleteById(club.getId());
        }
    }


}
