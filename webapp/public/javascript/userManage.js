var usermanage = $("#usermanage_node");
var chatroom = $("#usermanage_node");
var userManage = {
		init : function(){
			//用户管理菜单按钮事件
			$("#usermanage_node").on("click",function(){
				userManage.index();
			});
			
			//聊天室菜单按钮事件
			$("#usermanage_node").on("click",function(){
				$.ajax({
					url : webDemo.formatUrl("userManage/index"),
					type : "GET",
					success : function(result){
						$(this).parentElement.addClass("am-active");
						usermanage.removeClass("am-active");
						$("#main_content").html(result);
					},
					error : function(e){
						
					}
				});
			});
		},
		
		index : function() {
			$.ajax({
				url : webDemo.formatUrl("/userManage/index"),
				type : "GET",
				success : function(result){
					$(this).addClass(".am-active");
					chatroom.removeClass(".am-active");
					$("#main_content").html(result);
				},
				error : function(e){
					
				}
			});
		}
};