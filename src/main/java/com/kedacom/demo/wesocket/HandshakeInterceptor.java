package com.kedacom.demo.wesocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.kedacom.demo.model.User;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor{
	@Override    
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> attributes) throws Exception {    
		System.out.println("Before Handshake");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                //使用userName区分WebSocketHandler，以便定向发送消息
                User user = (User) session.getAttribute("currentUser");
                attributes.put("WEBSOCKET_USERNAME",user.getName());
            }
        }
		return super.beforeHandshake(request, response, handler, attributes);    
    }   
      
    @Override    
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {    
        super.afterHandshake(request, response, wsHandler, ex);    
    }          
      
}
