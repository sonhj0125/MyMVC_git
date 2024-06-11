package member.controller;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import member.domain.MemberVO;
import member.model.MemberDAO;
import member.model.MemberDAO_imple;

public class MemberOneDetail extends AbstractController {

	private MemberDAO mdao = null;
	
	public MemberOneDetail() {
		mdao = new MemberDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// === 관리자(admin)로 로그인 했을때만 조회가 가능하도록 한다. === //
		HttpSession session = request.getSession();
		
		MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
		
		if(loginuser != null && "admin".equals(loginuser.getUserid())) {
			// 관리자(admin)로 로그인 했을 경우
			
			String method = request.getMethod();
			
			if("POST".equalsIgnoreCase(method)) {
				// POST 방식일 때
				
				String userid = request.getParameter("userid");
				String goBackURL = request.getParameter("goBackURL");
				
				// System.out.println("goBackURL => " + goBackURL);
				// goBackURL => /member/memberList.up?searchType=name&searchWord=%EC%9C%A0&sizePerPage=5
				
				MemberVO mvo = mdao.selectOneMember(userid);
				
				request.setAttribute("mvo", mvo);
				request.setAttribute("goBackURL", goBackURL);
				
				super.setRedirect(false);
				super.setViewPage("/WEB-INF/member/admin/memberOneDetail.jsp");
				
			}
			
			
		}
		else {
			// 관리자(admin)로 로그인 하지 않은 경우 또는 로그인을 하지 않은 경우
			
			
			
			
		}
		
		
		
		
		

	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception

}
