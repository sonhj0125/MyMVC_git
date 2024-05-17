package myshop.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import myshop.domain.CategoryVO;
import myshop.domain.ImageVO;
import myshop.domain.ProductVO;
import myshop.domain.SpecVO;

public class ProductDAO_imple implements ProductDAO {
	
	private DataSource ds;  // DataSource ds 는 아파치톰캣이 제공하는 DBCP(DB Connection Pool)이다.
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	// 생성자
	public ProductDAO_imple() {
		
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			ds = (DataSource)envContext.lookup("jdbc/myoracle"); // DBCP
			// jdbc/myoracle => web.xml의 res-ref-name, C:\SW\apache-tomcat-10.1.19\conf\context.xml의 Resource name과 같음
			
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	// 사용한 자원을 반납하는 close() 메소드 생성하기 
	private void close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	// 메인페이지에 보여지는 상품 이미지 파일명을 모두 조회(select)하는 메소드
	@Override
	public List<ImageVO> imageSelectAll() throws SQLException {
		
		List<ImageVO> imgList = new ArrayList<>();
		
		try {
			
			conn = ds.getConnection();
			
			String sql = " select imgno, imgfilename "
					   + " from tbl_main_image "
					   + " order by imgno asc ";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				ImageVO imgvo = new ImageVO();
				
				imgvo.setImgno(rs.getInt("imgno"));
				imgvo.setImgfilename(rs.getString("imgfilename"));
				
				imgList.add(imgvo);
				
			} // end of while(rs.next()) -------------
			
		} finally {
			close();
		}
		
		return imgList;
	} // end of public List<ImageVO> imageSelectAll() throws SQLException ---------

	
	
	// tbl_category 테이블에서 카테고리 대분류 번호(cnum), 카테고리코드(code), 카테고리명(cname)을 조회해오기
	@Override
	public List<CategoryVO> getCategoryList() throws SQLException {
		
		List<CategoryVO> categoryList = new ArrayList<>(); 
	      
	      try {
	    	  conn = ds.getConnection();
	          
	          String sql = " select cnum, code, cname "  
	                     + " from tbl_category "
	                     + " order by cnum asc ";
	                    
	          pstmt = conn.prepareStatement(sql);
	               
	          rs = pstmt.executeQuery();
	          
	          while(rs.next()) {
	        	  CategoryVO cvo = new CategoryVO();
	        	  cvo.setCnum(rs.getInt(1));	// cnum
	        	  cvo.setCode(rs.getString(2)); // code
	        	  cvo.setCname(rs.getString(3)); // cname
	            
	        	  categoryList.add(cvo);
	          }// end of while(rs.next())----------------------------------
	         
	      } finally {
	         close();
	      }   
	      
	      return categoryList;	
		
	} // end of public List<CategoryVO> getCategoryList() throws SQLException

	
	
	// 제품의 스펙별(HIT, NEW, BEST) 상품의 전체개수를 알아오기
	@Override
	public int totalPspecCount(String fk_snum) throws SQLException {
		
		int totalCount = 0;
	      
	      	try {
	      		conn = ds.getConnection();
	         
	      		String sql = " select count(*) "
	      				   + " from tbl_product "
	      				   + " where fk_snum = ? ";
	         
	      		pstmt = conn.prepareStatement(sql);
	      		pstmt.setString(1, fk_snum);
	         
	      		rs = pstmt.executeQuery();
	         
	      		rs.next();
	         
	      		totalCount = rs.getInt(1);
	         
	      	} finally {
	      		close();
	      	}
	      
	      	return totalCount;
	} // end of public int totalPspecCount(String string) throws SQLException

	
	
	// 더보기 방식(페이징처리)으로 상품정보를 8개씩 잘라서(start ~ end) 조회해오기 
	@Override
	public List<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException {
		List<ProductVO> productList = new ArrayList<>();
	      
			try {
				conn = ds.getConnection();
	         
				String sql =  " SELECT pnum, pname, cname, pcompany, pimage1, pimage2, pqty, price, saleprice, sname, pcontent, point, pinputdate " 
		                    + " FROM "
		                    + " ( "
		                    + "    select row_number() over(order by pnum desc) AS RNO "
		                    + "         , pnum, pname, C.cname, pcompany, pimage1, pimage2, pqty, price, saleprice, S.sname, pcontent, point "
		                    + "         , to_char(pinputdate, 'yyyy-mm-dd') AS pinputdate "
		                    + "    from tbl_product P "
		                    + "    JOIN tbl_category C "
		                    + "    ON P.fk_cnum = C.cnum "
		                    + "    JOIN tbl_spec S "
		                    + "    ON P.fk_snum = S.snum "
		                    + "    where S.sname = ? "
		                    + " ) V "
		                    + " WHERE RNO between ? and ? ";
		         
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("sname"));
				pstmt.setString(2, paraMap.get("start"));
				pstmt.setString(3, paraMap.get("end"));
	         
				rs = pstmt.executeQuery();
	         
				while(rs.next()) {
	            
		            ProductVO pvo = new ProductVO();
		            
		            pvo.setPnum(rs.getInt(1));     // 제품번호
		            pvo.setPname(rs.getString(2)); // 제품명
		            
		            CategoryVO categvo = new CategoryVO();
		            categvo.setCname(rs.getString(3));  // 카테고리명
		            pvo.setCategvo(categvo);
		            
		            pvo.setPcompany(rs.getString(4));  // 제조회사명
		            pvo.setPimage1(rs.getString(5));   // 제품이미지1   이미지파일명
		            pvo.setPimage2(rs.getString(6));   // 제품이미지2   이미지파일명
		            pvo.setPqty(rs.getInt(7));         // 제품 재고량
		            pvo.setPrice(rs.getInt(8));        // 제품 정가
		            pvo.setSaleprice(rs.getInt(9));    // 제품 판매가(할인해서 팔 것이므로)
		            
		            SpecVO spvo = new SpecVO();
		            spvo.setSname(rs.getString(10)); // 스펙명
		            pvo.setSpvo(spvo);
		            
		            pvo.setPcontent(rs.getString(11));    // 제품설명 
		            pvo.setPoint(rs.getInt(12));         // 포인트 점수        
		            pvo.setPinputdate(rs.getString(13)); // 제품입고일자  
		            
		            productList.add(pvo);
	            
				}// end of while(rs.next())-------------------------
	         
	         
			} finally {
				close();
			}
	      
			return productList;
	
	} // end of public List<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException

	
	
	// 카테고리 목록을 조회해오기
	@Override
	public List<CategoryVO> selectCategoryList() throws SQLException {
		
		List<CategoryVO> categoryList = new ArrayList<>();
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " select cnum, code, cname "
	                  + " from tbl_category "
	                  + " order by cnum asc ";
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         rs = pstmt.executeQuery();
	         
	         while(rs.next()) {
	            CategoryVO cvo = new CategoryVO();
	            cvo.setCnum(rs.getInt(1));
	            cvo.setCode(rs.getString(2));
	            cvo.setCname(rs.getString(3));
	            
	            categoryList.add(cvo);
	         }// end of while-----------------
	         
	      } finally {
	         close();
	      }
	      
	      return categoryList;
	
	} // end of public List<CategoryVO> selectCategoryList()

	
	
	// 스펙 목록을 조회해오기
	@Override
	public List<SpecVO> selectSpectList() throws SQLException {
		
		List<SpecVO> specList = new ArrayList<>();
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " select snum, sname "
	         			+ " from tbl_spec "
	         			+ " order by snum asc ";
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         rs = pstmt.executeQuery();
	         
	         while(rs.next()) {
	        	 SpecVO svo = new SpecVO();
	        	 svo.setSnum(rs.getInt(1));
	        	 svo.setSname(rs.getString(2));
	            
	        	 specList.add(svo);
	         }// end of while-----------------
	         
	      } finally {
	         close();
	      }
	      
	      return specList;
		
	} // end of public List<SpecVO> selectSpectList() throws SQLException

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
