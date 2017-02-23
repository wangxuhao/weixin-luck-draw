package com.web.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Log4jListener implements ServletContextListener {

	private final String LOG4JDIRSIGN = "log4jdir";

	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {
		System.getProperties().remove(LOG4JDIRSIGN);
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		String realPath = servletContext.getServletContext().getRealPath("/");
		System.setProperty(LOG4JDIRSIGN, realPath);
	}

}
