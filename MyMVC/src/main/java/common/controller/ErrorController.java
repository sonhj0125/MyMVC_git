package common.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ErrorController extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
//		super.setRedirect(false);
		super.setViewPage("/WEB-INF/error.jsp");
		
	}

}
