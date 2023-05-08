package com.pj.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-SSO Server端 Controller 
 * @author click33
 *
 */
@RestController
public class TestController {

	// 首页 
	@RequestMapping("/")
	public Object index() {
		return new ModelAndView("index.html");
	}
	
	// 登录 
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue="10001") String id) {
		StpUtil.login(id);
		StpUtil.getSession().set("name", "zhangsan");
		return SaResult.ok();
	}

	// 注销 
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.ok();
	}
	
}
