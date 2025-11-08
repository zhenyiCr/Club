package com.example.controller;


import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Club;
import com.example.service.ClubService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/club")
public class ClubController {

    @Resource
    ClubService clubService;


    @PostMapping("/add")
    public Result add(@RequestBody Club club) { // @RequestBody 接受前端传来的json数据
        clubService.add(club);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        Account currentUser = TokenUtils.getCurrentUser();
        if (!"ADMIN".equals(currentUser.getRole())) {
            return Result.error("只有管理员可以删除社团");
        }
        clubService.deleteById(id);
        return Result.success();
    }
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Club> list) {
        clubService.deleteBatch(list);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Club club) {
        clubService.update(club);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Club club) {
        List<Club> clubList = clubService.selectAll(club);
        return Result.success(clubList);
    }

    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            Club club) {
        PageInfo<Club> pageInfo = clubService.selectPage(pageNum,pageSize,club);
        return Result.success(pageInfo);
    }

    @RequestMapping("/upload")
    public class UploadController {

        @Value("${upload.path}") // 在application.properties中配置：upload.path=./uploads/
        private String uploadPath;

        @PostMapping("/avatar")
        public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
            if (file.isEmpty()) {
                return Result.error("文件为空");
            }
            // 创建上传目录
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 生成唯一文件名
            String fileName = UUID.randomUUID() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            try {
                // 保存文件
                file.transferTo(new File(uploadPath + fileName));
                // 返回文件访问路径（实际项目中应配置静态资源映射）
                return Result.success("/uploads/" + fileName);
            } catch (Exception e) {
                return Result.error("上传失败：" + e.getMessage());
            }
        }
    }

}
