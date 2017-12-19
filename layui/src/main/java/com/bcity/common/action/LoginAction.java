package com.bcity.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
    public ModelAndView login(){
        logger.info("======用户进入了ShiroController的/login.html");
        ModelAndView m = new ModelAndView();
        m.setViewName("login");
        return m;
    }

	@RequestMapping(value = "/do")
	public ModelAndView login(User user) {
		ModelAndView m = new ModelAndView();
		Subject subject = SecurityUtils.getSubject();
		String msg = "";
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
		m.setViewName("login");
		try {
			subject.login(token);
			if(subject.isAuthenticated()){
				request.getSession().setAttribute("user", user);
				SavedRequest savedRequest = WebUtils.getSavedRequest(request);
                // 获取保存的URL
                if (savedRequest == null || savedRequest.getRequestUrl() == null) {
                    m.setViewName("admin/home");
//                    m.setViewName("personal/personal_index");
                } else {
                    m.setViewName("forward:" + savedRequest.getRequestUrl());
                }
			}else{
				m.setViewName("login");
			}
		} catch (IncorrectCredentialsException e) {
            msg = "登录密码错误. Password for account " + token.getPrincipal() + " was incorrect.";
            m.addObject("message", msg);
            System.out.println(msg);
        } catch (ExcessiveAttemptsException e) {
            msg = "登录失败次数过多";
            m.addObject("message", msg);
            System.out.println(msg);
        } catch (LockedAccountException e) {
            msg = "帐号已被锁定. The account for username " + token.getPrincipal() + " was locked.";
            m.addObject("message", msg);
            System.out.println(msg);
        } catch (DisabledAccountException e) {
            msg = "帐号已被禁用. The account for username " + token.getPrincipal() + " was disabled.";
            m.addObject("message", msg);
            System.out.println(msg);
        } catch (ExpiredCredentialsException e) {
            msg = "帐号已过期. the account for username " + token.getPrincipal() + "  was expired.";
            m.addObject("message", msg);
            System.out.println(msg);
        } catch (UnknownAccountException e) {
            msg = "帐号不存在. There is no user with username of " + token.getPrincipal();
            m.addObject("message", msg);
            System.out.println(msg);
        } catch (UnauthorizedException e) {
            msg = "您没有得到相应的授权！" + e.getMessage();
            m.addObject("message", msg);
            System.out.println(msg);
        }
		return m;
	}
	
	@RequestMapping(value = "/logout")
    public String doLogout(HttpServletRequest request, Model model) {
        logger.info("======用户"+request.getSession().getAttribute("user")+"退出了系统");
        SecurityUtils.getSubject().logout();
        return "redirect:login";
    }
}
