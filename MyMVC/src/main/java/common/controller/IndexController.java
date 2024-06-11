package common.controller;

import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myshop.domain.ImageVO;
import myshop.model.*;

public class IndexController extends AbstractController {

	private ProductDAO pdao = null;
	
	public IndexController() {
		pdao = new ProductDAO_imple();
//		System.out.println("~~~~ 확인용 IndexController 생성자 호출");
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
//		ProductDAO pdao = new ProductDAO_imple();
		// 함수 호출 시마다 매번 new 로 객체를 생성하게 되므로 위와 같이 지역변수로 사용하지 말 것!
		
		try {
			List<ImageVO> imgList = pdao.imageSelectAll();
			request.setAttribute("imgList", imgList);
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/index.jsp");
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			super.setRedirect(true);
			super.setViewPage(request.getContextPath() + "/error.up");
//			super.setViewPage("/WEB-INF/error.jsp");
			// 위와 같이 작성하면 에러 발생 시 url이 그대로 index.up이 된다. (에러 페이지인지 구분되지 않음)
		}
		
	}

}
