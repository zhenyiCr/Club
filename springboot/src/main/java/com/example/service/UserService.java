package com.example.service;

import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.ChangePasswordDTO;
import com.example.entity.User;
import com.example.exception.CustomerException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    public String User(String username) {
        if (username.equals("user"))
            return username;
        else throw new CustomerException("用户名不存在");
    }

    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    public void add(User user) {
        // 判断用户名是否已存在
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (dbUser != null) {
            throw new CustomerException("账号已存在");
        }
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword("user");
        }
        if (StrUtil.isBlank(user.getName())) {
            user.setName(user.getUsername());
        }
        user.setRole("USER");
        userMapper.insert(user);
    }


    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize, User user) {
        // 开始分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.selectAll(user);
        return PageInfo.of(users);
    }


    public void update(User user) {
        if (!userMapper.selectUsername(user).equals(user.getUsername())) {
            User dbUser = userMapper.selectByUsername(user.getUsername());
            if (dbUser != null) {
                throw new CustomerException("账号已存在");
            }
        }
        userMapper.update(user);

    }

    public void deleteById(String id) {
        userMapper.deleteById(id);
    }

    public void deleteBatch(List<User> list) {
        for (User user : list) {
            this.deleteById(user.getId());
        }
    }

    public User login(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (dbUser == null) {
            throw new CustomerException("用户名不存在");
        }
        if (!dbUser.getPassword().equals(account.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }
        String token = TokenUtils.createToken(dbUser.getId() + "-" +"USER", dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    public void register(User user) {
        this.add(user);
    }

    public User selectById(String id) {
        return userMapper.selectById(id);
    }

    public User updatePassword(ChangePasswordDTO changePasswordDTO, Account currentAccount) {
        User dbUser = userMapper.selectById(currentAccount.getId());
        // 正确：用用户输入的oldPassword对比数据库密码
        if (!dbUser.getPassword().equals(changePasswordDTO.getOldPassword())) {
            throw new CustomerException("原密码错误");
        }
        dbUser.setPassword(changePasswordDTO.getNewPassword());
        // 用新密码重新生成token（关键：密码变更后旧token失效）
        dbUser.setToken(TokenUtils.createToken(dbUser.getId() + "-ADMIN", dbUser.getPassword()));
        userMapper.update(dbUser);
        return dbUser;
    }
}
