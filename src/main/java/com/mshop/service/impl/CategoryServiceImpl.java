package com.mshop.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mshop.common.ServerResponse;
import com.mshop.dao.CategoryMapper;
import com.mshop.po.Category;
import com.mshop.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;


@Service
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCateGory(String categoryName, Integer parentId) {
        if (parentId == null && StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int resultCode = categoryMapper.insert(category);
        if (resultCode > 0) {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        } else {
            return ServerResponse.createByErrorMessage("添加品类失败");
        }
    }

    @Override
    public ServerResponse updateCateGoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int resultCode = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCode > 0) {
            return ServerResponse.createBySuccessMessage("更新品类名称成功");
        } else {
            return ServerResponse.createBySuccessMessage("更新品类名称失败");
        }
    }

    @Override
    public ServerResponse<List<Category>> getChildCateGory(Integer categoryId) {
        List<Category> cateList = categoryMapper.selectCateGoryChildByParentId(categoryId);
        if (CollectionUtils.isEmpty(cateList)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(cateList);
    }

    /**
     * 递归查询本节点的id及孩子节点的id
     *
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCateGory(categorySet, categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category category : categorySet) {
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }


    //递归算法,算出子节点
    public Set<Category> findChildCateGory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        //查找子节点,递归算法一定要有一个退出的条件
        List<Category> categorieList = categoryMapper.selectCateGoryChildByParentId(categoryId);
        for (Category categoryItem : categorieList) {
            findChildCateGory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }

}
