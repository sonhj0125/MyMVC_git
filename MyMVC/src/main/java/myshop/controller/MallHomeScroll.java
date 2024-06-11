package myshop.controller;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myshop.model.ProductDAO;
import myshop.model.ProductDAO_imple;

public class MallHomeScroll extends AbstractController {

	private ProductDAO pdao = null;
	
	public MallHomeScroll() {
		pdao = new ProductDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// 로그인 또는 로그아웃을 하면 시작페이지로 가는 것이 아니라 방금 보았던 그 페이지로 그대로 가기 위한 것임.(공통적이므로 부모클래스에서 해줌)
		super.goBackURL(request);
		
		
		// === Ajax(JSON)를 사용하여 HIT 상품목록 "스크롤" 방식으로 페이징 처리해서 보여주겠다. === // 
		int totalHITCount = pdao.totalPspecCount("1"); // HIT 상품의 전체개수를 알아온다.
		
	//	System.out.println("~~~~ 확인용 totalHITCount : " + totalHITCount);
		// ~~~~ 확인용 totalHITCount : 36
		
		request.setAttribute("totalHITCount", totalHITCount);
		
		super.setRedirect(false);
		super.setViewPage("/WEB-INF/myshop/mallHomeScroll.jsp");
		
	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception

}
