package myshop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import common.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myshop.domain.ProductVO;
import myshop.model.ProductDAO;
import myshop.model.ProductDAO_imple;

public class MallDisplayJSON extends AbstractController {

	
	private ProductDAO pdao = null;
	   
	public MallDisplayJSON() {
		pdao = new ProductDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String sname = request.getParameter("sname");		// "HIT"  "NEW"  "BEST"
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		/*
	       맨 처음에는 sname("HIT")상품을 start("1") 부터 len("8")개를 보여준다.
	       더보기 버튼을 클릭하면 sname("HIT")상품을 start("9") 부터 len("8")개를 보여준다.
	       또, 더보기 버튼을 클릭하면 sname("HIT")상품을 start("17") 부터 len("8")개를 보여준다.      
	    */   
		
		Map<String, String> paraMap = new HashMap<>();
		
		paraMap.put("sname", sname);	// "HIT"  "NEW"  "BEST"
		paraMap.put("start", start);	//  "1"  "9"  "17"  "25"  "33"
		
		String end = String.valueOf(Integer.parseInt(start) + Integer.parseInt(len) - 1);	// int 타입을 String 타입으로 변환
		paraMap.put("end", end);	// end => start + len - 1; 
        							// end    "8"   "16"  "24"   "32"   "40"
		
		List<ProductVO> productList = pdao.selectBySpecName(paraMap);
		
		JSONArray jsonArr = new JSONArray();	// []
		
		if(productList.size() > 0) {
			// DB에서 조회해온 결과물이 있을 경우
			
			for(ProductVO pvo : productList) {
				
				JSONObject jsonObj = new JSONObject();	// {}
				
				jsonObj.put("pnum", pvo.getPnum());		// {"pnum" : 36}
				jsonObj.put("pname", pvo.getPname());		// {"pnum" : 36, "pname" : "노트북30"}
				jsonObj.put("cname", pvo.getCategvo());		// {"pnum" : 36, "pname" : "노트북30", "cname" : "전자제품"}
				jsonObj.put("pcompany", pvo.getPcompany());
	            jsonObj.put("pimage1", pvo.getPimage1());
	            jsonObj.put("pimage2", pvo.getPimage2());
	            jsonObj.put("pqty", pvo.getPqty());
	            jsonObj.put("price", pvo.getPrice());
	            jsonObj.put("saleprice", pvo.getSaleprice());
	            jsonObj.put("sname", pvo.getSpvo().getSname());
	            jsonObj.put("pcontent", pvo.getPcontent());
	            jsonObj.put("point", pvo.getPoint());
	            jsonObj.put("pinputdate", pvo.getPinputdate());
	            
	            jsonObj.put("discountPercent", pvo.getDiscountPercent());
	            
	            // jsonObj ==> {"pnum" : 36, "pname" : "노트북30", "cname" : "전자제품", ....., "pinputdate" : "2024-05-14", "discountPercent" : 17}
	            
	            jsonArr.put(jsonObj);	// 배열[]속에 위의 jsonObj의 {}를 담는다.
	            
			} // end of for(ProductVO pvo : productList)
			
			String json = jsonArr.toString();	// 문자열로 변환
			
			// System.out.println("~~~ 확인용 => " + json);
			/*
				~~~ 확인용 => [{"pnum":36,"discountPercent":17,"pname":"노트북30","pcompany":"삼성전자","cname":"myshop.domain.CategoryVO@191b3209","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"59.jpg","pqty":100,"pimage2":"60.jpg","pcontent":"30번 노트북","price":1200000,"sname":"HIT"}
				 ,{"pnum":35,"discountPercent":17,"pname":"노트북29","pcompany":"레노버","cname":"myshop.domain.CategoryVO@3810ae34","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"57.jpg","pqty":100,"pimage2":"58.jpg","pcontent":"29번 노트북","price":1200000,"sname":"HIT"}
				 ,{"pnum":34,"discountPercent":17,"pname":"노트북28","pcompany":"아수스","cname":"myshop.domain.CategoryVO@4f5dec68","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"55.jpg","pqty":100,"pimage2":"56.jpg","pcontent":"28번 노트북","price":1200000,"sname":"HIT"}
				 ,{"pnum":33,"discountPercent":17,"pname":"노트북27","pcompany":"애플","cname":"myshop.domain.CategoryVO@5d9e3573","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"53.jpg","pqty":100,"pimage2":"54.jpg","pcontent":"27번 노트북","price":1200000,"sname":"HIT"}
				 ,{"pnum":32,"discountPercent":17,"pname":"노트북26","pcompany":"MSI","cname":"myshop.domain.CategoryVO@1d55cfb3","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"51.jpg","pqty":100,"pimage2":"52.jpg","pcontent":"26번 노트북","price":1200000,"sname":"HIT"}
				 ,{"pnum":31,"discountPercent":17,"pname":"노트북25","pcompany":"삼성전자","cname":"myshop.domain.CategoryVO@2089a323","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"49.jpg","pqty":100,"pimage2":"50.jpg","pcontent":"25번 노트북","price":1200000,"sname":"HIT"}
				 ,{"pnum":30,"discountPercent":17,"pname":"노트북24","pcompany":"한성컴퓨터","cname":"myshop.domain.CategoryVO@9725f6","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"47.jpg","pqty":100,"pimage2":"48.jpg","pcontent":"24번 노트북","price":1200000,"sname":"HIT"}
				 ,{"pnum":29,"discountPercent":17,"pname":"노트북23","pcompany":"DELL","cname":"myshop.domain.CategoryVO@2c0ead3a","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"45.jpg","pqty":100,"pimage2":"46.jpg","pcontent":"23번 노트북","price":1200000,"sname":"HIT"}]
			*/
			
			request.setAttribute("json", json);
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/jsonview.jsp");
			
			
		} // end of if(productList.size() > 0)
		
		
		

	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception

}
