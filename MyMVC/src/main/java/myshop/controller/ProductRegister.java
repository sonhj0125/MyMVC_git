package myshop.controller;

import java.util.List;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import member.domain.MemberVO;
import myshop.domain.CategoryVO;
import myshop.domain.ProductVO;
import myshop.domain.SpecVO;
import myshop.model.ProductDAO;
import myshop.model.ProductDAO_imple;

public class ProductRegister extends AbstractController {

	private ProductDAO pdao = null;
	   
	public ProductRegister() {
		pdao = new ProductDAO_imple();
	}	
	
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// == 관리자(admin)로 로그인 했을 때만 제품등록이 가능하도록 한다. == //
		HttpSession session = request.getSession();
		
		MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
		
		if( loginuser != null && "admin".equals(loginuser.getUserid())) {
			// 관리자(admin)로 로그인 했을 경우
			
			String method = request.getMethod();
			
			if(!"POST".equalsIgnoreCase(method)) {	// "GET" 이라면
				
				// 카테고리 목록 조회해오기
				List<CategoryVO> categoryList = pdao.selectCategoryList();
				request.setAttribute("categoryList", categoryList);
				
				// 스펙 목록 조회해오기
				List<SpecVO> specList = pdao.selectSpectList();
				request.setAttribute("specList", specList);
				
				super.setRedirect(false);
	            super.setViewPage("/WEB-INF/myshop/admin/productRegister.jsp");
	            
			} else {	// "POST" 이라면
				
				
			}	
			
		} else {
			
	        // 로그인을 안한 경우 또는 일반사용자로 로그인 한 경우 
	        String message = "관리자만 접근이 가능합니다.";
	        String loc = "javascript:history.back()";
	         
	        request.setAttribute("message", message);
	        request.setAttribute("loc", loc);
	         
	     // super.setRedirect(false);
	        super.setViewPage("/WEB-INF/msg.jsp");
	        
	    } // end of if_else( loginuser != null && "admin".equals(loginuser.getUserid()))
			
		
	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception

}
