package member.controller;

import java.sql.SQLException;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import member.domain.MemberVO;
import member.model.MemberDAO;
import member.model.MemberDAO_imple;

public class MemberRegister extends AbstractController {

	private MemberDAO mdao = null;
	
	public MemberRegister() {
		mdao = new MemberDAO_imple(); // 한번만 만들어놓으면 되기 때문에 MemberRegister의 기본생성자에서 생성
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String method = request.getMethod(); // "GET" 또는 "POST"
		
		if("GET".equalsIgnoreCase(method)) {
//			super.setRedirect(false);
			super.setViewPage("/WEB-INF/member/memberRegister.jsp");
			
		} else {
			// 변수 이름은 각 태그의 name 속성값으로 설정
			String name = request.getParameter("name");
			String userid = request.getParameter("userid");
			String pwd = request.getParameter("pwd");
			String email = request.getParameter("email");
			String hp1 = request.getParameter("hp1");
			String hp2 = request.getParameter("hp2");
			String hp3 = request.getParameter("hp3");
			String postcode = request.getParameter("postcode");
			String address = request.getParameter("address");
			String detailaddress = request.getParameter("detailaddress");
			String extraaddress = request.getParameter("extraaddress");
			String gender = request.getParameter("gender");
			String birthday = request.getParameter("birthday");
			
			String mobile = hp1 + hp2 + hp3;
			
			MemberVO member = new MemberVO();
			member.setUserid(userid);
			member.setPwd(pwd);
			member.setName(name);
			member.setEmail(email);
			member.setMobile(mobile);
			member.setPostcode(postcode);
			member.setAddress(address);
			member.setDetailaddress(detailaddress);
			member.setExtraaddress(extraaddress);
			member.setGender(gender);
			member.setBirthday(birthday);
			
			// === 회원가입이 성공되면 "회원가입 성공"이라는 alert를 띄우고 시작페이지로 이동한다. ===
		/*	
			String message = "";
			String loc = "";
			
			try {
				int n = mdao.registerMember(member);
				
				if(n == 1) {
					message = "회원가입 성공!";
					loc = request.getContextPath() + "/index.up"; // 시작 페이지로 이동한다.
				}
				
			} catch (SQLException e) {
				message = "회원가입 실패";
				loc = "javascript:history.back()"; // 자바스크립트를 이용하여 이전 페이지로 이동
				e.printStackTrace();
			}
			
			request.setAttribute("message", message);
			request.setAttribute("loc", loc);
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
		*/
			
			// ### 회원가입이 성공되면 자동으로 로그인되도록 한다. ###
			try {
				int n = mdao.registerMember(member);
				
				if(n == 1) {
					request.setAttribute("userid", userid);
					request.setAttribute("pwd", pwd);
					
					super.setRedirect(false);
					super.setViewPage("/WEB-INF/login/memberRegister_after_autoLogin.jsp"); // 자동 로그인할 페이지
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
				
				String message = "회원가입 실패";
				String loc = "javascript:history.back()"; // 자바스크립트를 이용하여 이전 페이지로 이동
				
				request.setAttribute("message", message);
				request.setAttribute("loc", loc);
				
				super.setRedirect(false);
				super.setViewPage("/WEB-INF/msg.jsp");
			}
			
		}
		
	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception ----------------------

}
