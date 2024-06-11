package member.controller;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import member.domain.MemberVO;
import member.model.MemberDAO;
import member.model.MemberDAO_imple;

public class CoinUpdateLoginUser extends AbstractController {

	private MemberDAO mdao = null;
	
	public CoinUpdateLoginUser() {
		mdao = new MemberDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String method = request.getMethod(); // "GET" 또는 "POST"
		
		int n = 0; // SQL 결과 확인용
		
		String message = "";
		String loc = "";
		
		if("POST".equalsIgnoreCase(method)) {
			// POST 방식이라면
			
			String userid = request.getParameter("userid");
			String coinmoney = request.getParameter("coinmoney");
			
			Map<String, String> paraMap = new HashMap<>(); // 보낼 데이터가 2개 이상일 경우 Map 사용!!!
			paraMap.put("userid", userid);
			paraMap.put("coinmoney", coinmoney);
			
			
			try {
				n = mdao.coinUpdateLoginUser(paraMap); // DB에서 회원의 코인 및 포인트 증가시키기
				
				if(n == 1) {
					HttpSession session = request.getSession();
					MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
					
					// !!!!! 세션값을 변경하기 !!!!! //
					loginuser.setCoin(loginuser.getCoin() + Integer.parseInt(coinmoney));
					loginuser.setPoint(loginuser.getPoint() + (int)(Integer.parseInt(coinmoney) * 0.01));
					
					DecimalFormat df = new DecimalFormat("#,###");
		            //  예를 들면
		            //  System.out.println(df.format(2005100));
	                //  "2,005,100"
					
					message = loginuser.getName() + "님의 " + df.format(Long.parseLong(coinmoney)) + "원 결제가 완료되었습니다.";
					loc = request.getContextPath() + "/index.up";
					
				}
				
			} catch (SQLException e) {
//				e.printStackTrace();
				
				message = "코인액 결제가 DB오류로 인해 실패되었습니다.";
	            loc = "javascript:history.back()";
			}
			
		} else {
			// POST 방식이 아니라면
			 
			message = "비정상적인 경로로 들어왔습니다.";
			loc = "javascript:history.back()";
			
		}
		
		JSONObject jsonObj = new JSONObject(); // {}
		
		jsonObj.put("n", n); 			 // {"n":1} 또는 {"n":0} 
		jsonObj.put("message", message); // {"n":1, "message":"김다영님의 300,000원 결제가 완료되었습니다."}
		jsonObj.put("loc", loc);		 // {"n":1, "message":"김다영님의 300,000원 결제가 완료되었습니다.", "loc":"/MyMVC/index.up"}
		
		String json = jsonObj.toString(); // "{"n":1, "message":"김다영님의 300,000원 결제가 완료되었습니다.", "loc":"/MyMVC/index.up"}"
		request.setAttribute("json", json);
		
//		super.setRedirect(false);
		super.setViewPage("/WEB-INF/jsonview.jsp");
	}

}
