package com.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dao.IAdminDao;
import com.pojo.Administrators;

@Component
public class AdminDao implements IAdminDao {
	
	@Autowired
	private SqlSession sqlSession;

	@Override
	public boolean checkUser(String userName, String password) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("password", password);
		Administrators aministrators = sqlSession.selectOne("com.pojo.AdminMapper.checkAdmin", params);
		boolean result = aministrators != null ? true : false;
		return result;
	}

}
