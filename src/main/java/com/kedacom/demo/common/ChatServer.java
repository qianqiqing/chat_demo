package com.kedacom.demo.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kedacom.demo.model.User;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket" , configurator = HttpSessionConfigurator.class)
public class ChatServer {
	private Logger logger = Logger.getLogger(this.getClass());
	
    private static int onlineCount = 0; //在线用户数
    private static CopyOnWriteArraySet<ChatServer> webSocketSet = new CopyOnWriteArraySet<ChatServer>();
    private Session session;            //与某个客户端的连接会话
    private String username;            //用户名
    private HttpSession httpSession;    //request的session

    private static List<String> list = new ArrayList();   //在线列表
    private static Map routetab = new HashMap();        //用户名和session的对应map
    
    private OutputStream outPut;
    
    private final String loadFilePath = "D:\\loadFiles\\";

    /**
     * 连接建立成功调用的方法
     * */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        this.session = session;
        webSocketSet.add(this); 
        addOnlineCount();           //在线数加1;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        User user = (User) httpSession.getAttribute("currentUser");    //获取当前用户
        username = user.getName();
        list.add(username);           //加入在线用户列表
        routetab.put(username, session);   //将用户名和session绑定到路由表
        String message = getMessage("[" + username + "]加入聊天室,当前在线人数为"+getOnlineCount()+"位", "notice",  list);
        broadcast(message);     //广播
    }

    /**
     * 连接关闭调用的方法
     * */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);    //从set中删除
        subOnlineCount();             //在线人数减1
        list.remove(username);        //从在线列表移除这个用户
        routetab.remove(username);
        String message = getMessage("[" + username +"]离开了聊天室,当前在线人数为"+getOnlineCount()+"位", "notice", list);
        broadcast(message);         //广播
    }
    
    /**
     * 接收客户端文字信息
     * */    
    @OnMessage
    public void receiveBigText(String _message, boolean last) {
        JSONObject chat = JSON.parseObject(_message);
        JSONObject message = JSON.parseObject(chat.get("message").toString());
        String type = chat.get("type").toString();
        String content = message.get("content").toString();
        
        try{
        	if("fileStart".equals(type)){                 //开始传输文件
            	outPut=new FileOutputStream(new File(loadFilePath + content));   //在服务端制定路径创建新的文件
            }else if("fileFinish".equals(type)){           //文件传输结束
            	outPut.close();
            	String messageContent = getMessage(message.get("from").toString() + " 说 :", "message", null);
            	if(message.get("to") == null || message.get("to").equals("")){      //如果to为空，发送给所有人;如果不为空,则对指定的用户发送消息
                	broadcast(messageContent);   
                	//发送图片
                	for(ChatServer chatServer : webSocketSet){
                		sendFile(content, chatServer.session);
                	}
                }else{
                    String [] userlist = message.get("to").toString().split(",");
                    singleSend(messageContent, (Session) routetab.get(message.get("from")));      //发送给自己,这个别忘了
                    for(String user : userlist){
                        if(!user.equals(message.get("from"))){
                            singleSend(messageContent, (Session) routetab.get(user));     //分别发送给每个指定用户
                            sendFile(content, (Session) routetab.get(user));
                        }
                    }
                }
            }else{
            	if(message.get("to") == null || message.get("to").equals("")){      //如果to为空，发送给所有人;如果不为空,则对指定的用户发送消息
                	broadcast(_message);   
                }else{
                    String [] userlist = message.get("to").toString().split(",");
                    singleSend(_message, (Session) routetab.get(message.get("from")));      //发送给自己,这个别忘了
                    for(String user : userlist){
                        if(!user.equals(message.get("from"))){
                            singleSend(_message, (Session) routetab.get(user));     //分别发送给每个指定用户
                        }
                    }
                }
            }
        }catch(Exception e){
        	
        }
        
        	
        	
        
    }
    
    /**
     * 接收客户端二进制文件
     * */   
    @OnMessage
    public void processStream(InputStream in)  {
    	try {  
    		ByteArrayOutputStream out=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024*4];
            int n=0;
            while ( (n=in.read(buffer)) !=-1) {
                out.write(buffer,0,n);
            }
    		outPut.write(buffer);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Throwable error){
        error.printStackTrace();
    }
    
    /**
     *  广播通知
     */
    public void broadcast(String _message){
    	try {
			for(ChatServer chatServer: webSocketSet){
			    chatServer.session.getBasicRemote().sendText(_message);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 对所有用户发送消息
     */
    public void multiSend(String _message){
    	JSONObject chat = JSON.parseObject(_message);
        JSONObject message = JSON.parseObject(chat.get("message").toString());
        broadcast(_message);
        for(ChatServer chatServer: webSocketSet){
			sendFile(message.get("content").toString(), chatServer.session);
		}
    }
    
    /**
     * 对单个用户发送消息
     */
    public void singleSend(String _message, Session session){
    	JSONObject chat = JSON.parseObject(_message);
        JSONObject message = JSON.parseObject(chat.get("message").toString());
        try {
        	if("message".equals(chat.get("type"))){
        		session.getBasicRemote().sendText(_message);
        	}else if("image".equals(chat.get("type"))){
        		sendFile(message.get("content").toString(), session);
        	}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendFile(String fileName, Session session){
    	FileInputStream input;  
        try {  
            File file=new File(loadFilePath + fileName);  
            input = new FileInputStream(file);  
            byte bytes[] = new byte[(int) file.length()];   
            input.read(bytes); 
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            session.getBasicRemote().sendBinary(buffer);
            input.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }      
    }
    
    /**
     * 组装返回给前台的消息
     */
    public String getMessage(String message, String type, List<String> list){
        JSONObject member = new JSONObject();
        member.put("message", message);
        member.put("type", type);
        member.put("list", list);
        return member.toString();
    }
    
    public  int getOnlineCount() {
        return onlineCount;
    }

    public  void addOnlineCount() {
        ChatServer.onlineCount++;
    }

    public  void subOnlineCount() {
        ChatServer.onlineCount--;
    }
    
}
