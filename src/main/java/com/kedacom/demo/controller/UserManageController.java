package com.kedacom.demo.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kedacom.demo.model.User;
import com.kedacom.demo.service.UserManageService;

@Controller
@RequestMapping("/userManage")
public class UserManageController {
	
	@Autowired
	private UserManageService userManageService;
     
	 @RequestMapping (value = "/addUserIndex" , method = RequestMethod.GET)
	 public ModelAndView addUserIndex() {
		 ModelAndView view = new ModelAndView();
		 view.setViewName("userManage/addUser");
		 return view;
	 }
	 
	 @RequestMapping (value = "/loadUser" , method = RequestMethod.GET)
	 public ModelAndView loadUser(@RequestParam int userId) {
		 ModelAndView view = new ModelAndView("userManage/userContent");
		 User user = userManageService.getUserById(userId);
		 view.addObject("user", user);
		 return view;
	 }
	 
	 @RequestMapping (value = "/createUser" , method = RequestMethod.POST)
	 @ResponseBody
	 public ResponseEntity<String> createUser(User user) {
		 try {
		    userManageService.createUser(user);
		 } catch(Exception e) {
			 return new ResponseEntity<String>("创建用户失败！", HttpStatus.INTERNAL_SERVER_ERROR);
		 }
		 return new ResponseEntity<String>("创建用户成功！", HttpStatus.OK);
	 }
	 
	 @RequestMapping (value = "/downLoad")
	 @ResponseBody
	 public void downLoad(HttpServletResponse response, @RequestParam String fileName) throws UnsupportedEncodingException {
		 userManageService.downLoad(response, java.net.URLDecoder.decode(fileName,"UTF-8"));
	 }
         
}
