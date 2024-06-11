package common.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import member.domain.MemberVO;
import my.util.MyUtil;

public abstract class AbstractController implements InterCommand {
	/*
	    === 다음의 나오는 것은 우리끼리 한 약속이다. ===
	
	    ※ view 단 페이지(.jsp)로 이동 시 forward 방법(dispatcher)으로 이동시키고자 한다면 
	       자식클래스(/webapp/WEB-INF/Command.properties 파일에 기록된 클래스명들)에서는 부모클래스에서 생성해둔 메소드 호출 시 아래와 같이 하면 되게끔 한다.
	     
	    super.setRedirect(false); 
	    super.setViewPage("/WEB-INF/index.jsp");
	    
	    
	     ※ URL 주소를 변경하여 페이지 이동시키고자 한다면
	        즉, sendRedirect 를 하고자 한다면
	        자식클래스에서는 부모클래스에서 생성해둔 메소드 호출시 아래와 같이 하면 되게끔 한다.
	          
	    super.setRedirect(true);
	    super.setViewPage("registerMember.up");               
	*/
	
	private boolean isRedirect = false;
	// isRedirect 변수의 값이 false 이라면 view단 페이지(.jsp)로 forward 방법(dispatcher)으로 이동시키겠다. 
    // isRedirect 변수의 값이 true 이라면 sendRedirect 로 페이지 이동을 시키겠다.
	
	private String viewPage;
	// viewPage 는 isRedirect 값이 false 이라면 view단 페이지(.jsp)의 경로명 이고,
    // isRedirect 값이 true 이라면 이동해야 할 페이지 URL 주소 이다.

	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getViewPage() {
		return viewPage;
	}

	public void setViewPage(String viewPage) {
		this.viewPage = viewPage;
	}
	
	
	
	//////////////////////////////////////////////////////
	// 로그인 유무를 검사해서 로그인을 했으면 true 를 리턴해주고
	// 로그인을 하지 않았으면 false 를 리턴해주도록 한다.
	public boolean checkLogin(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
		
		if(loginuser != null) {
			// 로그인 한 경우
			return true;
			
		} else {
			// 로그인 하지 않은 경우
			return false;
		}
		
	} // end of public boolean checkLogin(HttpServletRequest request) ---------------
	
	

	// 로그인 또는 로그아웃을 하면 시작페이지로 가는 것이 아니라 방금 보았던 그 페이지로 그대로 가기 위한 것임.(공통적이므로 부모클래스에서 해줌)
	public void goBackURL(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		session.setAttribute("goBackURL", MyUtil.getCurrentURL(request));	// CurrentURL => *** ? 다음의 데이터까지 포함한 현재 URL 주소를 알려주는 메소드를 생성한 것
		
		
		
		
	} // end of public void goBackURL(HttpServletRequest request)
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
