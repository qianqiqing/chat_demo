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
    private static int onlineCount = 0; //�����û���
    private static CopyOnWriteArraySet<ChatServer> webSocketSet = new CopyOnWriteArraySet<ChatServer>();
    private Session session;            //��ĳ���ͻ��˵����ӻỰ
    private String username;            //�û���
    private HttpSession httpSession;    //request��session

    private static List<String> list = new ArrayList();   //�����б�
    private static Map routetab = new HashMap();        //�û�����websocket��session�󶨵�·�ɱ�

    /**
     * ���ӽ����ɹ����õķ���
     * */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        this.session = session;
        webSocketSet.add(this); 
        addOnlineCount();           //��������1;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        User user = (User) httpSession.getAttribute("currentUser");    //��ȡ��ǰ�û�
        username = user.getName();
        list.add(username);           //���������û��б�
        routetab.put(username, session);   //���û�����session�󶨵�·�ɱ�
        String message = getMessage("[" + username + "]����������,��ǰ��������Ϊ"+getOnlineCount()+"λ", "notice",  list);
        broadcast(message);     //�㲥
    }

    /**
     * ���ӹرյ��õķ���
     * */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);    //��set��ɾ��
        subOnlineCount();             //����������1
        list.remove(username);        //�������б��Ƴ�����û�
        routetab.remove(username);
        String message = getMessage("[" + username +"]�뿪��������,��ǰ��������Ϊ"+getOnlineCount()+"λ", "notice", list);
        broadcast(message);         //�㲥
    }
    
    /**
     * ���տͻ��˵���Ϣ
     * */    
    @OnMessage
    public void onMessage(String _message) {
        JSONObject chat = JSON.parseObject(_message);
        JSONObject message = JSON.parseObject(chat.get("message").toString());
        if(message.get("to") == null || message.get("to").equals("")){      //���toΪ��,��㲥;�����Ϊ��,���ָ�����û�������Ϣ
            broadcast(_message);
        }else{
            String [] userlist = message.get("to").toString().split(",");
            singleSend(_message, (Session) routetab.get(message.get("from")));      //���͸��Լ�,���������
            for(String user : userlist){
                if(!user.equals(message.get("from"))){
                    singleSend(_message, (Session) routetab.get(user));     //�ֱ��͸�ÿ��ָ���û�
                }
            }
        }
    }
    
    /**
     * ��������ʱ����
     */
    @OnError
    public void onError(Throwable error){
        error.printStackTrace();
    }
    
    /**
     * �㲥��Ϣ
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
     * ���ض��û�������Ϣ
     */
    public void singleSend(String message, Session session){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ��װ���ظ�ǰ̨����Ϣ
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
