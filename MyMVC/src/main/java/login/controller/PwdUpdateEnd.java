package login.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import member.model.MemberDAO;
import member.model.MemberDAO_imple;

public class PwdUpdateEnd extends AbstractController {

	private MemberDAO mdao = null;
	
	public PwdUpdateEnd() {
		mdao = new MemberDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String userid = request.getParameter("userid");
		
		String method = request.getMethod(); // "GET" 또는 "POST" 
		
		if("POST".equalsIgnoreCase(method)) {
			// "암호변경하기" 버튼을 클릭했을 경우
			
			String new_pwd = request.getParameter("pwd");
			
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("userid", userid);
			paraMap.put("new_pwd", new_pwd);
			
			int n = 0;
			
			try {
				n = mdao.pwdUpdate(paraMap); // 보낼 변수(파라미터)가 2개 이상일 경우 Map 사용하기!!!
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			request.setAttribute("n", n);
			// n ==> 1  또는  n ==> 0
			
		} // end of if("POST".equalsIgnoreCase(method)) ----------------
		
		
		request.setAttribute("userid", userid);
		request.setAttribute("method", method);
		
//		super.setRedirect(false);
		super.setViewPage("/WEB-INF/login/pwdUpdateEnd.jsp");
	}

}
