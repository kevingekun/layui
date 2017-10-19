package com.wb.exceptions;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class CustomExceptionResolver extends SimpleMappingExceptionResolver{
	@Override  
	protected ModelAndView doResolveException(HttpServletRequest request,  
	          HttpServletResponse response, Object handler, Exception ex){
		String viewName = determineViewName(ex,request);
		if(viewName==null||ex instanceof BusinessException){
				try {
					response.setContentType("text/html; charset=utf-8");  
					PrintWriter writer = response.getWriter();
					writer.write(ex.getMessage());
					writer.flush();
					ex.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				/**
				 1.在servlet中输出中文，如果采用PrintWriter方式，需要在调用getPrintWriter()之前
				 调用setContentType 或者 setCharacterEncoding；
				 2.setContentType 和 setCharacterEncoding两方法中设定characterEncoding的方法
				 对服务器效果一致，不需要反复调用。在输出文本内容时， 采用response.setContentType
				 ("text/html; charset=utf-8");
				 3.PrintWriter自身并没有处理编码的职责，它还是应该看成一个装饰器比较好：它就是为了
				 输出更方便而设计的，提供print、println、printf等便利方法。要设置编码的话，可以在它的
				 底层Writer上设置：（这里以OutputStreamWriter为底层Writer。
				 * */
				//AJAX格式返回
				Integer statusCode = determineStatusCode(request, viewName);  
				if(statusCode!=null)
					applyStatusCodeIfPossible(request,response,statusCode);
				ex.printStackTrace();
				return getModelAndView(viewName,ex,request);
			}
		return null;
	}
}
