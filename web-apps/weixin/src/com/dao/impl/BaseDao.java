package com.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.dao.IBaseDao;

@Component
public class BaseDao implements IBaseDao {
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	//private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void update(String sql) {
		this.jdbcTemplate.update(sql);
	}

	@Override
	public void update(String sql, Object[] params) {
		this.jdbcTemplate.update(sql, params);
	}

	@Override
	public void delete(String sql) {
		this.jdbcTemplate.execute(sql);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int count(String sql) {
		this.jdbcTemplate.queryForInt(sql);
		return 0;
	}

	@Override
	public String getResultValue(String sql, String columnName) {
		String value = "";
		SqlRowSet s = this.jdbcTemplate.queryForRowSet(sql);
		while (s.next()) {
			value = s.getString(columnName);
		}
		return value;
	}

	@Override
	public List<?> getResult(String sql) {
		return this.jdbcTemplate.queryForList(sql);
	}

}
