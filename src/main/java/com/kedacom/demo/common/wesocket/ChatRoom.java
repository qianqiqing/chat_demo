package com.kedacom.demo.common.wesocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kedacom.demo.model.User;

/**
 * 继承AbstractWebSocketHandler实现webSocket通信
 * @author 钱其清
 *
 */
public class ChatRoom extends AbstractWebSocketHandler {
	private Logger logger = Logger.getLogger(this.getClass());
		
	public static final String loadFilePath = "D:\\loadFiles\\";
	public static List<Integer> onLineUserIds = new ArrayList();
	private final static List<WebSocketSession> sessionList = Collections.synchronizedList(new ArrayList<WebSocketSession>());
    private static List<String> list = new ArrayList();                        //在线列表
    private static Map<String,WebSocketSession> routetab = new HashMap();      //用户名和session的对应map
    private static int onlineCount = 0;                                        //在线用户数
    private String username;                                                   //当前用户名
    private User currentUser;
   
	private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private FileOutputStream outPut;
	 
    /**
     * 连接建立成功调用的方法,重写父类AbstractWebSocketHandler中的方法
     * @param WebSocketSession
     */
	@Override    
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {  
		sessionList.add(webSocketSession);  
		addOnlineCount(); 
		currentUser = (User) webSocketSession.getAttributes().get("currentUser"); //获取当前用户
        username = currentUser.getName();
        list.add(username);
        onLineUserIds.add(currentUser.getId());
        routetab.put(username, webSocketSession);                               //将用户名和session绑定到路由表
        TextMessage message = getMessage("[" + username + "]加入聊天室,当前在线人数为"+getOnlineCount()+"位", "notice",  list);
        broadcast(message);                                                     //广播通知
    }    
	
	/**
     * 连接关闭调用的方法,重写父类AbstractWebSocketHandler中的方法
     * @param webSocketSession
     * @param status
     */
	@Override    
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {    
        sessionList.remove(webSocketSession);  
        subOnlineCount();  
        list.remove(username);
        onLineUserIds.remove(currentUser.getId());
        routetab.remove(username);
        TextMessage message = getMessage("[" + username +"]离开了聊天室,当前在线人数为"+getOnlineCount()+"位", "notice", list);
        broadcast(message);   
    }
	
	/**
     * 处理客户端发送端的消息,重写父类AbstractWebSocketHandler中的方法
     * @description 客户端发送的是一个组装的json字符串，包含发送人，接收人，消息类型等  
     * @param webSocketSession
     * @param message
     */
	@Override  
    public void handleTextMessage(WebSocketSession websocketsession, TextMessage message) {  
        String payload = message.getPayload();  
        JSONObject jsonObject = JSON.parseObject(payload);
        JSONObject messageObject = JSON.parseObject(jsonObject.get("message").toString());
        String type = jsonObject.get("type").toString();
        String content = messageObject.get("content").toString();
        TextMessage textMessage;
        String htmlMessage;
        try {
        	if ("fileStart".equals(type)) {                                       //开始传输文件
        		File file = new File(loadFilePath);
        		if (!file.exists()) {
        			file.mkdirs();
        		}
            	outPut=new FileOutputStream(new File(loadFilePath + content));    //在服务端制定路径创建新的文件
            } else if ("fileFinish".equals(type)) {                               //文件传输结束
            	outPut.close();
            	if ("image".equals(messageObject.get("fileType").toString())) {   //如果是图片，返回客户端的是  文字信息+图片二进制，分两次发送
            		htmlMessage = messageObject.get("from").toString() + " ("+format.format(new Date())+")" + " 说 :";
            		textMessage = getMessage(htmlMessage, "message", null);
            		
            		sendMessage(textMessage, messageObject);
            	} else {                                                           //文件则发送一个可下载的文件名链接
            		String html = "<a href=\"#\" onclick=\"downLoad('"+content+"')\">"+content+"</a></br>";
            		htmlMessage = messageObject.get("from").toString() + " ("+format.format(new Date())+")" + " 发送文件 : " + html;
            		textMessage = getMessage(htmlMessage, "message", null);
            		
            		sendMessage(textMessage, messageObject);
            	}
            	
            } else {
            	htmlMessage = messageObject.get("from").toString() + " ("+format.format(new Date())+")" + " 说 : " + content +"</br>";
            	textMessage = getMessage(htmlMessage, "message", null);
            	
            	sendMessage(textMessage, messageObject);
            }
        } catch (Exception e) {
        	logger.error("handleTextMessage exception:"+e);
        }
    }  
	
	/**
     * 处理客户端发送的BinaryMessage,重写父类AbstractWebSocketHandler中的方法
     * @param session
     * @param message
     */
	@Override  
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {  
        ByteBuffer buffer = message.getPayload();  
        try {  
            outPut.write(buffer.array());  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
	
	/**
     * webSocket消息传输异常处理,重写父类AbstractWebSocketHandler中的方法
     * @param webSocketSession
     * @param throwable
     */
	@Override    
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {    
        if (webSocketSession.isOpen()) {    
            webSocketSession.close();    
        }    
    }
	
	/**
     * 支持分段发送,重写父类AbstractWebSocketHandler中的方法
     * @return [boolean] [设为true表示支持分段发送]
     */
	@Override    
    public boolean supportsPartialMessages() {    
        return true;    
    }  
	
	/**
     * 发送消息
     * @description 如果是图片，返回客户端的是 发送人信息+图片二进制，分两次发送；如果是文件则返回文件的链接
     */
	private void sendMessage(TextMessage message, JSONObject messageObject) {
		String fileType = messageObject.get("fileType").toString();
		if (messageObject.get("to") == null || messageObject.get("to").equals("")) {      //如果to为空，发送给所有人;如果不为空,则对指定的用户发送消息
        	broadcast(message);
        	if ("image".equals(fileType)) {                                               //如果是图片，必须将图片的二进制数据传回客户端展示
        		//发送图片
            	for (WebSocketSession session : sessionList) {
            		sendFile(messageObject.get("content").toString(), session);
            	}
        	}
        } else {
            String [] userlist = messageObject.get("to").toString().split(",");
            sendText(message, (WebSocketSession) routetab.get(messageObject.get("from")));//发送给自己,这个别忘了
            if ("image".equals(fileType)) { 
            	sendFile(messageObject.get("content").toString(), (WebSocketSession) routetab.get(messageObject.get("from")));
            }
            for (String user : userlist) {
                if (!user.equals(messageObject.get("from"))) {
                	sendText(message, (WebSocketSession) routetab.get(user));             //分别发送给每个指定用户
                	if ("image".equals(fileType)) { 
                		sendFile(messageObject.get("content").toString(), (WebSocketSession) routetab.get(user));
                	}
                }
            }
        }
	}
	
	/**
     * 发送文本信息
     * @param [message]
     * @param [session]
     */
	private void sendText(TextMessage message, WebSocketSession session) {
		try {
			session.sendMessage(message);
		} catch (Exception e) {
			logger.error("发送消息失败:"+e);
		}
	}
       
	/**
     * 发送文件的二级制信息；
     * @param [fileName]
     * @param [session]
     */
    private void sendFile(String fileName, WebSocketSession session) {  
        FileInputStream input;  
        try {  
            File file = new File(loadFilePath + fileName);  
            input = new FileInputStream(file);  
            byte bytes[] = new byte[(int) file.length()];   
            input.read(bytes);  
            BinaryMessage byteMessage = new BinaryMessage(bytes);  
            session.sendMessage(byteMessage);  
            input.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     * 发送信息给所有人
     * @param [_message]
     */
    private void broadcast(TextMessage _message) {
    	try {
			for (WebSocketSession session: sessionList) {
			    session.sendMessage(_message);
			}
		} catch (IOException e) {
			logger.error("broadcast error :" + e);
		}
    }
    
    /**
     * 发送信息给所有人
     * @param [_message]
     */
    private TextMessage getMessage(String message, String type, List<String> list) {
        JSONObject member = new JSONObject();
        member.put("message", message);
        member.put("type", type);
        member.put("list", list);
        TextMessage textMessage = new TextMessage(member.toString());
        return textMessage;
    }
    
    /**
     * 获取在线用户数
     * @return
     */
    private int getOnlineCount() {
        return onlineCount;
    }

    private void addOnlineCount() {
        onlineCount++;
    }

    private void subOnlineCount() {
        onlineCount--;
    }

}
