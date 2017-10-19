package com.bcity.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bcity.common.service.CommonService;

@RequestMapping(value = "/common")
@Controller
public class CommonAction {

	private static final Logger logger = Logger.getLogger(CommonAction.class);

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;
	@Autowired
	private CommonService commonService;

	@RequestMapping(value = "/resetPsw")
	public ModelAndView resetPsw() {
		ModelAndView m = new ModelAndView();
		m.setViewName("common/resetPsw");
		return m;
	}
}
