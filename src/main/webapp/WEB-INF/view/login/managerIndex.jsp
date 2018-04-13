<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="/demo/public/css/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="/demo/public/css/themes/icon.css">
<link rel="stylesheet" type="text/css" href="/demo/public/css/demo.css">
<script type="text/javascript" src="/demo/public/javascript/common/jquery.min.js"></script>
<script type="text/javascript" src="/demo/public/javascript/common/jquery.easyui.min.js"></script>
<style>
   *{
        margin: 0;
        padding: 0;
    }
    html,body{
        width: 100%;
        height: 100%;
    }
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>个人主页</title>
</head>
<body>
<div class="easyui-layout" style="width:100%;height:100%;">
	<div data-options="region:'north'" style="height:50px">
	    <div class="easyui-panel" style="padding:5px;">
		<a href="#" class="easyui-linkbutton" data-options="plain:true" id="diaAdd">Home</a>
		<a href="#" class="easyui-menubutton" data-options="menu:'#mm1',iconCls:'icon-edit'">Edit</a>
		<a href="#" class="easyui-menubutton" data-options="menu:'#mm2',iconCls:'icon-help'">Help</a>
		<a href="#" class="easyui-menubutton" data-options="menu:'#mm3'">About</a>
	</div>
	<div id="mm1" style="width:150px;">
		<div data-options="iconCls:'icon-undo'">Undo</div>
		<div data-options="iconCls:'icon-redo'">Redo</div>
		<div class="menu-sep"></div>
		<div>Cut</div>
		<div>Copy</div>
		<div>Paste</div>
		<div class="menu-sep"></div>
		<div>
			<span>Toolbar</span>
			<div>
				<div>Address</div>
				<div>Link</div>
				<div>Navigation Toolbar</div>
				<div>Bookmark Toolbar</div>
				<div class="menu-sep"></div>
				<div>New Toolbar...</div>
			</div>
		</div>
		<div data-options="iconCls:'icon-remove'">Delete</div>
		<div>Select All</div>
	</div>
	<div id="mm2" style="width:100px;">
		<div>Help</div>
		<div>Update</div>
		<div>About</div>
	</div>
	<div id="mm3" class="menu-content" style="background:#f0f0f0;padding:10px;text-align:left">
		<img src="http://www.jeasyui.com/images/logo1.png" style="width:150px;height:50px">
		<p style="font-size:14px;color:#444;">Try jQuery EasyUI to build your modern, interactive, javascript applications.</p>
	</div>
	</div>
	<div data-options="region:'south',split:true" style="height:50px;"></div>
	<div data-options="region:'east',split:true" title="East" style="width:180px;"></div>
	<div data-options="region:'west',split:true" title="West" style="width:100px;"></div>
	<div data-options="region:'center',iconCls:'icon-ok'" title="Center">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'north',split:true,border:false" style="height:50px"></div>
			<div data-options="region:'west',split:true,border:false" style="width:100px"></div>
			<div data-options="region:'center',border:false"></div>
		</div>
	</div>
</div>

<div id="dialog" closed="true" title="创建新用户">
    <form align="center">
       <table cellpadding="5">
    		<tr>
    			<td>用户名 :</td>
    			<td><input class="easyui-textbox" type="text" id="name" data-options="required:true"></input></td>
    		</tr>
    		<tr>
    			<td>密码 :</td>
    			<td><input class="easyui-textbox" type="password" id="password" data-options="required:true"></input></td>
    		</tr>
    		<tr>
    			<td>邮箱 :</td>
    			<td><input class="easyui-textbox" type="text" id="email" data-options="required:true"></input></td>
    		</tr>
    		<tr>
    			<td>分组 :</td>
    			<td><input class="easyui-textbox" type="text" id="group"></input></td>
    		</tr>
	   </table>
    </form>
    <div style="text-align:center;padding:5px">
    	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">提交</a>
    	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()">取消</a>
    </div>
</div>

<script>
    $(function(){
	    $( "#dialog" ).dialog({
	      autoOpen: false,
	      height: 350,
	      width: 300,
	      modal: true,
	      show: {
	        effect: "blind",
	        duration: 1000
	      },
	      hide: {
	        effect: "explode",
	        duration: 1000
	      }
	    });
	    
        $("#diaAdd").on("click",function(){
            $("#dialog").dialog("open");
        });
    });
    
    function submitForm(){
        debugger
        var user= {
            "id" : null,
            "name" : $("#name").val(),
            "password" : $("#password").val(),
            "email" : $("#email").val(),
            "photo" : "",
            "group" : $("#group").val(),
            "role" : null,
            "status" : 0
        };
        
        $.ajax({
            url : "/demo/userManage/createUser",
              type: "POST",
              data : user,
              dataType : "json",
              success : function(result){
                 $("#dialog").dialog("close");
              },
              error : function(e){
              
              }
        })
    }
</script>
</body>
</html>