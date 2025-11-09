package com.example.service;

import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.ChangePasswordDTO;
import com.example.entity.ClubMember;
import com.example.entity.Student;
import com.example.exception.CustomerException;
import com.example.mapper.ClubMemberMapper;
import com.example.mapper.StudentMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Resource
    StudentMapper studentMapper;
    @Resource
    ClubMemberMapper clubMemberMapper;

    public List<Student> selectAll(Student student) {
        return studentMapper.selectAll(student);
    }

    public void add(Student student) {
        // 判断用户名是否已存在
        Student dbStudent = studentMapper.selectByUsername(student.getUsername());
        if (dbStudent != null) {
            throw new CustomerException("账号已存在");
        }
        if (StrUtil.isBlank(student.getPassword())) {
            student.setPassword("student");
        }
        if (StrUtil.isBlank(student.getName())) {
            student.setName(student.getUsername());
        }
        student.setRole("STUDENT");
        studentMapper.insert(student);
    }


    public PageInfo<Student> selectPage(Integer pageNum, Integer pageSize, Student student) {
        // 开始分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Student> students = studentMapper.selectAll(student);
        return PageInfo.of(students);
    }


    public void update(Student student) {
        if (!studentMapper.selectUsername(student).equals(student.getUsername())) {
            Student dbStudent = studentMapper.selectByUsername(student.getUsername());
            if (dbStudent != null) {
                throw new CustomerException("账号已存在");
            }
        }
        studentMapper.update(student);

    }

    public void deleteById(String id) {
        studentMapper.deleteById(id);
    }

    public void deleteBatch(List<Student> list) {
        for (Student student : list) {
            this.deleteById(student.getId());
        }
    }

    public Student login(Account account) {
        Student dbStudent = studentMapper.selectByUsername(account.getUsername());
        if (dbStudent == null) {
            throw new CustomerException("用户名不存在");
        }
        if (!dbStudent.getPassword().equals(account.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }

        // 新增：查询学生的社团信息
        ClubMember clubMember = clubMemberMapper.selectByStudentId(dbStudent.getId());
        if (clubMember != null) {
            dbStudent.setClubId(clubMember.getClubId());
            dbStudent.setClubRole(clubMember.getRole());
        } else {
            dbStudent.setClubId(null);
            dbStudent.setClubRole(null);
        }

        String token = TokenUtils.createToken(dbStudent.getId() + "-" +"STUDENT", dbStudent.getPassword());
        dbStudent.setToken(token);
        return dbStudent;
    }

    public void register(Student student) {
        this.add(student);
    }

    public Student selectById(String id) {
        return studentMapper.selectById(id);
    }

    public Student updatePassword(ChangePasswordDTO changePasswordDTO, Account currentAccount) {
        Student dbStudent = studentMapper.selectById(currentAccount.getId());
        // 正确：用用户输入的oldPassword对比数据库密码
        if (!dbStudent.getPassword().equals(changePasswordDTO.getOldPassword())) {
            throw new CustomerException("原密码错误");
        }
        dbStudent.setPassword(changePasswordDTO.getNewPassword());
        // 用新密码重新生成token（关键：密码变更后旧token失效）
        dbStudent.setToken(TokenUtils.createToken(dbStudent.getId() + "-STUDENT", dbStudent.getPassword()));
        studentMapper.update(dbStudent);
        return dbStudent;
    }
}
