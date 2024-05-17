package myshop.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import myshop.domain.CategoryVO;
import myshop.domain.ImageVO;
import myshop.domain.ProductVO;
import myshop.domain.SpecVO;

public interface ProductDAO {
	
	// 메인페이지에 보여지는 상품 이미지 파일명을 모두 조회(select)하는 메소드
	List<ImageVO> imageSelectAll() throws SQLException;

	// tbl_category 테이블에서 카테고리 대분류 번호(cnum), 카테고리코드(code), 카테고리명(cname)을 조회해오기
	List<CategoryVO> getCategoryList() throws SQLException;

	// 제품의 스펙별(HIT, NEW, BEST) 상품의 전체개수를 알아오기
	int totalPspecCount(String fk_snum) throws SQLException;

	// 더보기 방식(페이징처리)으로 상품정보를 8개씩 잘라서(start ~ end) 조회해오기 
	List<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException;

	// 카테고리 목록을 조회해오기
	List<CategoryVO> selectCategoryList() throws SQLException;

	// 스펙 목록을 조회해오기
	List<SpecVO> selectSpectList() throws SQLException;
	
}
