package com.bcity.common.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import com.bcity.common.model.Permission;
import com.bcity.common.model.User;
import com.bcity.common.service.UserService;
import com.wb.jdbcutils.CommonJdbcUtils;
@Repository
public class UserServiceImpl implements UserService {

	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public User getByUsername(String username) {
		User u= CommonJdbcUtils.queryFirst("select * from TUSER a where a.username=?", User.class, username);
		return u;
	}

	@Override
	public Set<String> getRoles(String username) {
		String sql = "select a.name from trole a,tuser b where a.role_id=b.role_id and b.username=?";
		List<String> list = jdbcTemplate.queryForList(sql,new Object[]{username}, String.class);
		Set<String> set = new HashSet<String>();
		set.addAll(list);
		return set;
	}
	
	@Override
	public Set<String> getPermissions(String username) {
		String sql = "select c.permission_name from tuser a,trole b,tpermission c where a.role_id=b.role_id and c.role_id=b.role_id and a.username=?";
		List<String> list = jdbcTemplate.queryForList(sql,new Object[]{username}, String.class);
		//mysql 下只能查出一条数据。
		//List<String> list = CommonJdbcUtils.query("select c.permission_name from tuser a,trole b,tpermission c where a.role_id=b.role_id and c.role_id=b.role_id and a.username=?", String.class, username);
		Set<String> set = new HashSet<String>();
		set.addAll(list);
		return set;
	}

}
