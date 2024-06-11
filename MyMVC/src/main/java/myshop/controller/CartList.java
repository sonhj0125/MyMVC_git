package myshop.controller;

import java.util.List;
import java.util.Map;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import member.domain.MemberVO;
import myshop.domain.CartVO;
import myshop.model.ProductDAO;
import myshop.model.ProductDAO_imple;

public class CartList extends AbstractController {

	private ProductDAO pdao = null;

	public CartList() {
		pdao = new ProductDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		////////////////////////////////////////////////////////////
		// **** GET 방식을 막는 또 다른 방법 ==> 웹브라우저 주소창에서 직접 입력하지 못하게 막아버리면 된다. **** //
	    // 이것의 단점은 웹브라우저에서 북마크(즐겨찾기)를 했을 경우 접속이 안 된다는 것이다.
	    // 왜냐하면 이전 페이지가 없이 웹브라우저 주소창에서 직접 입력한 것과 동일하기 때문이다.
		
		String referer = request.getHeader("referer"); 
		// request.getHeader("referer"); 은 이전 페이지의 URL을 가져오는 것이다.
		
		if(referer == null) {
			// referer == null 은 웹브라우저 주소창에 URL 을 직접 입력하고 들어온 경우이다.
			super.setRedirect(true);
			super.setViewPage(request.getContextPath() + "/index.up");
			return;
		}
		
		// 장바구니 보기는 반드시 해당사용자가 로그인을 해야만 볼 수 있다.
		if (!super.checkLogin(request)) {
			// 장바구니 메뉴바는 무조건 로그인을 해야만 보여지기 때문에 if문을 쓰지 않아도 됨
			
			request.setAttribute("message", "장바구니를 보려면 먼저 로그인부터 하세요!!");
			request.setAttribute("loc", "javascript:history.back()");

			super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
			return;
		}
		
		else {
			// 로그인 했을 경우
			
			HttpSession session = request.getSession();
			MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
			
			// 로그인한 사용자의 장바구니 목록을 조회하기
			List<CartVO> cartList = pdao.selectProductCart(loginuser.getUserid());
			
			// 로그인한 사용자의 장바구니에 담긴 주문총액합계 및 총포인트합계 알아오기
			Map<String, String> sumMap = pdao.selectCartSumPricePoint(loginuser.getUserid());
			
			request.setAttribute("cartList", cartList);
			request.setAttribute("sumMap", sumMap);
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/myshop/cartList.jsp");
			
		}

		super.setRedirect(false);
		super.setViewPage("/WEB-INF/myshop/cartList.jsp");
		
	}

}
