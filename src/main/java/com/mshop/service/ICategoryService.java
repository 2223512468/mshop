package com.mshop.service;

import com.mshop.common.ServerResponse;
import com.mshop.po.Category;

import java.util.List;

public interface ICategoryService {

    ServerResponse addCateGory(String categoryName, Integer parentId);

    ServerResponse updateCateGoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildCateGory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
