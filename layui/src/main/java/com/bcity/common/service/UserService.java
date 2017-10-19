package com.bcity.common.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.bcity.common.model.User;

@Service
public interface UserService {
	
	public User getByUsername(String username);

    public Set<String> getRoles(String username);

    public Set<String> getPermissions(String username);
}
