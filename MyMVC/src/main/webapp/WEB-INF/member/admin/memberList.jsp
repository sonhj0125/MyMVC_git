<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../header2.jsp" />

<style type="text/css">
   
   table#memberTbl {
      width: 80%;
      margin: 0 auto;
   }
   
   table#memberTbl th {
      text-align: center;
      font-size: 14pt;
   }
   
   table#memberTbl tr.memberInfo:hover {
      background-color: #e6ffe6;
      cursor: pointer;
   }
   
   form[name="member_search_frm"] {
      border: solid 0px red;
      width: 80%;
      margin: 0 auto 3% auto;
   }
   
   form[name="member_search_frm"] button.btn-secondary {
      margin-left: 2%;
      margin-right: 32%;
   }
   
   div#pageBar {
      
      border: solid 0px red;
      width: 80%;
      margin: 3% auto 0 auto;
      display: flex;
   }
   
   div#pageBar > nav {
      margin: auto;
   }
   
</style>

<script type="text/javascript">
	$(document).ready(function() {
		
		// 검색 시 검색타입, 검색 단어 그대로 유지하도록 하기
		if("${requestScope.searchType}" != "" &&
		   "${requestScope.searchWord}" != "") { <%-- ${} 바깥에 따옴표 필수!! --%>
			
			$("select[name='searchType']").val("${requestScope.searchType}");
			$("input:text[name='searchWord']").val("${requestScope.searchWord}");
			
		}
		
		// 검색 시 페이지 당 회원수 선택한 것 유지하기
		if("${requestScope.sizePerPage}" != "") { <%-- ${} 바깥에 따옴표 필수!! --%>
			$("select[name='sizePerPage']").val("${requestScope.sizePerPage}");
			
		}
		
		// searchWord 엔터 시 검색 실행
		$("input:text[name='searchWord']").bind("keydown", function(e) {
			
			if(e.keyCode == 13) {
				goSearch();
			}
			
		}); // end of $("input:text[name='searchWord']").bind("keydown", function(e) {}) ---------
		
		// **** select 태그에 대한 이벤트는 click 이 아니라 change 이다. **** //
		$("select[name='sizePerPage']").bind("change", function() {
			
			const frm = document.member_search_frm;
//			frm.action = "memberList.up";  // form 태그에 action 이 명기되지 않았으면 현재보이는 URL 경로로 submit 되어진다.
//			frm.method = "get";  // form 태그에 method 를 명기하지 않으면 "get" 방식이다.
			frm.submit();
			
		}); // end of $("select[name='sizePerPage']").bind("change", function() {}) ------------------
		
		
		// **** 특정 회원을 클릭하면 그 회원의 상세정보를 보여주도록 한다. **** //
		$("table#memberTbl tr.memberInfo").click( e => {
			// alert($(e.target).parent().html());
			// const userid = $(e.target).parent().find(".userid").text();
			// 또는
			const userid = $(e.target).parent().children(".userid").text();
			// alert(userid);
			
			const frm = document.memberOneDetail_frm;
			frm.userid.value = userid;
			
			<%-- frm.action = "<%= ctxPath%>/member/memberOneDetail.up"; --%>
			// 위와 아래는 동일
			frm.action = "${pageContext.request.contextPath}/member/memberOneDetail.up";
			frm.method = "post";
			frm.submit();
			
		});
		
		<%--  .jsp 파일에서 사용되어지는 것들 
			 console.log('${pageContext.request.contextPath}');  // 컨텍스트패스   /MyMVC
		     console.log('${pageContext.request.requestURL}');   // 전체 URL     http://localhost:9090/MyMVC/WEB-INF/member/admin/memberList.jsp
		     console.log('${pageContext.request.scheme}');       // http        http
		     console.log('${pageContext.request.serverName}');   // localhost   localhost
		     console.log('${pageContext.request.serverPort}');   // 포트번호      9090
		     console.log('${pageContext.request.requestURI}');   // 요청 URI     /MyMVC/WEB-INF/member/admin/memberList.jsp 
		     console.log('${pageContext.request.servletPath}');  // 파일명       /WEB-INF/member/admin/memberList.jsp 
	    --%>
		
		
		
	}); // end of $(document).ready(function() {}) --------------------------
	
	function goSearch() {
		
		const searchType = $("select[name='searchType']").val();
		
		if(searchType == "") {
			alert("검색대상을 선택하세요!");
			return; // goSearch() 함수 종료
			
		}
		
		const frm = document.member_search_frm;
//		frm.action = "memberList.up";  // form 태그에 action 이 명기되지 않았으면 현재보이는 URL 경로로 submit 되어진다.
//		frm.method = "get";  // form 태그에 method 를 명기하지 않으면 "get" 방식이다.
		frm.submit();
		// action이나 method를 기재하지 않으면 현재 URL에 get 방식으로 전송한다.
		
	} // end of function goSearch() ---------------------------
</script>

<div class="container" style="padding: 3% 0;">
	<h2 class="text-center mb-5">::: 회원전체 목록 :::</h2>

	<form name="member_search_frm">
		<select name="searchType">
			<option value="">검색대상</option>
			<option value="name">회원명</option>
			<option value="userid">아이디</option>
			<option value="email">이메일</option>
		</select>
		&nbsp;
		<input type="text" name="searchWord" />
		<%--
             form 태그 내에서 데이터를 전송해야 할 input 태그가 만약에 1개 밖에 없을 경우에는
             input 태그 내에 값을 넣고 나서 그냥 엔터를 해 버리면 submit 되어버린다.
             그래서 유효성 검사를 거친 후 submit 을 하고 싶어도 input 태그가 만약에 1개 밖에 없을 경우라면 
             유효성검사가 있더라도 유효성검사를 거치지 않고 바로 submit 된다. 
             이것을 막으려면 input 태그가 2개 이상 존재하면 된다.  
             그런데 실제 화면에 보여질 input 태그는 1개 이어야 한다.
             이럴 경우 아래와 같이 해주면 된다. 
             또한 form 태그에 action 이 명기되지 않았으면 현재 보이는 URL 경로로 submit 된다.   
        --%>
		<input type="text" style="display: none;" /> <%-- 조심할 것은 type="hidden" 이 아니다. ★ --%> 

		<button type="button" class="btn btn-secondary" onclick="goSearch()">검색</button>

		<span style="font-size: 12pt; font-weight: bold;">페이지당 회원명수&nbsp;-&nbsp;</span>
		<select name="sizePerPage">
			<option value="10">10명</option>
			<option value="5">5명</option>
			<option value="3">3명</option>
		</select>
	</form>

	<table class="table table-bordered" id="memberTbl">
		<thead>
			<tr>
				<th>번호</th>
				<th>아이디</th>
				<th>회원명</th>
				<th>이메일</th>
				<th>성별</th>
			</tr>
		</thead>

		<tbody>
			<c:if test="${not empty requestScope.memberList}">
				<c:forEach var="membervo" items="${requestScope.memberList}" varStatus="status">
					<tr class="memberInfo">
					
						<fmt:parseNumber var="currentShowPageNo" value="${requestScope.currentShowPageNo}" />
						<fmt:parseNumber var="sizePerPage" value="${requestScope.sizePerPage}" />
						<%-- fmt:parseNumber 은 문자열을 숫자형식으로 형변환 시키는 것이다. --%>
						
						<td>${requestScope.totalMemberCount - (currentShowPageNo - 1) * sizePerPage - (status.index)}</td>
						<%-- >>> 페이징 처리 시 보여주는 순번 공식 <<<
		                     데이터개수 - (페이지번호 - 1) * 1페이지당보여줄개수 - 인덱스번호 => 순번 
		                  
		                     <예제>
		                     데이터개수 : 12
		                     1페이지당보여줄개수 : 5
		                  
		                     ==> 1 페이지       
		                     12 - (1-1) * 5 - 0  => 12
		                     12 - (1-1) * 5 - 1  => 11
		                     12 - (1-1) * 5 - 2  => 10
		                     12 - (1-1) * 5 - 3  =>  9
		                     12 - (1-1) * 5 - 4  =>  8
		                  
		                     ==> 2 페이지
		                     12 - (2-1) * 5 - 0  =>  7
		                     12 - (2-1) * 5 - 1  =>  6
		                     12 - (2-1) * 5 - 2  =>  5
		                     12 - (2-1) * 5 - 3  =>  4
		                     12 - (2-1) * 5 - 4  =>  3
		                  
		                     ==> 3 페이지
		                     12 - (3-1) * 5 - 0  =>  2
		                     12 - (3-1) * 5 - 1  =>  1 
		                 --%>
						<td class="userid">${membervo.userid}</td>
						<td>${membervo.name}</td>
						<td>${membervo.email}</td>
						<td>
							<c:choose>
								<c:when test="${membervo.gender == '1'}">남</c:when>
								<c:otherwise>여</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</c:if>
			
			<c:if test="${empty requestScope.memberList}">
				<tr>
					<td colspan="5" style="text-align: center;">데이터가 존재하지 않습니다.</td>
				</tr>
			</c:if>
		</tbody>
	</table>

	<div id="pageBar">
		<nav>
			<ul class = "pagination">${requestScope.pageBar}</ul>
		</nav>
	</div>
</div>

<form name="memberOneDetail_frm">
	<input type="hidden" name="userid" />
	<input type="hidden" name="goBackURL" value="${requestScope.currentURL}" />
</form>


<jsp:include page="../../footer2.jsp" />