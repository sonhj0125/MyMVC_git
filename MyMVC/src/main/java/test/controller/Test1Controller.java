package test.controller;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Test1Controller extends AbstractController {
	
//	public Test1Controller() {
//		System.out.println("### 확인용 Test1Controller 클래스 생성자 호출");
//	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute("name", "김다영");
		
//		super.setRedirect(false);
		super.setViewPage("/WEB-INF/test/test1.jsp");
	}
}
