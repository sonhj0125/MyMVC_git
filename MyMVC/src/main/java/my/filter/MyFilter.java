package my.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;

/*
	=== 필터란 ? ===
	필터란 Servlet 2.3 버전에 추가된 것으로,
	클라이언트의 요청을 서블릿이 받기 전에 가로채어 필터에 작성된 내용을 수행하는 것을 말한다.
	따라서 필터를 사용하면 클라이언트의 요청을 가로채서 서버 컴포넌트의 추가적인 다른 기능을 수행시킬 수 있다.
	
	<< 필터 적용 순서 >>
	1. Filter 인터페이스를 구현하는 자바 클래스를 생성.
	2. /WEB-INF/web.xml 에 filter 엘리먼트를 사용하여 필터 클래스를 등록한다.
*/

// web.xml에 있는 filter 엘리먼트 부분 주석 삭제해야 동작 가능 ★
public class MyFilter extends HttpFilter implements Filter {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpFilter#HttpFilter()
	 */
	public MyFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		// 필터 인스턴스를 종료시키기 전에 호출하는 메소드
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 필터의 로직을 작성하는 메소드
		// ==> doPost()에서 한글이 안 깨지려면
		// request.getParameter("name"); 을 하기 전에
		// request.setCharacterEncoding("UTF-8"); 을
		// 먼저 해 주어야 한다.
		request.setCharacterEncoding("UTF-8");

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// 서블릿 컨테이너가 필터 인스턴스를 초기화하기 위해서 호출하는 메소드
		// 여기는 기술할 필요가 없다.
	}

}
