<link rel="stylesheet" type="text/css" href="public/css/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="public/css/themes/icon.css">
<link rel="stylesheet" type="text/css" href="public/css/demo.css">
<script type="text/javascript" src="public/javascript/common/jquery.min.js"></script>
<script type="text/javascript" src="public/javascript/common/jquery.easyui.min.js"></script>

<div align="center">
	<h2 align="center">登录</h2>
	<p align="center">Fill the form and submit it.</p>
	<div style="margin:20px 0;"></div>
	<div class="easyui-panel" title="New Topic" style="width:400px">
		<div style="padding:10px 60px 20px 60px">
	    	<table cellpadding="5">
	    		<tr>
	    			<td>Name:</td>
	    			<td><input class="easyui-textbox" type="text" id="name" data-options="required:true"></input></td>
	    		</tr>
	    		<tr>
	    			<td>Email:</td>
	    			<td><input class="easyui-textbox" type="password" id="password" data-options="required:true"></input></td>
	    		</tr>
	    		
	    	</table>
		    <div style="text-align:center;padding:5px">
		    	<a href="javascript:void(0)" id="submit" class="easyui-linkbutton">Submit</a>
		    	<a href="javascript:void(0)" id="clear" class="easyui-linkbutton">Clear</a>
		    </div>
		    <div id = "errMsg" align="center">
		        
		    </div>
	    </div>
	</div>
</div>
<script>
    $(document).ready(function(){
       $("#submit").on("click",function(){
       debugger
           var name = $("#name").val();
           var password = $("#password").val();
           $.ajax({
              url : "/demo/login/validate",
              type : "post",
              data : {
                  name : name,
                  password : password
              },
              success : function(result){
              debugger
                  if(result == null || result == ""){
                      window.location.href = "/demo/login/managerIndex";
                  }else{
                      $("#errMsg").html(result);
                  }
              },
              error : function(e){
              
              }
           })
       });
       
       $("#clear").on("click",function(){
       
       })
    });

</script>
