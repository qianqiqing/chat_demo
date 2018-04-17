package com.kedacom.demo.common.wesocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.kedacom.demo.model.User;

/**
 * WebSocket握手请求的拦截器. 检查握手请求和响应
 * @author 钱其清
 */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	@Override    
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> attributes) throws Exception {    
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
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
