package login.controller;

import java.util.HashMap;
import java.util.Map;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import member.domain.MemberVO;
import member.model.MemberDAO;
import member.model.MemberDAO_imple;

public class Login extends AbstractController {
	
	private MemberDAO mdao = null;
	
	public Login() {
		mdao = new MemberDAO_imple();
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String method = request.getMethod(); // "GET" 또는 "POST"
		
		if(!"POST".equalsIgnoreCase(method)) {
			// POST 방식으로 넘어온 것이 아니라면
			
			String message = "비정상적인 경로로 들어왔습니다.";
			String loc = "javascript:history.back()";
			
			request.setAttribute("message", message);
			request.setAttribute("loc", loc);
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
			
			return; // execute(HttpServletRequest request, HttpServletResponse response) 메소드 종료
					// return 을 적어주었기 때문에 밑에 else를 적어주지 않아도 된다.
				
		}
		
		// POST 방식으로 넘어온 것이라면
		String userid = request.getParameter("userid");
		String pwd = request.getParameter("pwd");
		
		// === 클라이언트의 IP 주소를 알아오는 것 === //
		String clientip = request.getRemoteAddr();
		// 먼저, C:\NCS\workspace_jsp\MyMVC\src\main\webapp\JSP 파일을 실행시켰을 때 IP 주소가 제대로 출력되기위한 방법.txt 참조할 것
		
	/*	
		System.out.println("~~~ 확인용 userid : " + userid);
		System.out.println("~~~ 확인용 pwd : " + pwd);
		System.out.println("~~~ 확인용 clientip : " + clientip);
	*/	
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("pwd", pwd);
		paraMap.put("clientip", clientip);
		
		MemberVO loginuser = mdao.login(paraMap);
		
		if(loginuser != null) {
			// 로그인 성공 시
			
//			System.out.println("~~~ 확인용 로그인 성공 ^___^");
//			System.out.println("~~~ 확인용 로그인한 사용자명 : " + loginuser.getName());
			
			if(loginuser.getIdle() == 1) {
				// 마지막으로 로그인 한 것이 1년 이상 지난 경우 
	            
	            String message = "로그인을 한 지 1년이 지나 휴면상태로 되었습니다.\\n휴면을 풀어주는 페이지로 이동합니다!!";
	            String loc = request.getContextPath()+"/index.up";
	            // 원래는 위와 같이 index.up 이 아니라 휴면인 계정을 풀어주는 페이지로 URL을 잡아주어야 한다.!!
	            
	            request.setAttribute("message", message);
	            request.setAttribute("loc", loc);
	            
	            super.setRedirect(false); 
	            super.setViewPage("/WEB-INF/msg.jsp");
	            
	            return; // 메소드 종료
			}	// return 이 있으므로 else를 해줄 필요 없음.
			
			// !!!! session(세션) 이라는 저장소에 로그인 되어진 loginuser 을 저장시켜두어야 한다.!!!! //
			// session(세션) 이란 ? WAS 컴퓨터의 메모리(RAM)의 일부분을 사용하는 것으로 접속한 클라이언트 컴퓨터에서 보내온 정보를 저장하는 용도로 쓰인다.
			// 클라이언트 컴퓨터가 WAS 컴퓨터에 웹으로 접속을 하기만 하면 무조건 자동적으로 WAS 컴퓨터의 메모리(RAM)의 일부분에 session이 생성된다.
			// session 은 클라이언트 컴퓨터 웹브라우저당 1개씩 생성된다.
			// 예를 들면 클라이언트 컴퓨터가 크롬웹브라우저로 WAS 컴퓨터에 웹으로 연결하면 session이 하나 생성되고 ,
			// 또 이어서 동일한 클라이언트 컴퓨터가 엣지웹브라우저로 WAS 컴퓨터에 웹으로 연결하면 또 하나의 새로운 session이 생성된다.
	         /*
	               -------------
	               | 클라이언트   |             ---------------------
	               | A 웹브라우저 | ----------- |   WAS 서버         |
	               -------------             |                   |
	                                         |  RAM (A session)  |
	               -------------             |      (B session)  | 
	               | 클라이언트    |            |                   |
	               | B 웹브라우저  | ---------- |                   |
	               -------------             --------------------
	               
	           !!!! 세션(session)이라는 저장 영역에 loginuser 를 저장시켜두면
	                Command.properties 파일에 기술된 모든 클래스 및  모든 JSP 페이지(파일)에서 
	                세션(session)에 저장된 loginuser 정보를 사용할 수 있게 된다 !!! 
	                그러므로 어떤 정보를 여러 클래스 또는 여러 jsp 페이지에서 공통적으로 사용하고자 한다면
	                세션(session)에 저장해야 한다.!!!!
	          */
			
			HttpSession session = request.getSession();
			// WAS 메모리에 생성되어져 있는 session 을 불러오는 것이다.
			
			session.setAttribute("loginuser", loginuser);
			// session(세션)에 로그인 된 사용자 정보인 loginuser 를 키 이름을 "loginuser" 으로 하여 저장시켜두는 것이다.
			
			if(loginuser.isRequirePwdChange()) {
				
				// 비밀번호를 변경한 지 3개월 이상된 경우(true)
                
	            String message = "비밀번호를 변경하신지 3개월이 지났습니다.\\n암호를 변경하는 페이지로 이동합니다!!";
	            String loc = request.getContextPath() + "/index.up";
	            // 원래는 위와 같이 index.up 이 아니라 암호를 변경하는 페이지로 URL을 잡아주어야 한다.!!
	            
	            request.setAttribute("message", message);
	            request.setAttribute("loc", loc);
	            
	            super.setRedirect(false); 
	            super.setViewPage("/WEB-INF/msg.jsp");
	            
	            return; // 메소드 종료
			}
			
			super.setRedirect(true);
			super.setViewPage(request.getContextPath() + "/index.up");
			
		} else {
//			System.out.println("~~~ 확인용 로그인 실패 ㅠㅠ");
			
			String message = "로그인 실패";
			String loc = "javascript:history.back()";

			request.setAttribute("message", message);
			request.setAttribute("loc", loc);

			super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
		}
		
	}

}
