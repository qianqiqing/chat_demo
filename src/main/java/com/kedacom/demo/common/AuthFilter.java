package com.kedacom.demo.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kedacom.demo.model.User;

public class AuthFilter implements Filter {
	private String requestUrl;
	private String initUrl;

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");
		requestUrl = request.getRequestURI();
		String[] reUrl = requestUrl.split("/");

		if (initUrl.equals(reUrl[reUrl.length-1]) || user != null) {
		    arg2.doFilter(request, response); 
		}else if(requestUrl.contains(".css") || requestUrl.contains(".js") || requestUrl.contains("/loginValidate")){
			arg2.doFilter(arg0, arg1);
		}else {
		    response.sendRedirect(request.getContextPath()+"/login");
		}
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		initUrl = arg0.getInitParameter("initUrl");
	}
	
    public void destroy() {
		
	}
}