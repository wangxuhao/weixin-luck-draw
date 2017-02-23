package com.web.util;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class WebContextListener implements ServletContextListener {

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("web contextInitialized start!");
		initDatabase(event);
		logger.info("web contextInitialized end!");
	}

	/*
	 * 
	 */
	public void initDatabase(ServletContextEvent event) {
		logger.info("init database start!");
		WebApplicationContext springContext = WebApplicationContextUtils
				.getWebApplicationContext(event.getServletContext());
		JdbcTemplate jdbcTemplate = (JdbcTemplate) springContext.getBean("jdbcTemplate");
		DataSource dataSource = jdbcTemplate.getDataSource();
		try {
			dataSource.getConnection();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.info("init database end!");
	}
}
