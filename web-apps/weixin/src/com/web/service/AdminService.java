package com.web.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AdminService {
	
	public static boolean checkLogin(HttpServletRequest request){
		HttpSession session = request.getSession();
		boolean result = session.getAttribute("loginUser") == null ? false : true;
		return result;
	}

}
