package com.example.service;

import com.example.entity.Category;
import com.example.exception.CustomerException;
import com.example.mapper.CategoryMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Resource
    CategoryMapper categoryMapper;

    public String Category(String title) {
        if (title.equals("notice"))
            return title;
        else throw new CustomerException("标题不存在");
    }

    public List<Category> selectAll(Category category) {
        return categoryMapper.selectAll(category);
    }

    public void add(Category category) {
        // 判断用户名是否已存在
        Category dbCategory = categoryMapper.selectByTitle(category.getTitle());
        if (dbCategory != null) {
            throw new CustomerException("标题已存在");
        }
        categoryMapper.insert(category);
    }


    public PageInfo<Category> selectPage(Integer pageNum, Integer pageSize, Category category) {
        // 开始分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Category> categoryList = categoryMapper.selectAll(category);
        return PageInfo.of(categoryList);
    }


    public void update(Category category) {
        if (!categoryMapper.selectTitle(category).equals(category.getTitle())) {
            Category dbCategory = categoryMapper.selectByTitle(category.getTitle());
            if (dbCategory != null) {
                throw new CustomerException("标题已存在");
            }
        }
        categoryMapper.update(category);

    }

    public void deleteById(String id) {
        categoryMapper.deleteById(id);
    }



    public void deleteBatch(List<Category> list) {
        for (Category category : list) {
            this.deleteById(category.getId());
        }
    }

}
