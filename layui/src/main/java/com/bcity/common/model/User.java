package com.bcity.common.model;

import java.util.Date;

import com.wb.jdbcutils.annos.Column;
import com.wb.jdbcutils.annos.Table;

@Table(name = "USER")
public class User {

	private long user_id;
	private String username;
	private String password;
	private String nickname;
	private String phone;
	private String email;
	private long city_id;
	private Date register_date;
	private long login_count;
	private String valid;

	@Column(name = "user_id", length = 18)
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	@Column(name = "username", length = 30)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", length = 64)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "nickname", length = 30)
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name = "phone", length = 16)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "city_id")
	public long getCity_id() {
		return city_id;
	}

	public void setCity_id(long city_id) {
		this.city_id = city_id;
	}

	@Column(name = "register_date")
	public Date getRegister_date() {
		return register_date;
	}

	public void setRegister_date(Date register_date) {
		this.register_date = register_date;
	}

	@Column(name = "login_count")
	public long getLogin_count() {
		return login_count;
	}

	public void setLogin_count(long login_count) {
		this.login_count = login_count;
	}

	@Column(name = "valid")
	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

}
