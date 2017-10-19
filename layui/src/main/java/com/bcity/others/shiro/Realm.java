package com.bcity.others.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.bcity.common.model.User;
import com.bcity.common.service.UserService;

public class Realm extends AuthorizingRealm{

	@Autowired
	private UserService userService;
	
	// 为当前登陆成功的用户授予权限和角色，已经登陆成功了
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		String username = (String) arg0.getPrimaryPrincipal();//获取用户名
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setRoles(userService.getRoles(username));
		info.setStringPermissions(userService.getPermissions(username));
		return info;
	}

	// 验证当前登录的用户，获取认证信息
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {
		String username = (String) arg0.getPrincipal();//获取用户名
		User user = userService.getByUsername(username);
		if(user!=null){
			AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), "myRealm");
            return authcInfo;
		}else{
			return null;
		}
	}

}
