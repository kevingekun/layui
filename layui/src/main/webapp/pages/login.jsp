<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<title>登录</title>
<link rel="stylesheet" href="resources/layui/css/layui.css">
</head>
<body>
	<div class="layui-fluid">
		<div class="layui-row" style="margin-top: 200px;">
			<div class="layui-col-md4 layui-col-md-offset4">
				<fieldset class="layui-elem-field">
					<legend>登录</legend>
					<div class="grid-demo" style="padding: 20px;">
						<form class="layui-form" action="login/do" method="post">
							<div class="layui-form-item">
								<label for="title" class="layui-form-label">用户名</label>
								<div class="layui-input-inline">
									<input type="text" id="username" name="username" required
										lay-verify="required" placeholder="请输入用户名" autocomplete="off"
										class="layui-input">
								</div>
							</div>
							<div class="layui-form-item">
								<label class="layui-form-label">密码</label>
								<div class="layui-input-inline">
									<input type="password" name="password" required
										lay-verify="required" placeholder="请输入密码" autocomplete="off"
										class="layui-input">
								</div>
								<div class="layui-form-mid layui-word-aux"></div>
							</div>
							<div class="layui-form-item">
								<label class="layui-form-label">验证码</label>
								<div class="layui-input-inline">
									<input type="text" name="checkCode" id="checkCode" required
										lay-verify="required" autocomplete="off" maxlength="4" 
										class="layui-input" onkeyup="onValidateCode();">
									<img alt="点击切换" id="validationCode" onclick="refreshCode();"
											src="<%=request.getContextPath()%>/validationCode/captchaToken.action"
											 width="80" height="25" />
								</div>
								<div class="layui-form-mid layui-word-aux">
									<i id="vno" class="layui-icon" style="display:none; font-size: 30px; color: red;">&#x1006;</i>
									<i id="vyes" class="layui-icon" style="display:none; font-size: 30px; color: green;">&#xe605;</i>
								</div>
							</div>
							<div class="layui-form-item layui-col-md-offset1">
								<div class="layui-input-block">
									<button class="layui-btn" lay-submit lay-filter="formDemo">登录</button>
									<button type="reset" class="layui-btn layui-btn-primary">重置</button>
								</div>
							</div>
							<div class="layui-form-item ">
								<div class="layui-input-block">
									<font color="red">
										<c:out value="${error }"></c:out>
									</font>
								</div>
							</div>
						</form>
					</div>
				</fieldset>
			</div>
		</div>
	</div>
	<!-- 你的HTML代码 -->

	<script src="resources/layui/layui.js"></script>
	<script src="resources/js/jquery-3.1.1.min.js"></script>
	<script type="text/javascript">
	//一般直接写在一个js文件中
	layui.use([ 'layer', 'form','jquery' ], function() {
		var layer = layui.layer, form = layui.form;
		var $ = layui.$;
		//layer.msg('Hello World');
		//监听提交
		form.on('submit(formDemo)', function(data) {
			//layer.msg(JSON.stringify(data.field));
			if(onValidateCode()){
				return true;
			}else{
				return false;
			}
		});
		
	});
 
		/**
		 * 刷新验证码
		 * @param imgObj 验证码Img元素
		 */
		function refreshCode(imgObj) {
			if (!imgObj) {
				imgObj = document.getElementById("validationCode");
			}

			var index = imgObj.src.indexOf("?");
			if (index != -1) {
				var url = imgObj.src.substring(0, index + 1);
				imgObj.src = url + Math.random();
			} else {
				imgObj.src = imgObj.src + "?" + Math.random();
			}
		}
		/**
		       验证码反馈
		 **/
		function onValidateCode() {
			var b = false;
			var checkCode = document.getElementById("checkCode").value.replace(/(^\s*)|(\s*$)/g, '');
			if (checkCode.length == 4) {
				var url = '<%=request.getContextPath()%>/validationCode/equalsCode.action';
			$.ajax({
				type : 'post',
				async : false,
				url : url,
				data: {checkCode: checkCode},
				success : function(data) {
					if(data=='success'){
				    	$("#vyes").css("display", "block");
						$("#vno").css("display", "none");
						b = true;
				     }
				     if(data=='false'){
				    	$("#vyes").css("display", "none");
						$("#vno").css("display", "block");
						b = false;
						$("#checkCode").focus();
				     }
				},
				error:function(){
					b = false;
				}
			});
		}else{
			$("#vyes").css("display", "none");
			$("#vno").css("display", "none");
			b = false;
			$("#checkCode").focus();
		}
		return b;
	}
		
</script>
</body>
</html>