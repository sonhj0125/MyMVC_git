<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	String ctxPath = request.getContextPath();
	//	   /MyMVC
%>

<jsp:include page="header1.jsp" />

<div class="container"> <%-- BootStrap을 추가할 필요 없다. (이미 header1.jsp 파일에 설정해두었기 때문) --%>
	<p class="h2 text-danger mt-4">장애발생</p>
    <img src="<%= ctxPath%>/images/error.gif" class="img-fluid" /> <%-- 반응형 이미지 --%>
    <p class="h4 text-primary mt-3">빠른 복구를 위해 최선을 다하겠습니다</p>
</div>

<jsp:include page="footer1.jsp" />