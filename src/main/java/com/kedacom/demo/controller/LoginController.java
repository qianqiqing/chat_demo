package com.kedacom.demo.controller;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kedacom.demo.model.User;
import com.kedacom.demo.service.LoginInfoService;
import com.kedacom.demo.service.UserManageService;

@Controller
@RequestMapping("/login")
public class LoginController {
	Logger logger = Logger.getLogger(LoginController.class);
	
	@Autowired
	private LoginInfoService loginInfoService;
	
	@Autowired
	private UserManageService userManageService;
	
	@RequestMapping (method = RequestMethod.GET)  
    public ModelAndView login() {
		ModelAndView view = new ModelAndView("login/login");
        return view;   
    }
	
	@RequestMapping (value = "/loginValidate")
	public String loginValidate(HttpSession session , String name, String password, Model model) {
		User user = loginInfoService.validateUser(name, password);
		if (user != null) {
			session.setAttribute("currentUser", user);
			return "index_test";
		} else {
			return "login/login";
		}
	}
}
