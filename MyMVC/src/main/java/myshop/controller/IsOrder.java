package myshop.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myshop.model.ProductDAO;
import myshop.model.ProductDAO_imple;

public class IsOrder extends AbstractController {

	private ProductDAO pdao = null;

	public IsOrder() {
		pdao = new ProductDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String fk_pnum = request.getParameter("fk_pnum");
	    String fk_userid = request.getParameter("fk_userid");
	    
	    Map<String, String> paraMap = new HashMap<>();
	      
	    paraMap.put("fk_pnum", fk_pnum);
	    paraMap.put("fk_userid", fk_userid);
		
	    boolean bool = pdao.isOrder(paraMap);
	    
	    JSONObject jsonobj = new JSONObject();
	    jsonobj.put("isOrder", bool); 
	    
	    String json = jsonobj.toString();
	    request.setAttribute("json", json);
		
	    super.setRedirect(false);
	    super.setViewPage("/WEB-INF/jsonview.jsp");
	      
	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception

}
