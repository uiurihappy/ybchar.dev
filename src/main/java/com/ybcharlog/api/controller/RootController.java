package com.ybcharlog.api.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController implements ErrorController {

	// 에러 발생 시 index 페이지 리다이렉트
	@GetMapping("/error")
	public String redirectRoot() {
		return "index.html";
	}

	public String getErrorPath(){
		return null;
	}
}
