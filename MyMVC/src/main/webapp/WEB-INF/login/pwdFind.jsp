<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath = request.getContextPath();
    //    /MyMVC
%>

<%-- Required meta tags --%>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

<%-- Bootstrap CSS --%>
<link rel="stylesheet" type="text/css" href="<%= ctxPath%>/bootstrap-4.6.2-dist/css/bootstrap.min.css" > 

<%-- Font Awesome 6 Icons --%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

<%-- Optional JavaScript --%>
<script type="text/javascript" src="<%= ctxPath%>/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript" src="<%= ctxPath%>/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js" ></script>

<script type="text/javascript">
	$(document).ready(function() {
		
		const method = "${requestScope.method}";
		
//		console.log("~~~ 확인용 method : " + method);
		/*
			~~~ 확인용 method : GET
			~~~ 확인용 method : POST
		*/
		
		if(method == "GET") {
			$("div#div_findResult").hide();
		}
		
		if(method == "POST") {
 			$("input:text[name='userid']").val("${requestScope.userid}");
			$("input:text[name='email']").val("${requestScope.email}");
			
			if(${requestScope.isUserExist == true && requestScope.sendMailSuccess == true}) {
				$("button.btn-success").hide();
			}
		}
		
		
		// '찾기' 버튼을 클릭하였을 경우
		$("button.btn-success").click(function() {
			goFind();
		}); // end of $("button.btn-success").click(function() {}) -------------
		
		
		// 이메일 입력 후 엔터를 눌렀을 경우
		$("input:text[name='email']").bind("keyup", function(e) { // keyup 또는 keydown도 가능
			if(e.keyCode == 13) { // 엔터를 클릭할 경우
				goFind();
			}
		}); // end of $("input:text[name='email']").bind("keyup", function(e) {}) ------------
		
		
		
		// === 인증하기 버튼 클릭 시 이벤트 처리해주기 시작 ===
		$("button.btn-info").click(function() {
			const input_confirmCode = $("input:text[name='input_confirmCode']").val().trim();
			
			if(input_confirmCode == "") {
				alert("인증코드를 입력하세요!");
				return; // 함수 종료
			}
			
			const frm = document.verifyCertificationFrm;
			frm.userCertificationCode.value = input_confirmCode; // 입력받은 인증코드 넣기
			frm.userid.value = $("input:text[name='userid']").val();
			
			frm.action = "<%=ctxPath%>/login/verifyCertification.up";
			frm.method = "post";
			frm.submit();
			
			
			
			
		}); // $("button.btn-info").click(function() {}) ------------------
		// === 인증하기 버튼 클릭 시 이벤트 처리해주기 끝 ===
		
		
	}); // end of $(document).ready(function() {}) ----------------
	

	
	// Function Declaration
	function goFind() {
		// --- 아이디 ---
		const userid = $("input:text[name='userid']").val().trim();
		
		if(userid == "") { // 아이디를 작성하지 않았거나 공백인 경우
			alert("아이디를 입력하세요!");
			return; // 함수 종료
		}
		
		// --- 이메일 ---
		const email = $("input:text[name='email']").val();
		
		// const regExp_email = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;  
        // 또는
        const regExp_email = new RegExp(/^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i);
        // 이메일 정규표현식 객체 생성 

        const bool = regExp_email.test(email);

        if (!bool) {
            // 이메일이 정규표현식에 위배된 경우
            alert("이메일을 올바르게 입력하세요!");
            return; // 함수 종료
        }
        
        // --- return이 되지 않은 경우(올바르게 입력한 경우) ---
        const frm = document.pwdFindFrm;
        frm.action = "<%=ctxPath%>/login/pwdFind.up";
        frm.method = "post";
        frm.submit();
        
	} // end of function goFind() ------------------------
	
</script>



<form name="pwdFindFrm">
	<ul style="list-style-type: none;">
		<li style="margin: 25px 0">
			<label style="display: inline-block; width: 90px;">아이디</label>
			<input type="text" name="userid" size="25" autocomplete="off" />
		</li>
		<li style="margin: 25px 0">
			<label style="display: inline-block; width: 90px;">이메일</label>
			<input type="text" name="email" size="25" autocomplete="off" />
		</li>
	</ul>

	<div class="my-3 text-center">
		<button type="button" class="btn btn-success">찾기</button>
	</div>
</form>


<div class="my-3 text-center" id="div_findResult">

	<c:if test="${requestScope.isUserExist == false}">
		<span style="color: red;">사용자 정보가 없습니다.</span>
	</c:if>
	
	
	<c:if test="${requestScope.isUserExist == true && requestScope.sendMailSuccess == true}">
		<span style="font-size: 10pt;">
			인증코드가 ${requestScope.email}로 발송되었습니다.<br>
			인증코드를 입력해주세요.
		</span>
		<br>
		<input type="text" name="input_confirmCode" />
		<br><br>
		<button type="button" class="btn btn-info">인증하기</button>
	</c:if>
	
	
	<c:if test="${requestScope.isUserExist == true && requestScope.sendMailSuccess == false}">
		<span style="color: red;">메일 발송에 실패했습니다.</span>
	</c:if>
	
</div>

<%-- 인증하기 form --%>
<form name="verifyCertificationFrm">
	<input type="hidden" name="userCertificationCode" />
	<input type="hidden" name="userid" />
</form>



