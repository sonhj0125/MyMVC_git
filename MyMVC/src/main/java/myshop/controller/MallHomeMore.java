package myshop.controller;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myshop.model.ProductDAO;
import myshop.model.ProductDAO_imple;

public class MallHomeMore extends AbstractController {

	private ProductDAO pdao = null;
	   
	public MallHomeMore() {
		pdao = new ProductDAO_imple();
	}
	
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// === Ajax(JSON)를 사용하여 HIT 상품목록 "더보기" 방식으로 페이징 처리해서 보여주겠다. === // 
		
		// HIT 상품의 전체개수를 알아온다. --> DAO 필요
		int totalHITCount = pdao.totalPspecCount("1");
		
		// System.out.println("~~~~ 확인용 totalHITCount : " + totalHITCount);
		// ~~~~ 확인용 totalHITCount : 36
		
		request.setAttribute("totalHITCount", totalHITCount);
		
		
		
		
		super.setRedirect(false);
		super.setViewPage("/WEB-INF/myshop/mallHomeMore.jsp");
		
	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception

}
