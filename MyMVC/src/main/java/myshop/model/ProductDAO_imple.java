package myshop.model;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import member.domain.MemberVO;
import myshop.domain.CartVO;
import myshop.domain.CategoryVO;
import myshop.domain.ImageVO;
import myshop.domain.ProductVO;
import myshop.domain.PurchaseReviewsVO;
import myshop.domain.SpecVO;
import util.security.AES256;
import util.security.SecretMyKey;

public class ProductDAO_imple implements ProductDAO {
	
	private DataSource ds;  // DataSource ds 는 아파치톰캣이 제공하는 DBCP(DB Connection Pool)이다.
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private AES256 aes;
	
	// 생성자
	public ProductDAO_imple() {
		
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			ds = (DataSource)envContext.lookup("jdbc/myoracle"); // DBCP
			// jdbc/myoracle => web.xml의 res-ref-name, C:\SW\apache-tomcat-10.1.19\conf\context.xml의 Resource name과 같음
			
			aes = new AES256(SecretMyKey.KEY);
			// SecretMyKey.KEY 는 우리가 만든 암호화/복호화 키이다.
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
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
		
		List<CategoryVO> categoryList  = new ArrayList<>(); 
	    
		try {
			conn = ds.getConnection();

			String sql = " select cnum, code, cname "
					   + " from tbl_category "
					   + " order by cnum asc ";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				CategoryVO cvo = new CategoryVO();
				cvo.setCnum(rs.getInt(1));
				cvo.setCode(rs.getString(2));
				cvo.setCname(rs.getString(3));

				categoryList.add(cvo);
				
			} // end of while(rs.next()) ----------------------------------

		} finally {
			close();
		}

		return categoryList;
	} // end of public List<CategoryVO> getCategoryList() throws SQLException ----------------



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
		
	} // end of public int totalPspecCount(String string) throws SQLException------


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
				
				pvo.setPcontent(rs.getString(11));	 // 제품설명 
				pvo.setPoint(rs.getInt(12));         // 포인트 점수  	   
				pvo.setPinputdate(rs.getString(13)); // 제품입고일자  
				
				productList.add(pvo);
				
			}// end of while(rs.next())-------------------------
			
			
		} finally {
			close();
		}
		
		return productList;		
		
	} // end of public List<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException-------

	
	
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
	         } // end of while -----------------
	         
	      } finally {
	         close();
	      }
	      
	      return categoryList;
	      
	} // end of public List<CategoryVO> selectCategoryList() throws SQLException -------------

	
	
	// 제품 스펙 목록을 조회해오기
	@Override
	public List<SpecVO> selectSpecList() throws SQLException {

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
	        	 svo.setSnum(rs.getInt("snum"));
	        	 svo.setSname(rs.getString("sname"));
	            
	        	 specList.add(svo);
	         } // end of while -----------------
	         
	      } finally {
	         close();
	      }
	      
	      return specList;
	      
	} // end of public List<SpecVO> selectSpecList() throws SQLException ---------------

	
	
	// 제품번호 채번 해오기
	@Override
	public int getPnumOfProduct() throws SQLException {
		
		int pnum = 0;

		try {
			conn = ds.getConnection();

			String sql = " select seq_tbl_product_pnum.nextval AS PNUM "
					   + " from dual ";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			rs.next();
			pnum = rs.getInt(1);

		} finally {
			close();
		}

		return pnum;
		
	} // end of public int getPnumOfProduct() throws SQLException --------------------

	
	
	// tbl_product 테이블에 제품정보 insert 하기
	@Override
	public int productInsert(ProductVO pvo) throws SQLException {

		int result = 0;

		try {
			conn = ds.getConnection();

			String sql = " insert into tbl_product(pnum, pname, fk_cnum, pcompany, pimage1, pimage2, prdmanual_systemFileName, prdmanual_orginFileName, pqty, price, saleprice, fk_snum, pcontent, point) "
					   + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, pvo.getPnum());
			pstmt.setString(2, pvo.getPname());
			pstmt.setInt(3, pvo.getFk_cnum());
			pstmt.setString(4, pvo.getPcompany());
			pstmt.setString(5, pvo.getPimage1());
			pstmt.setString(6, pvo.getPimage2());
			pstmt.setString(7, pvo.getPrdmanual_systemFileName());
			pstmt.setString(8, pvo.getPrdmanual_orginFileName());
			pstmt.setInt(9, pvo.getPqty());
			pstmt.setInt(10, pvo.getPrice());
			pstmt.setInt(11, pvo.getSaleprice());
			pstmt.setInt(12, pvo.getFk_snum());
			pstmt.setString(13, pvo.getPcontent());
			pstmt.setInt(14, pvo.getPoint());

			result = pstmt.executeUpdate();

		} finally {
			close();
		}

		return result;

	} // end of public int productInsert(ProductVO pvo) throws SQLException ----------------

	
	
	// >>> tbl_product_imagefile 테이블에 제품의 추가이미지 파일명 insert 하기 <<<
	@Override
	public int product_imagefile_insert(Map<String, String> paraMap) throws SQLException {

		int result = 0;

		try {
			conn = ds.getConnection();

			String sql = " insert into tbl_product_imagefile(imgfileno, fk_pnum, imgfilename) "
					   + " values(seqImgfileno.nextval, ?, ?) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, Integer.parseInt(paraMap.get("pnum")));
			pstmt.setString(2, paraMap.get("attachFileName"));

			result = pstmt.executeUpdate();

		} finally {
			close();
		}

		return result;

	} // end of public int product_imagefile_insert(Map<String, String> paraMap) throws SQLException -----------

	
	
	// 제품번호를 가지고 해당 제품의 정보를 조회해오기
	@Override
	public ProductVO selectOneProductByPnum(String pnum) throws SQLException {

		ProductVO pvo = null;

		try {
			conn = ds.getConnection();

			String sql = " SELECT sname, pnum, pname, pcompany, price, saleprice, point, pqty, pcontent, pimage1, pimage2,"
					   + "		  prdmanual_systemFileName, NVL(prdmanual_orginFileName, '없음') AS prdmanual_orginFileName "
					   + " FROM " 
					   + " ( "
					   + "      select fk_snum, pnum, pname, pcompany, price, saleprice, point, pqty, pcontent, pimage1, pimage2, prdmanual_systemFileName, prdmanual_orginFileName "
					   + "      from tbl_product "
					   + "      where pnum = ? "
					   + " ) P "
					   + " JOIN tbl_spec S "
					   + " ON P.fk_snum = S.snum ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				pvo = new ProductVO();

				pvo.setPnum(rs.getInt("PNUM")); // 제품번호
				pvo.setPname(rs.getString("PNAME")); // 제품명
				pvo.setPcompany(rs.getString("PCOMPANY")); // 제조회사명
				pvo.setPimage1(rs.getString("PIMAGE1")); // 제품이미지1 이미지파일명
				pvo.setPimage2(rs.getString("PIMAGE2")); // 제품이미지2 이미지파일명
				pvo.setPqty(rs.getInt("PQTY")); // 제품 재고량
				pvo.setPrice(rs.getInt("PRICE")); // 제품 정가
				pvo.setSaleprice(rs.getInt("SALEPRICE")); // 제품 판매가(할인해서 팔 것이므로)

				SpecVO spvo = new SpecVO();
				spvo.setSname(rs.getString("SNAME")); // 스펙명
				pvo.setSpvo(spvo);

				pvo.setPcontent(rs.getString("PCONTENT")); // 제품설명
				pvo.setPoint(rs.getInt("POINT")); // 포인트 점수

				pvo.setPrdmanual_systemFileName(rs.getString("PRDMANUAL_SYSTEMFILENAME")); // 파일서버에 업로드되는 실제 제품설명서 파일명
				pvo.setPrdmanual_orginFileName(rs.getString("PRDMANUAL_ORGINFILENAME")); // 웹클라이언트의 웹브라우저에서 파일을 업로드 할 때
				
			} // end of while(rs.next())-------------------------

		} finally {
			close();
		}

		return pvo;

	} // end of public ProductVO selectOneProductByPnum(String pnum) throws SQLException -------------------

	
	
	// 제품번호를 가지고 해당 제품의 추가된 이미지 정보를 조회해오기
	@Override
	public List<String> getImagesByPnum(String pnum) throws SQLException {

		List<String> imgList = new ArrayList<>();

		try {
			conn = ds.getConnection();

			String sql = " select imgfilename "
					   + " from tbl_product_imagefile "
					   + " where fk_pnum = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String imgfilename = rs.getString(1); // 이미지파일명
				imgList.add(imgfilename);
			}

		} finally {
			close();
		}

		return imgList;

	} // end of public List<String> getImagesByPnum(String pnum) throws SQLException --------------

	
	
	// 제품번호를 가지고서 해당 제품의 제품설명서 첨부파일의 서버에 업로드 된 파일명과 오리지널파일명 알아오기
	@Override
	public Map<String, String> getPrdmanualFileName(String pnum) throws SQLException {

		Map<String, String> map = new HashMap<>();

		try {
			conn = ds.getConnection();

			String sql = " select PRDMANUAL_SYSTEMFILENAME, PRDMANUAL_ORGINFILENAME "
					   + " from tbl_product "
					   + " where pnum = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				map.put("PRDMANUAL_SYSTEMFILENAME", rs.getString(1));
				// 파일서버에 업로드 된 실제 제품설명서 파일명

				map.put("PRDMANUAL_ORGINFILENAME", rs.getString(2));
				// 웹클라이언트의 웹브라우저에서 파일을 업로드 할 때 올리는 제품설명서 파일명
			}

		} finally {
			close();
		}

		return map;

	} // end of public Map<String, String> getPrdmanualFileName(String pnum) throws SQLException -------------

	
	
	// 장바구니 담기 
    // 장바구니 테이블(tbl_cart)에 해당 제품을 담아야 한다.
    // 장바구니 테이블에 해당 제품이 존재하지 않는 경우에는 tbl_cart 테이블에 insert 를 해야 하고, 
    // 장바구니 테이블에 해당 제품이 존재하는 경우에는 또 그 제품을 추가해서 장바구니 담기를 한다면 tbl_cart 테이블에 update 를 해야 한다
	@Override
	public int addCart(Map<String, String> paraMap) throws SQLException {
	
		int n = 0;
		
		try {
			conn = ds.getConnection();
			
			
			
			/*
	           먼저 장바구니 테이블(tbl_cart)에 어떤 회원이 새로운 제품을 넣는 것인지,
	           아니면 또 다시 제품을 추가로 더 구매하는 것인지를 알아야 한다.
	           이것을 알기 위해서 어떤 회원이 어떤 제품을 장바구니 테이블(tbl_cart) 넣을때
	           그 제품이 이미 존재하는지 select 를 통해서 알아와야 한다.
	           
				-------------------------------------------
				 cartno   fk_userid     fk_pnum   oqty  
				-------------------------------------------
				   1      kimdy          7        12     
				   2      kimdy          6         3     
				   3      leess          7         5     
	        */
						
			String sql = " select cartno "
					   + "from tbl_cart "
					   + "where fk_userid = ? and fk_pnum = ?  ";
			
			pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, paraMap.get("userid"));
	        pstmt.setString(2, paraMap.get("pnum"));
	        
	        rs = pstmt.executeQuery();
	        
	        if(rs.next()) {
	        	// 어떤 제품을 추가로 장바구니에 넣고자 하는 경우
	        	
	        	sql = "update tbl_cart set oqty = oqty + ? "
	        		+ " where cartno = ? ";
	        	
	        	pstmt = conn.prepareStatement(sql);
	        	pstmt.setInt(1, Integer.parseInt(paraMap.get("oqty")));
	        	pstmt.setInt(2, rs.getInt("cartno")); // 위에서 방금 읽어온 cartno 을 update문 조건의 cartno 에 넣어주기
	        	
	        	n = pstmt.executeUpdate();
	        	
	        } else {
	        	// 장바구니에 존재하지 않는 새로운 제품을 넣고자 하는 경우

				sql = " insert into tbl_cart(cartno, fk_userid, fk_pnum, oqty, registerday) "
					+ " values(seq_tbl_cart_cartno.nextval, ?, ?, ?, default) ";

				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("userid"));
				pstmt.setInt(2, Integer.parseInt(paraMap.get("pnum")));
				pstmt.setInt(3, Integer.parseInt(paraMap.get("oqty")));

				n = pstmt.executeUpdate();
	        }
			
		} finally {
			close();
		}
		
		return n;
		
	} // end of public int addCart(Map<String, String> paraMap) throws SQLException -----------------

	
	
	// 로그인한 사용자의 장바구니 목록을 조회하기
	@Override
	public List<CartVO> selectProductCart(String userid) throws SQLException {
		
		List<CartVO> cartList = null; // 물건을 장바구니에 아예 담지 않은 회원이 있을 수 있으므로 초기값 null로 설정
		
		try {
			
			conn = ds.getConnection();
			
			String sql = " SELECT C.cartno, C.fk_userid, C.fk_pnum, C.oqty, P.pname, P.pimage1, P.saleprice, P.point, P.pqty "
					   + " FROM "
					   + " (select cartno, fk_userid, fk_pnum, oqty, registerday "
					   + "  from tbl_cart "
					   + "  where fk_userid = ?"
					   + " ) C "
					   + " JOIN tbl_product P "
					   + " ON C.fk_pnum = P.pnum "
					   + " ORDER BY C.cartno DESC ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			 
			rs = pstmt.executeQuery();
			
			int cnt = 0;
			
			while(rs.next()) {
				
				cnt++;
				
				if(cnt == 1) {
					cartList = new ArrayList<>();
					
				}
				
				int cartno = rs.getInt("CARTNO");
	            String fk_userid = rs.getString("FK_USERID");
	            int fk_pnum = rs.getInt("FK_PNUM");
	            int oqty = rs.getInt("OQTY"); // 주문량
	            String pname = rs.getString("PNAME");
	            String pimage1 = rs.getString("PIMAGE1");
	            int saleprice = rs.getInt("SALEPRICE");
	            int point = rs.getInt("POINT"); 
	            int pqty = rs.getInt("PQTY"); // 잔고량

				ProductVO prodvo = new ProductVO();
				prodvo.setPnum(fk_pnum);
				prodvo.setPname(pname);
				prodvo.setPimage1(pimage1);
				prodvo.setSaleprice(saleprice);
				prodvo.setPoint(point);
				prodvo.setPqty(pqty);
				
				// ***** !!!! 중요함 !!!! ***** //
	            prodvo.setTotalPriceTotalPoint(oqty);
	            // ***** !!!! 중요함 !!!! ***** //
	            
	            CartVO cvo = new CartVO();
	            cvo.setCartno(cartno);
	            cvo.setFk_userid(fk_userid);
	            cvo.setFk_pnum(fk_pnum);
	            cvo.setOqty(oqty);
	            cvo.setProd(prodvo);
	            
	            cartList.add(cvo);

			} // end of while(rs.next()) -------------
			
		} finally {
			close();
		}
		
		return cartList;
		
	} // end of public List<CartVO> selectProductCart(String userid) throws SQLException ------------

	
	
	// 로그인한 사용자의 장바구니에 담긴 주문총액합계 및 총포인트합계 알아오기
	@Override
	public Map<String, String> selectCartSumPricePoint(String userid) throws SQLException {
		
		Map<String, String> sumMap = new HashMap<>();
		
		try {
			
			conn = ds.getConnection();
			
			String sql = " SELECT NVL(sum(C.oqty * P.saleprice),0) AS SUMTOTALPRICE, "
					   + "        NVL(sum(C.oqty * P.point),0) AS SUMTOTALPOINT "
					   + " FROM "
					   + " (select fk_pnum, oqty "
					   + "  from tbl_cart "
					   + "  where fk_userid = ?) C "
					   + " JOIN tbl_product P "
					   + " ON C.fk_pnum = P.pnum ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);

			rs = pstmt.executeQuery();
			rs.next();

			sumMap.put("SUMTOTALPRICE", rs.getString("SUMTOTALPRICE"));
			sumMap.put("SUMTOTALPOINT", rs.getString("SUMTOTALPOINT"));

		} finally {
			close();
		}
		
		return sumMap;
		
	} // end of public Map<String, String> selectCartSumPricePoint(String userid) throws SQLException ---------

	
	
	// 장바구니 테이블에서 특정제품의 주문량 변경시키기
	@Override
	public int updateCart(Map<String, String> paraMap) throws SQLException {
		
		int n = 0;
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " update tbl_cart set oqty = ? "
	                  	+ " where cartno = ? ";
	                  
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, paraMap.get("oqty"));
	         pstmt.setString(2, paraMap.get("cartno"));
	         
	         n = pstmt.executeUpdate();
	         
	      } finally {
	         close();
	      }
	      
	      return n;      
	} // end of public int updateCart(Map<String, String> paraMap) throws SQLException

	
	
	// 장바구니 테이블에서 특정제품을 장바구니에서 비우기
	@Override
	public int delCart(String cartno) throws SQLException {
		
		int n = 0;
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " delete from tbl_cart "
	                  	+ " where cartno = ? ";
	                  
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, cartno);
	         
	         n = pstmt.executeUpdate();
	         
	      } finally {
	         close();
	      }
	      
	      return n; 
	      
	} // end of public int delCart(String cartno) throws SQLException

	
	
	// 주문코드[명세서번호](시퀀스 seq_tbl_order 값)을 채번해오는 것.
	@Override
	public int get_seq_tbl_order() throws SQLException {
		
		int seq = 0;
        
	      try {
	          conn = ds.getConnection();
	             
	          String sql = " select seq_tbl_order.nextval AS seq "
	                     + " from dual";
	             
	          pstmt = conn.prepareStatement(sql);
	             
	          rs = pstmt.executeQuery();
	             
	          rs.next();
	             
	          seq = rs.getInt("seq");
	             
	         } finally {
	           close();
	      }
	         
	      return seq;
	      
	} // end of public int get_seq_tbl_order() throws SQLException

	
	
	// ===== Transaction 처리하기 ===== // 
    // 2. 주문 테이블에 채번해온 주문전표, 로그인한 사용자, 현재시각을 insert 하기(수동커밋처리)
    // 3. 주문상세 테이블에 채번해온 주문전표, 제품번호, 주문량, 주문금액을 insert 하기(수동커밋처리)
    // 4. 제품 테이블에서 제품번호에 해당하는 잔고량을 주문량 만큼 감하기(수동커밋처리)
	
    // 5. 장바구니 테이블에서 str_cartno_join 값에 해당하는 행들을 삭제(delete)하기(수동커밋처리)
    // >> 장바구니에서 주문을 한 것이 아니라 특정제품을 바로주문하기를 한 경우에는 장바구니 테이블에서 행들을 삭제할 작업은 없다. <<
	
    // 6. 회원 테이블에서 로그인한 사용자의 coin 액을 sum_totalPrice 만큼 감하고, point 를 sum_totalPoint 만큼 더하기(update)(수동커밋처리)
    // 7. **** 모든처리가 성공되었을시 commit 하기(commit) **** 
    // 8. **** SQL 장애 발생시 rollback 하기(rollback) ****
	@Override
	public int orderAdd(Map<String, Object> paraMap) throws SQLException {
		
		int isSuccess = 0;
		int n1 = 0, n2 = 0, n3 = 0, n4 = 0, n5 = 0;
		
		try {
			conn = ds.getConnection();
			
			conn.setAutoCommit(false);	// 수동 커밋으로 전환
			
			
			// 2. 주문 테이블에 채번해온 주문전표, 로그인한 사용자, 현재시각을 insert 하기(수동커밋처리)
			String sql = " insert into tbl_order(odrcode, fk_userid, odrtotalPrice, odrtotalPoint, odrdate) "
					   + " values(?, ?, ?, ?, default) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, (String)paraMap.get("odrcode"));
			pstmt.setString(2, (String)paraMap.get("userid"));
			pstmt.setInt(3, Integer.parseInt((String)paraMap.get("sum_totalPrice")));
			pstmt.setInt(4, Integer.parseInt((String)paraMap.get("sum_totalPoint")));
			
		    n1 = pstmt.executeUpdate();
		    // System.out.println("~~~~~ 확인용 n1 : " + n1);
		    // ~~~~~ 확인용 n1 : 1   
			
		    
		    // 3. 주문상세 테이블에 채번해온 주문전표, 제품번호, 주문량, 주문금액을 insert 하기(수동커밋처리)
		    if(n1 == 1) {
		    	// 주문코드(명세서번호) --> (String)paraMap.get("odrcode")
		    	String[] pnum_arr = (String[]) paraMap.get("pnum_arr"); // 제품번호
		    	String[] oqty_arr = (String[]) paraMap.get("oqty_arr");	// 주문량
		    	String[] totalPrice_arr = (String[]) paraMap.get("totalPrice_arr");	// 주문금액
		    	
		    	int cnt = 0;
		    	for(int i=0; i<pnum_arr.length; i++) {
		    		
		    		sql = " insert into tbl_orderdetail(odrseqnum, fk_odrcode, fk_pnum, oqty, odrprice, deliverStatus) "
		    			+ " values(seq_tbl_orderdetail.nextval, ?, to_number(?), to_number(?), to_number(?), default) ";
		    		
		    		pstmt = conn.prepareStatement(sql);
		    		pstmt.setString(1,(String)paraMap.get("odrcode") );
		    		pstmt.setString(2, pnum_arr[i]);
		    		pstmt.setString(3, oqty_arr[i]);
		    		pstmt.setString(4, totalPrice_arr[i]);
		    		
		    		pstmt.executeUpdate();
		    		
		    		cnt++;
		    		
		    	} // end of for(int i=0; i<pnum_arr.length; i++)
		    	
		    	if(cnt == pnum_arr.length) {
		    		n2 = 1;
		    	}
		    	// System.out.println("~~~~~ 확인용 n2 : " + n2);
	            //  ~~~~~ 확인용 n2 : 1
		    	
		    } // end of if(n1 == 1)
		    
		    
		    // 4. 제품 테이블에서 제품번호에 해당하는 잔고량을 주문량 만큼 감하기(수동커밋처리)
		    if(n2 == 1) {
		    	
		    	String[] pnum_arr = (String[]) paraMap.get("pnum_arr"); // 제품번호
		    	String[] oqty_arr = (String[]) paraMap.get("oqty_arr");	// 주문량
		    	
		    	int cnt = 0;
		    	
		    	for(int i=0; i<pnum_arr.length; i++) {
		    		
		    		sql = " update tbl_product set pqty = pqty - ?"
		    			+ " where pnum = ? ";
		    	
		    		pstmt = conn.prepareStatement(sql);
		    		pstmt.setInt(1, Integer.parseInt(oqty_arr[i]) );
		    		pstmt.setString(2, pnum_arr[i]);
		    		
		    		pstmt.executeUpdate();
		    		cnt++;
		    		
		    	} // end of for(int i=0; i<pnum_arr.length; i++)
		    	
		    	if(cnt == pnum_arr.length) {
		    		n3 = 1;
		    	}
		    	// System.out.println("~~~~~ 확인용 n3 : " + n3);
	            //  ~~~~~ 확인용 n3 : 1
		    	
		    	
		    } // end of if(n2 == 1)
		    

		    // 5. 장바구니 테이블에서 str_cartno_join 값에 해당하는 행들을 삭제(delete)하기(수동커밋처리)
		    // >> 장바구니에서 주문을 한 것이 아니라 특정제품을 바로주문하기를 한 경우에는 장바구니 테이블에서 행들을 삭제할 작업은 없다. <<
		    if(n3 == 1 && paraMap.get("cartno_arr") != null) {
		    	/*
	             	sql = " delete from tbl_cart "
	                	+ " where cartno in (?) ";
		    	*/   
	            // !!! 중요 in 절은 위와 같이 위치홀더 ? 를 사용하면 안됨. !!! //
		    	String[] cartno_arr = (String[]) paraMap.get("cartno_arr"); // 장바구니 번호
		    	// cartno_arr 은 ["9", "8"]
		    	
		    	String cartno_join = String.join("','", cartno_arr);	// "9','8"
		    	
		    	cartno_join = "'" + cartno_join + "'";	//	"'9','8'"
		    	// System.out.println("~~~~ 확인용 cartno_join => " + cartno_join);
	            // ~~~~ 확인용 cartno_join => '11','8','7'
		    	
		    	sql = " delete from tbl_cart "
	                + " where cartno in ("+cartno_join+") ";
		    	// !!! 중요 in 절은 위와 같이 직접 변수로 처리해야 함. !!!
	            // String.join(",", cartno_arr) 은 "11,8,7" 이러한 것이다.
	            // 조심할 것은 in 에 사용되어지는 cartno 컬럼의 타입이 number 타입이라면 괜찮은데
	            // 만약에 cartno 컬럼의 타입이 varchar2 타입이라면 "11,8,7" 와 같이 되어지면 오류가 발생한다. 
	            // 그래서 cartno 컬럼의 타입이 varchar2 타입이라면 "11,8,7" 을 "'11','8','7'" 와 같이 변경해주어야 한다. 
		    	
		    	pstmt = conn.prepareStatement(sql); 
	            n4 = pstmt.executeUpdate();
	          
	            // System.out.println("~~~~~ 확인용 n4 : " + n4);
	       
	             
	            if(n4 == cartno_arr.length) {
	               n4 = 1;
	               // System.out.println("~~~~~ 확인용 n4 : " + n4);
	            
	            }
	            
		    } // end of if(n3 == 1 && paraMap.get("cartno_arr") != null) 
		    
		    
		    if(n3==1 && paraMap.get("cartno_arr") == null) {
		    	// "제품 상세 정보" 페이지에서 "바로주문하기" 를 한 경우
	            // 장바구니 번호인 paraMap.get("cartno_arr") 이 없는 것이다. 
		    	
		    	n4 = 1;
		    	
		    } // end of if(n3==1 && paraMap.get("cartno_arr") == null)
		    

		    // 6. 회원 테이블에서 로그인한 사용자의 coin 액을 sum_totalPrice 만큼 감하고, point 를 sum_totalPoint 만큼 더하기(update)(수동커밋처리)
		    if(n4 == 1) {
		    	
		    	sql = " update tbl_member set coin = coin - ?"
		    		+ " , point = point + ?"
		    		+ " where userid = ? ";
		    	
		    	pstmt = conn.prepareStatement(sql);
		    	
		    	pstmt.setInt(1, Integer.parseInt((String)paraMap.get("sum_totalPoint")));
		    	pstmt.setInt(2, Integer.parseInt((String)paraMap.get("sum_totalPoint")));
		    	pstmt.setString(3, (String)paraMap.get("userid"));
		    	
		    	n5 = pstmt.executeUpdate();
		          
		        // System.out.println("~~~~~ 확인용 n5 : " + n5);
		        //  ~~~~~ 확인용 n5 : 1
		             
		    } // end of if(n4 == 1)
		    
		    
		    
		    // 7. **** 모든처리가 성공되었을시 commit 하기(commit) ****
		    if(n1*n2*n3*n4*n5 == 1) {
		    	
		    	conn.commit();
		    	
		    	conn.setAutoCommit(true);	// 자동 커밋으로 전환
		    	
		    	// System.out.println("~~~~~ 확인용 n1*n2*n3*n4*n5 : " + n1*n2*n3*n4*n5);
	            // ~~~~~ 확인용 n1*n2*n3*n4*n5 : 1
		    	
		    	isSuccess = 1;
		    	
		    }
		    
		} catch(SQLException e) {	
			// 8. **** SQL 장애 발생시 rollback 하기(rollback) ****
			
			conn.rollback();
	    	
	    	conn.setAutoCommit(true);	// 자동 커밋으로 전환
	    	
	    	isSuccess = 0;	// 디폴트가 0이므로 쓸 필요는 없음
			
		} finally {
			close();
		}
		
		return isSuccess;
		
	} // end of public int orderAdd(Map<String, Object> paraMap) throws SQLException

	
	

	// 주문한 제품에 대해 email 보내기시 email 내용에 넣을 주문한 제품번호들에 대한 제품정보를 얻어오는 것.
	@Override
	public List<ProductVO> getJumunProductList(String pnumes) throws SQLException {
		
		List<ProductVO> productList = new ArrayList<>();
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point "
						+ "    , to_char(pinputdate, 'yyyy-mm-dd') AS pinputdate "
						+ "    from tbl_product "
						+ "    where pnum in("+ pnumes +") ";
					
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				ProductVO pvo = new ProductVO();
				
				pvo.setPnum(rs.getInt("pnum"));     			// 제품번호
				pvo.setPname(rs.getString("pname")); 			// 제품명
				
				pvo.setPcompany(rs.getString("pcompany"));  	// 제조회사명
				pvo.setPimage1(rs.getString("pimage1"));   		// 제품이미지1   이미지파일명
				pvo.setPimage2(rs.getString("pimage2"));   		// 제품이미지2   이미지파일명
				pvo.setPqty(rs.getInt("pqty"));         		// 제품 재고량
				pvo.setPrice(rs.getInt("price"));        		// 제품 정가
				pvo.setSaleprice(rs.getInt("saleprice"));    	// 제품 판매가(할인해서 팔 것이므로)
				
				pvo.setPcontent(rs.getString("pcontent"));	 	// 제품설명 
				pvo.setPoint(rs.getInt("point"));         		// 포인트 점수  	   
				pvo.setPinputdate(rs.getString("pinputdate")); 	// 제품입고일자  
				
				productList.add(pvo);
				
			}// end of while(rs.next())-------------------------
			
			
		} finally {
			close();
		}
		
		return productList;		
		
		
	} // end of public List<ProductVO> getJumunProductList(String pnumes) throws SQLException

	
	
	// *** 관리자가 아닌 일반사용자로 로그인 했을 경우에는 자신이 주문한 내역만 페이징 처리하여 조회를 해오고, 
	//     관리자로 로그인을 했을 경우에는 모든 사용자들의 주문내역을 페이징 처리하여 조회해온다. 
	@Override
	public List<Map<String, String>> getOrderList(Map<String, String> paraMap) throws SQLException {
      
        List<Map<String, String>> order_map_List = new ArrayList<Map<String,String>>();
         
         try {
            
            conn = ds.getConnection();
            
            String sql  = " SELECT odrcode, fk_userid, odrdate, odrseqnum, fk_pnum, oqty, odrprice "
                        + "      , deliverstatus, pname, pimage1, price, saleprice, point "
                        + " FROM "
                        + " ( "
                        + " SELECT ROW_NUMBER() OVER(ORDER BY B.fk_odrcode desc, B.odrseqnum asc) AS RNO "
                        + "     , A.odrcode, A.fk_userid, to_char(A.odrdate, 'yyyy-mm-dd hh24:mi:ss') AS odrdate " 
                        + "     , B.odrseqnum, B.fk_pnum, B.oqty, B.odrprice "
                        + "     , CASE B.deliverstatus "
                        + "            WHEN 1 THEN '주문완료' "
                        + "            WHEN 2 THEN '배송중' "
                        + "            WHEN 3 THEN '배송완료' "
                        + "       END AS deliverstatus "
                        + "     , C.pname, C.pimage1, C.price, C.saleprice, C.point "
                        + " FROM tbl_order A JOIN tbl_orderdetail B "
                        + " ON A.odrcode = B.fk_odrcode "
                        + " JOIN tbl_product C "
                        + " ON B.fk_pnum = C.pnum ";
           
           if(!"admin".equals(paraMap.get("userid"))) {
              // 관리자가 아닌 일반사용자로 로그인 한 경우 
              sql += " WHERE A.fk_userid = ? ";
           }
           
           sql += " ) V "
                + " WHERE V.RNO BETWEEN ? AND ? ";
           
           pstmt = conn.prepareStatement(sql);
            
           
           /*
           		=== 페이징처리의 공식 ===
           		where RNO BETWEEN (조회하고자하는페이지번호 * 한페이지당보여줄행의개수) - (한페이지당보여줄행의개수 - 1) AND (조회하고자하는페이지번호 * 한페이지당보여줄행의개수); 
           */
           int currentShowPageNo = Integer.parseInt(paraMap.get("currentShowPageNo"));
           int sizePerPage = 10; // 한 페이지당 화면상에 보여줄 주문내역의 개수는 10 으로 한다.
           
           
           
           if(!"admin".equals(paraMap.get("userid"))) {
               // 관리자가 아닌 일반사용자로 로그인한 경우
               pstmt.setString(1, paraMap.get("userid"));
               pstmt.setInt(2, (currentShowPageNo * sizePerPage) - (sizePerPage - 1)); // 공식
               pstmt.setInt(3, (currentShowPageNo * sizePerPage)); // 공식
           }
           else {
               // 관리자로 로그인 한 경우 
               pstmt.setInt(1, (currentShowPageNo * sizePerPage) - (sizePerPage - 1)); // 공식
               pstmt.setInt(2, (currentShowPageNo * sizePerPage)); // 공식
           }
            
           rs = pstmt.executeQuery();
               
           while(rs.next()) {
              
              String odrcode = rs.getString("odrcode");
              String fk_userid = rs.getString("fk_userid");
              String odrdate = rs.getString("odrdate");
              String odrseqnum = rs.getString("odrseqnum");
              String fk_pnum = rs.getString("fk_pnum");
              String oqty = rs.getString("oqty");
              String odrprice = rs.getString("odrprice");
              String deliverstatus = rs.getString("deliverstatus");
              String pname = rs.getString("pname");
              String pimage1 = rs.getString("pimage1");
              String price = rs.getString("price");
              String saleprice = rs.getString("saleprice");
              String point = rs.getString("point");
              
              Map<String, String> odrmap = new HashMap<>();
              odrmap.put("ODRCODE", odrcode);
              odrmap.put("FK_USERID", fk_userid);
              odrmap.put("ODRDATE", odrdate);
              odrmap.put("ODRSEQNUM", odrseqnum);
              odrmap.put("FK_PNUM", fk_pnum);
              odrmap.put("OQTY", oqty);
              odrmap.put("ODRPRICE", odrprice);
              odrmap.put("DELIVERSTATUS", deliverstatus);
              odrmap.put("PNAME", pname);
              odrmap.put("PIMAGE1", pimage1);
              odrmap.put("PRICE", price);
              odrmap.put("SALEPRICE", saleprice);
              odrmap.put("POINT", point);
              
              order_map_List.add(odrmap);
              
           } // end of while()--------------------------------------   
        
         } finally {
            close();
         }
         
         return order_map_List;
         
      }// end of public List<Map<String, String>> getOrderList(String userid) throws SQLException--------------------

	
	
	
	
	
	
	
	
	
	
	
	
	// tbl_map(위,경도) 테이블에 있는 정보를 가져오기(select)
	@Override
	public List<Map<String, String>> selectStoreMap() throws SQLException {
		
		List<Map<String, String>> storeMapList = new ArrayList<>();
	      
	      	try {
	      		conn = ds.getConnection();
	         
		        String sql = " select storeID, storeName, storeUrl, storeImg, storeAddress, lat, lng, zindex " + 
		                    " from tbl_map " + 
		                    " order by zindex asc ";
		         
		        pstmt = conn.prepareStatement(sql);
		         
		        rs = pstmt.executeQuery();
		         
		        while(rs.next()) {
		            Map<String, String> map = new HashMap<>();
		            map.put("STOREID", rs.getString("STOREID"));
		            map.put("STORENAME", rs.getString("STORENAME"));
		            map.put("STOREURL", rs.getString("STOREURL"));
		            map.put("STOREIMG", rs.getString("STOREIMG"));
		            map.put("STOREADDRESS", rs.getString("STOREADDRESS"));
		            map.put("LAT", rs.getString("LAT"));
		            map.put("LNG", rs.getString("LNG"));
		            map.put("ZINDEX", rs.getString("ZINDEX"));
		                        
		            storeMapList.add(map); 
		        } // end of while(rs.next())
		         
	      } finally {
	         close();
	      }
	      
	      return storeMapList;   
	      
	} // end of public List<Map<String, String>> selectStoreMap() throws SQLException

	
	
	
	// 나의 카테고리별주문 통계정보 알아오기 
	@Override
	public List<Map<String, String>> myPurchase_byCategory(String userid) throws SQLException {
		
		List<Map<String, String>> myPurchase_map_List = new ArrayList<>();
		
		try {
			conn = ds.getConnection();
			
			String sql = " WITH "
					   + " O AS "
					   + " (SELECT odrcode "
					   + " FROM tbl_order "
					   + " WHERE fk_userid = ? "
					   + " ) "
					   + " , "
					   + " OD AS "
					   + " (SELECT fk_odrcode, fk_pnum, oqty, odrprice "
					   + " 	FROM tbl_orderdetail "
					   + " ) "
					   + " SELECT C.cname "
					   + "     , COUNT(C.cname) AS CNT "
					   + "     , SUM(OD.oqty * OD.odrprice) AS SUMPAY "
					   + "     , ROUND( SUM(OD.oqty * OD.odrprice) / ( SELECT SUM(OD.oqty * OD.odrprice) "
					   + "                                             FROM O JOIN OD "
					   + "                                             ON O.odrcode = OD.fk_odrcode ) * 100, 2 ) AS SUMPAY_PCT "
					   + " FROM O JOIN OD "
					   + " ON O.odrcode = OD.fk_odrcode "
					   + " JOIN tbl_product P "
					   + " ON OD.fk_pnum = P.pnum "
					   + " JOIN tbl_category C "
					   + " ON P.fk_cnum = C.cnum "
					   + " GROUP BY C.cname "
					   + " ORDER BY 3 DESC ";
			
			
			pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userid);
	         
	        rs = pstmt.executeQuery();
	                  
	        while(rs.next()) {
	            String cname = rs.getString("CNAME");
	            String cnt = rs.getString("CNT");
	            String sumpay = rs.getString("SUMPAY");
	            String sumpay_pct = rs.getString("SUMPAY_PCT");
	            
	            Map<String, String> map = new HashMap<>();
	            map.put("cname", cname);
	            map.put("cnt", cnt);
	            map.put("sumpay", sumpay);
	            map.put("sumpay_pct", sumpay_pct);
	            
	            myPurchase_map_List.add(map);
	        } // end of while----------------------------------
	        
	        
		} finally {
			close();
		} // end of try_finally
		
		return myPurchase_map_List;
		
	} // end of public List<Map<String, String>> myPurchase_byCategory(String userid) throws SQLException 

	
	
	
	// 나의 카테고리별 월별주문 통계정보 알아오기
	@Override
	public List<Map<String, String>> myPurchase_byMonth_byCategory(String userid) throws SQLException {
		
		List<Map<String, String>> myPurchase_map_List = new ArrayList<>();
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " WITH "
                        + " O AS "
                        + " (SELECT odrcode, odrdate "
                        + " FROM tbl_order "
                        +	" WHERE fk_userid = ? and to_char(odrdate, 'yyyy') = to_char(sysdate, 'yyyy') "
                        + " ) "
                        + " , "
                        + " OD AS "
                        + " (SELECT fk_odrcode, fk_pnum, oqty, odrprice "
                        + " FROM tbl_orderdetail "
                        + " ) "
                        + " SELECT C.cname "
                        + "     , COUNT(C.cname) AS CNT "
                        + "     , SUM(OD.oqty * OD.odrprice) AS SUMPAY "
                        + "     , round( SUM(OD.oqty * OD.odrprice)/( SELECT SUM(OD.oqty * OD.odrprice) "
                        + "                                           FROM O JOIN OD "
                        + "                                           ON O.odrcode = OD.fk_odrcode)*100, 2) AS SUMPAY_PCT "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '01', OD.oqty * OD.odrprice, 0) ) AS M_01 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '02', OD.oqty * OD.odrprice, 0) ) AS M_02 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '03', OD.oqty * OD.odrprice, 0) ) AS M_03 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '04', OD.oqty * OD.odrprice, 0) ) AS M_04 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '05', OD.oqty * OD.odrprice, 0) ) AS M_05 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '06', OD.oqty * OD.odrprice, 0) ) AS M_06 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '07', OD.oqty * OD.odrprice, 0) ) AS M_07 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '08', OD.oqty * OD.odrprice, 0) ) AS M_08 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '09', OD.oqty * OD.odrprice, 0) ) AS M_09 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '10', OD.oqty * OD.odrprice, 0) ) AS M_10 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '11', OD.oqty * OD.odrprice, 0) ) AS M_11 "
                        + "     , SUM( decode( to_char(O.odrdate,'mm'), '12', OD.oqty * OD.odrprice, 0) ) AS M_12 "
                        + " FROM O JOIN OD "
                        + " ON O.odrcode = OD.fk_odrcode "
	                  	+ " JOIN tbl_product P "
	                  	+ " ON OD.fk_pnum = P.pnum "
	                  	+ " JOIN tbl_category C "
	                  	+ " ON P.fk_cnum = C.cnum "
                  		+ " GROUP BY C.cname "
                  		+ " ORDER BY 3 desc ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, userid);
	         
	         rs = pstmt.executeQuery();
	                  
	         while(rs.next()) {
	            String cname = rs.getString("CNAME");
	            String cnt = rs.getString("CNT");
	            String sumpay = rs.getString("SUMPAY");
	            String sumpay_pct = rs.getString("SUMPAY_PCT");
	            String m_01 = rs.getString("M_01");
	            String m_02 = rs.getString("M_02");
	            String m_03 = rs.getString("M_03");
	            String m_04 = rs.getString("M_04");
	            String m_05 = rs.getString("M_05");
	            String m_06 = rs.getString("M_06");
	            String m_07 = rs.getString("M_07");
	            String m_08 = rs.getString("M_08");
	            String m_09 = rs.getString("M_09");
	            String m_10 = rs.getString("M_10");
	            String m_11 = rs.getString("M_11");
	            String m_12 = rs.getString("M_12");
	            
	            Map<String, String> map = new HashMap<>();
	            map.put("cname", cname);
	            map.put("cnt", cnt);
	            map.put("sumpay", sumpay);
	            map.put("sumpay_pct", sumpay_pct);
	            map.put("m_01", m_01);
	            map.put("m_02", m_02);
	            map.put("m_03", m_03);
	            map.put("m_04", m_04);
	            map.put("m_05", m_05);
	            map.put("m_06", m_06);
	            map.put("m_07", m_07);
	            map.put("m_08", m_08);
	            map.put("m_09", m_09);
	            map.put("m_10", m_10);
	            map.put("m_11", m_11);
	            map.put("m_12", m_12);
	            
	            myPurchase_map_List.add(map);
	         } // end of while----------------------------------
	                  
	      } finally {
	         close();
	      }
	      
	      return myPurchase_map_List;
		
		
	} // end of public List<Map<String, String>> myPurchase_byMonth_byCategory(String userid) throws SQLException

	

	// 로그인한 사용자가 특정 제품을 구매했는지 여부를 알아오는 것. 구매했다라면 true, 구매하지 않았다면 false 를 리턴함.
	@Override
	public boolean isOrder(Map<String, String> paraMap) throws SQLException {
		
		boolean bool = false;
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " select D.odrseqnum " + 
	                    " from tbl_orderdetail D JOIN tbl_order O " + 
	                    " on D.fk_odrcode = O.odrcode " + 
	                    " where D.fk_pnum = ? and O.fk_userid = ? ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, paraMap.get("fk_pnum"));
	         pstmt.setString(2, paraMap.get("fk_userid"));
	         
	         rs = pstmt.executeQuery();
	         
	         bool = rs.next();
	         
	      } finally {
	         close();
	      }
	      
	      return bool;
		
	} // end of public boolean isOrder(Map<String, String> paraMap) throws SQLException

	
	
	// 특정 회원이 특정 제품에 대해 좋아요에 투표하기(insert)
	@Override
	public int likeAdd(Map<String, String> paraMap) throws SQLException {
		
		int n = 0;
	      
	      try {
	         conn = ds.getConnection();
	         
	         conn.setAutoCommit(false); // 수동커밋으로 전환 
	         
	         String sql = " delete from tbl_product_dislike " + 
	                      " where fk_userid = ? and fk_pnum = ? ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, paraMap.get("userid") );
	         pstmt.setString(2, paraMap.get("pnum"));
	         
	         pstmt.executeUpdate();
	         
	         sql = " insert into tbl_product_like(fk_userid, fk_pnum) " + 
	                " values(?, ?) ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, paraMap.get("userid") );
	         pstmt.setString(2, paraMap.get("pnum"));
	         
	         n = pstmt.executeUpdate();
	                  
	         if(n == 1) {
	            conn.commit();
	         }
	         
	      } catch(SQLIntegrityConstraintViolationException e) {
	         conn.rollback();
	      } finally {
	         conn.setAutoCommit(true); // 자동커밋으로 전환 
	         close();
	      }
	      
	      return n;
	      
	} // end of public int likeAdd(Map<String, String> paraMap) throws SQLException


	
	

	// 특정 회원이 특정 제품에 대해 싫어요에 투표하기(insert)
	@Override
	public int dislikeAdd(Map<String, String> paraMap) throws SQLException {
		
		int n = 0;
	      
	    try {
	         conn = ds.getConnection();
	         
	         conn.setAutoCommit(false); // 수동커밋으로 전환 
	         
	         String sql = " delete from tbl_product_like " + 
	                      " where fk_userid = ? and fk_pnum = ? ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, paraMap.get("userid") );
	         pstmt.setString(2, paraMap.get("pnum"));
	         
	         pstmt.executeUpdate();
	         
	         sql = " insert into tbl_product_dislike(fk_userid, fk_pnum) " + 
	                " values(?, ?) ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, paraMap.get("userid") );
	         pstmt.setString(2, paraMap.get("pnum"));
	         
	         n = pstmt.executeUpdate();
	                  
	         if(n == 1) {
	            conn.commit();
	         }
	         
	    } catch(SQLIntegrityConstraintViolationException e) {
	         conn.rollback();
	    } finally {
	         conn.setAutoCommit(true); // 자동커밋으로 전환 
	         close();
	    }
	      
	      return n;
	      
	} // end of public int dislikeAdd(Map<String, String> paraMap) throws SQLException

	
	

	// 특정 제품에 대한 좋아요, 싫어요의 투표결과(select)
	@Override
	public Map<String, Integer> getLikeDislikeCnt(String pnum) throws SQLException {
		
		Map<String, Integer> map = new HashMap<>();
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " select ( select count(*) "+
	                    "          from tbl_product_like "+
	                    "          where fk_pnum = ? ) AS LIKECNT "+
	                    "      , ( select count(*) "+
	                    "          from tbl_product_dislike "+
	                    "          where fk_pnum = ? ) AS DISLIKECNT "+
	                    " from dual ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, pnum);
	         pstmt.setString(2, pnum);
	         
	         rs = pstmt.executeQuery();
	         
	         rs.next();
	         
	         map.put("likecnt", rs.getInt(1));
	         map.put("dislikecnt", rs.getInt(2));
	         
	      } finally {
	         close();
	      }
	      
	      return map;      
	      
	} // end of public Map<String, Integer> getLikeDislikeCnt(String pnum) throws SQLException 

	
	

	// 특정 사용자가 특정 제품에 대해 상품후기를 입력하기(insert)
	@Override
	public int addReview(PurchaseReviewsVO reviewsvo) throws SQLException {
		
		int n = 0;
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " insert into tbl_purchase_reviews(review_seq, fk_userid, fk_pnum, contents, writeDate) "
	                    + " values(seq_purchase_reviews.nextval, ?, ?, ?, default) ";
	                  
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, reviewsvo.getFk_userid());
	         pstmt.setInt(2, reviewsvo.getFk_pnum());
	         pstmt.setString(3, reviewsvo.getContents());
	         
	         n = pstmt.executeUpdate();
	         
	      } finally {
	         close();
	      }
	      
	      return n;
		
		
	} // end of public int addReview(PurchaseReviewsVO reviewsvo) throws SQLException

	
	
	
	// 특정 제품의 사용후기를 조회하기(select)
	@Override
	public List<PurchaseReviewsVO> reviewList(String fk_pnum) throws SQLException {
		
		List<PurchaseReviewsVO> reviewList = new ArrayList<>();
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " select review_seq, fk_userid, name, fk_pnum, contents, to_char(writeDate, 'yyyy-mm-dd hh24:mi:ss') AS writeDate "+
	                      " from tbl_purchase_reviews R join tbl_member M "+
	                      " on R.fk_userid = M.userid  "+
	                      " where R.fk_pnum = ? "+
	                      " order by review_seq desc ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, fk_pnum);
	         
	         rs = pstmt.executeQuery();
	         
	         while(rs.next()) {
	            String contents = rs.getString("contents");
	            String name = rs.getString("name");
	            String writeDate = rs.getString("writeDate");
	            String fk_userid = rs.getString("fk_userid");
	            int review_seq = rs.getInt("review_seq");
	                                    
	            PurchaseReviewsVO reviewvo = new PurchaseReviewsVO();
	            reviewvo.setContents(contents);
	            
	            MemberVO mvo = new MemberVO();
	            mvo.setName(name);
	            
	            reviewvo.setMvo(mvo);
	            reviewvo.setWriteDate(writeDate);
	            reviewvo.setFk_userid(fk_userid);
	            reviewvo.setReview_seq(review_seq);
	            
	            reviewList.add(reviewvo);
	         }         
	         
	      } finally {
	         close();
	      }
	      
	      return reviewList;
		
	} // end of public List<PurchaseReviewsVO> reviewList(String fk_pnum) throws SQLException

	
	
	// 특정 제품의 사용후기를 삭제하기(delete)
	@Override
	public int reviewDel(String review_seq) throws SQLException {
		
		int n = 0;
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " delete from tbl_purchase_reviews "
	                  + " where review_seq = ? ";
	                  
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, review_seq);
	         
	         n = pstmt.executeUpdate();
	         
	      } finally {
	         close();
	      }
	      
	      return n;
	      
	} // end of public int reviewDel(String review_seq) throws SQLException

	
	

   // 특정 제품의 사용후기를 수정하기(update)
   @Override
   public int reviewUpdate(Map<String, String> paraMap) throws SQLException {

      int n = 0;

      try {
         conn = ds.getConnection();

         String sql = " update tbl_purchase_reviews set contents = ?, writeDate = sysdate "
                  + " where review_seq = ? ";

         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, paraMap.get("contents"));
         pstmt.setString(2, paraMap.get("review_seq"));

         n = pstmt.executeUpdate();

      } finally {
         close();
      }

      return n;
       
   } // end of public int reviewUpdate(Map<String, String> paraMap) throws SQLException --------------

   
   

	// *** 페이징 처리를 위해서 먼저 주문개수를 알아오기 ***
	// 1. 일반 사용자로 로그인 한 경우는 자신이 주문한 개수만 알아오고,
	// 2. 관리자(admin)으로 로그인 한 경우 모든 사용자들의 주문한 개수를 알아온다. 
	@Override
	public int getTotalCountOrder(String userid) throws SQLException {
		
		int totalCountOrder = 0;
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " SELECT count(*) AS CNT "
	                    + " FROM tbl_order A JOIN tbl_orderdetail B "
	                    + " ON A.odrcode = B.fk_odrcode ";
	         
	         if("admin".equals(userid)) { // admin 으로 로그인한 경우 
	            pstmt = conn.prepareStatement(sql);
	         }
	         else { // admin 이 아닌 일반사용자로 로그인한 경우 
	            sql += " WHERE A.fk_userid = ? ";
	            
	            pstmt = conn.prepareStatement(sql);
	            pstmt.setString(1, userid);
	         }
	         
	         rs = pstmt.executeQuery();
	         rs.next();
	         
	         totalCountOrder = rs.getInt("CNT");
	         
	      } finally {
	         close();
	      }
	      
	      return totalCountOrder;
	      
	} // end of public int getTotalCountOrder(String userid) throws SQLException

	
	
	// 영수증전표(odrcode)소유주에 대한 사용자 정보를 조회해오는 것. 
	@Override
	public MemberVO odrcodeOwnerMemberInfo(String odrcode) throws SQLException {
		
		MemberVO mvo = null;
	      
	    try {
	    	conn = ds.getConnection();
	                  
	    	String sql = " select userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender " + 
	                     "      , birthday, coin, point, to_char(registerday, 'yyyy-mm-dd') AS registerday " +
	                     " from tbl_member " +
	                     " where userid = (select fk_userid " + 
	                     "                 from tbl_order " + 
	                     "                 where odrcode = ? ) ";
	         
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, odrcode);
	         
	        rs = pstmt.executeQuery();
	         
	        boolean isExists = rs.next();
	         
	        if(isExists) {
	        	mvo = new MemberVO();
	            
	            mvo.setUserid(rs.getString(1));
	            mvo.setName(rs.getString(2));
	            mvo.setEmail(aes.decrypt(rs.getString(3)));    // 복호화 
	            mvo.setMobile(aes.decrypt(rs.getString(4))); // 복호화 
	            mvo.setPostcode(rs.getString(5));
	            mvo.setAddress(rs.getString(6));
	            mvo.setDetailaddress(rs.getString(7));
	            mvo.setExtraaddress(rs.getString(8));
	            mvo.setGender(rs.getString(9));
	            mvo.setBirthday(rs.getString(10));
	            mvo.setCoin(rs.getInt(11));
	            mvo.setPoint(rs.getInt(12));
	            mvo.setRegisterday(rs.getString(13));
	         }
	         
	      } catch (GeneralSecurityException | UnsupportedEncodingException e) {
	         e.printStackTrace();
	      } finally {
	         close();
	      }
	      
	      return mvo;   
		
	} // end of public MemberVO odrcodeOwnerMemberInfo(String odrcode) throws SQLException

	
	

	// tbl_orderdetail 테이블의 deliverstatus(배송상태) 컬럼의 값을 2(배송시작)로 변경하기 
	@Override
	public int updateDeliverStart(String odrcodePnum) throws SQLException {
		
		int n = 0;
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " update tbl_orderdetail set deliverstatus = 2 "
	                    + " where fk_odrcode || '/' || fk_pnum in("+odrcodePnum+")"; 
	         
	         pstmt = conn.prepareStatement(sql); 
	         
	         n = pstmt.executeUpdate();
	         
	      } finally {
	         close();
	      }
	      
	      return n;   
	      
	} // end of public int updateDeliverStart(String odrcodePnum) throws SQLException

	

	// tbl_orderdetail 테이블의 deliverstatus(배송상태) 컬럼의 값을 3(배송완료)로 변경하기 
	@Override
	public int updateDeliverEnd(String odrcodePnum) throws SQLException {

		int n = 0;
  
		try {
			conn = ds.getConnection();
     
			String sql = " update tbl_orderdetail set deliverstatus = 3, deliverDate = sysdate "
					   + " where fk_odrcode || '/' || fk_pnum in("+odrcodePnum+")"; 
     
			pstmt = conn.prepareStatement(sql); 
     
			n = pstmt.executeUpdate();
     
		} finally {
			close();
		}
  
		return n;   
		      
	} // end of public int updateDeliverEnd(String odrcodePnum) throws SQLException

	

	// cnum(카테고리번호)이 tbl_category 테이블에 존재하는지 존재하지 않는지 알아오기 
	@Override
	public boolean isExist_cum(String cnum) throws SQLException {
		
		boolean isExist = false;
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " select * "  
	                    + " from tbl_category "
	                    + " where cnum = ? "; 
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         pstmt.setString(1, cnum);
	               
	         rs = pstmt.executeQuery();
	         
	         isExist = rs.next();
	         
	      } finally {
	         close();
	      }      
	      
	      return isExist;
	} // end of public boolean isExist_cum(String cnum) throws SQLException
	

	
	// 페이징 처리를 위한 특정 카테고리에 대한 총페이지수 알아오기
	@Override
	public int getTotalPage(String cnum) throws SQLException {
		
		int totalPage = 0;
	      
	    try {
	    	conn = ds.getConnection();
	         
	       	String sql = " select ceil(count(*)/10) " // 10 이 sizePerPage 이다.
	                   + " from tbl_product "
	                   + " where fk_cnum = ? ";
	         
	        pstmt = conn.prepareStatement(sql);
	         
	        pstmt.setString(1, cnum);
	         
	        rs = pstmt.executeQuery();
	        
	        rs.next();
	         
	        totalPage = rs.getInt(1);
	         
	    } finally {
	         close();
	    }
	      
	    return totalPage;
		
	} // end of public int getTotalPage(String cnum) throws SQLException

	
	
	// 특정한 카테고리에서 특정 페이지번호에 해당하는 제품들을 조회해오기
	@Override
	public List<ProductVO> selectProductByCategory(Map<String, String> paraMap) throws SQLException {
		
		List<ProductVO> productList = new ArrayList<>();
	      
	    try {
	        conn = ds.getConnection();
	         
	        String sql =  " SELECT cname, sname, pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point, pinputdate "
	                    + " FROM "
	                    + " ("
	                    + "     SELECT row_number() over(order by P.pnum desc) AS RNO "
	                    + "          , C.cname, S.sname, P.pnum, P.pname, P.pcompany, P.pimage1, P.pimage2, P.pqty, P.price, P.saleprice, P.pcontent, P.point, P.pinputdate "
	                    + "     FROM "
	                    + "     ( "
	                    + "      select pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point "
	                    + "           , to_char(pinputdate, 'yyyy-mm-dd') AS pinputdate, fk_cnum, fk_snum "
	                    + "      from tbl_product "
	                    + "      where fk_cnum = ? "
	                    + "     ) P "
	                    + "     JOIN tbl_category C "
	                    + "     ON P.fk_cnum = C.cnum "
	                    + "     JOIN tbl_spec S "
	                    + "     ON P.fk_snum = S.snum "
	                    + " ) V "
	                    + " WHERE V.RNO between ? and ? ";
	         
	        pstmt = conn.prepareStatement(sql);
	         
	      /*
	         === 페이징처리의 공식 ===
	         where RNO BETWEEN (조회하고자하는페이지번호 * 한페이지당보여줄행의개수) - (한페이지당보여줄행의개수 - 1) AND (조회하고자하는페이지번호 * 한페이지당보여줄행의개수); 
	      */
	        int currentShowPageNo = Integer.parseInt(paraMap.get("currentShowPageNo"));
	        int sizePerPage = 10; // 한 페이지당 화면상에 보여줄 제품의 개수는 10 으로 한다.
	         
	        pstmt.setString(1, paraMap.get("cnum"));
	        pstmt.setInt(2, (currentShowPageNo * sizePerPage) - (sizePerPage - 1)); // 공식
	        pstmt.setInt(3, (currentShowPageNo * sizePerPage)); // 공식 
	         
	        rs = pstmt.executeQuery();
	         
	        while(rs.next()) {
	            
	        	ProductVO pvo = new ProductVO();
	            
	        	pvo.setPnum(rs.getInt("pnum"));      // 제품번호
	        	pvo.setPname(rs.getString("pname")); // 제품명
	            
	            CategoryVO categvo = new CategoryVO();
	            categvo.setCname(rs.getString("cname"));  // 카테고리명
	            pvo.setCategvo(categvo);
	            
	            pvo.setPcompany(rs.getString("pcompany")); // 제조회사명
	            pvo.setPimage1(rs.getString("pimage1"));   // 제품이미지1   이미지파일명
	            pvo.setPimage2(rs.getString("pimage2"));   // 제품이미지2   이미지파일명
	            pvo.setPqty(rs.getInt("pqty"));            // 제품 재고량
	            pvo.setPrice(rs.getInt("price"));          // 제품 정가
	            pvo.setSaleprice(rs.getInt("saleprice"));  // 제품 판매가(할인해서 팔 것이므로)
	            
	            SpecVO spvo = new SpecVO();
	            spvo.setSname(rs.getString("sname")); // 스펙명
	            pvo.setSpvo(spvo);
	            
	            pvo.setPcontent(rs.getString("pcontent"));      // 제품설명 
	            pvo.setPoint(rs.getInt("point"));              // 포인트 점수        
	            pvo.setPinputdate(rs.getString("pinputdate")); // 제품입고일자  
	            
	            productList.add(pvo);
	            
	         }// end of while(rs.next())-------------------------
	         
	         
	      } finally {
	         close();
	      }
	      
	      return productList;      
		
	} // end of public List<ProductVO> selectProductByCategory(Map<String, String> paraMap) throws SQLException

	
	
	
}
