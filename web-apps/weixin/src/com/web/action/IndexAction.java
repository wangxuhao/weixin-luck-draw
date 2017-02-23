package com.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dao.impl.AdminDao;

@Controller
public class IndexAction {
	
	@Autowired
	private AdminDao adminDao;
	
	@RequestMapping(value = "/**/index.page")
	public ModelAndView indexPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		return new ModelAndView("redirect:/web/index.html");
	}
	
	@ResponseBody
	@RequestMapping(value = "/**/login.action")
	public  boolean login(HttpServletRequest request, HttpServletResponse response, Model model) {
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		if(adminDao.checkUser(userName, password)){
			HttpSession session = request.getSession();
			session.setAttribute("loginUser", userName);
			return true;
		}
		return false;
	}
}
