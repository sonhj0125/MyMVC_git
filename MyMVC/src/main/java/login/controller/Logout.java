package login.controller;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class Logout extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// 로그아웃 처리하기
		
		HttpSession session = request.getSession();	// 해당세션 불러오기
		
		
		
		
		
		
		
		
		
		
		
		///////////////////////////////////////////////////////////
		
		// 첫번째 방법 : 세션을 그대로 존재하게끔 해두고, 세션에 저장된 특정 값(지금은 로그인 된 회원객체)을 삭제하기
//		session.removeAttribute("loginuser");
				
		
		// 두번째 방법 : WAS 메모리 상에서 세션에 저장된 모든 데이터를 삭제하기 
		session.invalidate();	
		
		
		super.setRedirect(true);
		super.setViewPage(request.getContextPath()+"/index.up");
		
	}

}
