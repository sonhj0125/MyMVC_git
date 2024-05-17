<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

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
			$("input:text[name='name']").val("${requestScope.name}");
			$("input:text[name='email']").val("${requestScope.email}");
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
		
	}); // end of $(document).ready(function() {}) ----------------
	

	
	// Function Declaration
	function goFind() {
		// --- 성명 ---
		const name = $("input:text[name='name']").val().trim();
		
		if(name == "") { // 이름을 작성하지 않았거나 공백인 경우
			alert("성명을 입력하세요!");
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
        const frm = document.idFindFrm;
        frm.action = "<%=ctxPath%>/login/idFind.up";
        frm.method = "post";
        frm.submit();
        
	} // end of function goFind() ------------------------
	
	
	
	// 아이디 찾기 모달창에 입력한 input 태그 value 값 초기화 시키기
	function func_form_reset_empty() {
		
		document.querySelector("form[name='idFindFrm']").reset();
		$("div#div_findResult").empty();
		
	} // end of function func_form_reset_empty() ----------
	
</script>



<form name="idFindFrm">

   <ul style="list-style-type: none;">
      <li style="margin: 25px 0">
          <label style="display: inline-block; width: 90px;">성명</label>
          <input type="text" name="name" size="25" autocomplete="off" /> 
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
   ID : <span style="color: red; font-size: 16pt; font-weight: bold;">${requestScope.userid}</span>
</div>



