package com.bcity.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bcity.common.model.User;

@RequestMapping(value = "/login")
@Controller
public class LoginAction {

	private static final Logger logger = Logger.getLogger(LoginAction.class);
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;

	@RequestMapping(value = "/do")
	public ModelAndView login(User user) {
		ModelAndView m = new ModelAndView();
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
		try {
			subject.login(token);
			request.getSession().setAttribute("user", user);
			m.setViewName("personal/personal_index");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			request.setAttribute("error", "用户名或密码错误！");
			m.setViewName("login");
		}
		return m;
	}
}
