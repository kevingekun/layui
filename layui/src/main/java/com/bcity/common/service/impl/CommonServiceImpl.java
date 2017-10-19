package com.bcity.common.service.impl;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bcity.common.service.CommonService;

@Repository
public class CommonServiceImpl implements CommonService {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public void text() {
		
	}

}
