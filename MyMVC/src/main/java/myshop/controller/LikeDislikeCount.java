package myshop.controller;

import java.util.Map;

import org.json.JSONObject;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myshop.model.ProductDAO;
import myshop.model.ProductDAO_imple;

public class LikeDislikeCount extends AbstractController {

	private ProductDAO pdao = null;

	public LikeDislikeCount() {
		pdao = new ProductDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String pnum = request.getParameter("pnum");
		
		Map<String, Integer> map = pdao.getLikeDislikeCnt(pnum);
		
		JSONObject jsonObj = new JSONObject(); // {}
	      
		jsonObj.put("likecnt", map.get("likecnt"));       // {"likecnt":1}
		jsonObj.put("dislikecnt", map.get("dislikecnt")); // {"likecnt":1, "dislikecnt":0} 
      
		String json = jsonObj.toString(); // "{"likecnt":1, "dislikecnt":0}"
      
		request.setAttribute("json", json);
      
		super.setRedirect(false);
		super.setViewPage("/WEB-INF/jsonview.jsp");
		
	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception

}
