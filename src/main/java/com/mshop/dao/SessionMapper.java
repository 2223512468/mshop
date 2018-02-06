package com.mshop.dao;

import com.mshop.po.Session;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionMapper {
    int deleteByPrimaryKey(Integer sid);

    int insert(Session record);

    int insertSelective(Session record);

    Session selectByPrimaryKey(Integer sid);

    int updateByPrimaryKeySelective(Session record);

    int updateByPrimaryKey(Session record);

    Session selectByUserId(Integer user_id);

    int updateTokenByUserId(@Param("token") String token, @Param("user_id") Integer user_id);


}