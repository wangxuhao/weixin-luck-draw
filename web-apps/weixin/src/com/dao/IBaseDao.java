package com.dao;

import java.util.List;

public interface IBaseDao {
	void update(String sql);

	void update(String sql, Object params[]);

	void delete(String sql);

	int count(String sql);

	String getResultValue(String sql, String columnName);

	List<?> getResult(String sql);
}
