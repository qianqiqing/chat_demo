package com.kedacom.demo.common.wesocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * webSocket配置类
 * @author 钱其清
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		//注册webSocket的实现类
		registry.addHandler(ChatRoom(), "/chat.sc").addInterceptors(handshakeInterceptor());   
	}
	
	@Bean    
    public HandshakeInterceptor handshakeInterceptor() {    
        return new HandshakeInterceptor();    
    }    
      
    @Bean    
    public ChatRoom ChatRoom() {    
        return new ChatRoom();    
    }  
	
}
