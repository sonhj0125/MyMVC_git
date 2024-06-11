package test.controller;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Test2Controller extends AbstractController {

//	public Test2Controller() {
//		System.out.println("$$$ 확인용 Test2Controller 클래스 생성자 호출");
//	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		super.setRedirect(true);
		super.setViewPage(request.getContextPath() + "/test1.up");
	}
}
