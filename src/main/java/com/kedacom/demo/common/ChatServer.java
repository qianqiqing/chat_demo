package com.kedacom.demo.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kedacom.demo.model.User;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket" , configurator = HttpSessionConfigurator.class)
public class ChatServer {
    private static int onlineCount = 0; //在线用户数
    private static CopyOnWriteArraySet<ChatServer> webSocketSet = new CopyOnWriteArraySet<ChatServer>();
    private Session session;            //与某个客户端的连接会话
    private String username;            //用户名
    private HttpSession httpSession;    //request的session

    private static List<String> list = new ArrayList();   //在线列表
    private static Map routetab = new HashMap();        //用户名和websocket的session绑定的路由表

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
     * 接收客户端的消息
     * */    
    @OnMessage
    public void onMessage(String _message) {
        JSONObject chat = JSON.parseObject(_message);
        JSONObject message = JSON.parseObject(chat.get("message").toString());
        if(message.get("to") == null || message.get("to").equals("")){      //如果to为空,则广播;如果不为空,则对指定的用户发送消息
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
    
    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Throwable error){
        error.printStackTrace();
    }
    
    /**
     * 广播消息
     */
    public void broadcast(String message){
        for(ChatServer chat: webSocketSet){
            try {
                chat.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
    
    /**
     * 对特定用户发送消息
     */
    public void singleSend(String message, Session session){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
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
