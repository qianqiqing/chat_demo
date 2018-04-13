package com.kedacom.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.BasicConfigurator;  
import org.apache.log4j.Level;  
import org.apache.log4j.Logger;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kedacom.demo.model.User;
import com.kedacom.demo.service.LoginInfoService;

@Controller
@RequestMapping("/login")
public class LoginController {
	Logger logger = Logger.getLogger(LoginController.class);
	
	@Autowired
	private LoginInfoService loginInfoService;
	
	private User user;
	
	@RequestMapping (method = RequestMethod.GET)  
    public ModelAndView login() {
		ModelAndView view = new ModelAndView("login/login");
        return view;   
    }
	
	/*@RequestMapping (value = "/validate", method = RequestMethod.POST)
	@ResponseBody
	public String validate (HttpSession session , @RequestParam String name, @RequestParam String password) {
		user = new User(name,password);
		String msg = loginInfoService.validate(user);
		if (StringUtils.isEmpty(msg)){
			session.setAttribute("currentUser", user);
		}
		return msg;
	}*/
	
	@RequestMapping (value = "/loginValidate")
	public String loginValidate(HttpSession session , String name, String password){
		User user = loginInfoService.validateUser(name, password);
		if (user != null){
			session.setAttribute("currentUser", user);
			return "index";
		} else{
			return "login/login";
		}
	}
	
	
	@RequestMapping (value = "/managerIndex" , method = RequestMethod.GET)
	public ModelAndView managerIndex() {
		ModelAndView view = new ModelAndView("index");
		return view;
	}
}
