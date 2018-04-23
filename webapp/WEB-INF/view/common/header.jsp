<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="am-topbar am-topbar-inverse">
    <div class="am-topbar-brand">
        <i class="am-icon-weixin"></i> <strong>网页聊天室</strong>
    </div>
    <div class="am-collapse am-topbar-collapse" id="doc-topbar-collapse">
	    <ul class="am-nav am-nav-pills am-topbar-nav">
	      <li class="am-active" id="usermanage_node"><a id="usermanage" href="#">用户管理</a></li>
	      <li id="chatroom_node"><a id="chatroom" href="#">聊天室</a></li>
	    </ul>
	    
	    <div class="am-topbar-right">
	      <div class="am-dropdown" data-am-dropdown="{boundary: '.am-topbar'}">
	        <button class="am-btn am-btn-secondary am-topbar-btn am-btn-sm am-dropdown-toggle" data-am-dropdown-toggle><span class="am-icon-users"></span> ${currentUser.name} <span class="am-icon-caret-down"></span></button>
	        <ul class="am-dropdown-content">
	          <li><a href="javascript:createUser();"><span class="am-icon-user"></span> 创建用户</a></li>
			  <li><a href="javascript:createGroup();"><span class="am-icon-user"></span> 新建分组</a></li>
	        </ul>
	      </div>
    </div>
    </div>
</header>

<div class="am-popup" id="my-popup" style="width:600px;height:420px">
	<div class="am-popup-inner">
	    <div class="am-popup-hd">
	      <h4 class="am-popup-title">创建用户</h4>
	      <span data-am-modal-close
	            class="am-close">&times;
	      </span>
	    </div>
	    <div class="am-popup-bd">
		     <form class="am-form am-form-horizontal" >
		        <fieldset>
				  <div class="am-form-group">
				    <label for="doc-ipt-3" class="am-u-sm-2 am-form-label">用户名</label>
				    <div class="am-u-sm-10">
				      <input type="text" id="name" placeholder="输入用户名">
				    </div>
				  </div>
				
				  <div class="am-form-group">
				    <label for="doc-ipt-pwd-2" class="am-u-sm-2 am-form-label">密码</label>
				    <div class="am-u-sm-10">
				      <input type="password" id="password" placeholder="设置一个密码">
				    </div>
				  </div>
				  
				  <div class="am-form-group">
				    <label for="doc-ipt-3" class="am-u-sm-2 am-form-label">邮箱</label>
				    <div class="am-u-sm-10">
				      <input type="email" id="email" placeholder="输入邮箱">
				    </div>
				  </div>
				  
				  <div class="am-form-group">
				    <label for="doc-ipt-3" class="am-u-sm-2 am-form-label">角色</label>
				    <div class="am-form-group am-form-select am-u-sm-10">
					    <select id="role" class="">
					      <option value="1">管理员</option>
					      <option value="0">普通用户</option>
					    </select>
					  </div>
				  </div>
				  </fieldset>
				  
				  <button type="button" class="am-btn am-btn-primary am-btn-block" onclick="submitForm()">创建用户</button>
		    </form>
	    </div>
	</div>
</div>
<div class="am-modal am-modal-alert" tabindex="-1" id="success">
  <div class="am-modal-dialog">
    <div class="am-modal-hd">创建用户成功！</div>
    <div class="am-modal-footer">
      <span class="am-modal-btn">确定</span>
    </div>
  </div>
</div>
<script>
    var $modal = $("#my-popup");
    var $modelSuccess = $("#success");
    function createUser(){
	    $modal.modal();
    }
    
    function submitForm(){
        var user= {
            "id" : null,
            "name" : $("#name").val(),
            "password" : $("#password").val(),
            "email" : $("#email").val(),
            "photo" : "",
            "group" : null,
            "role" : $("#role").val(),
            "status" : 0
        };
        
        $.ajax({
            url : "/demo/userManage/createUser",
              type: "POST",
              data : user,
              success : function(result){
            	  debugger
            	  $modal.modal('close');
            	  $modelSuccess.modal();
              },
              error : function(e){
            	  debugger
            	  $modal.modal('close');
            	  $modelSuccess.modal();
              }
        })
    }
    
</script>