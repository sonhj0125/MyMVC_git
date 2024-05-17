<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../../header2.jsp" />    

<style type="text/css">
   	table#tblProdInput {border: solid gray 1px; 
                       border-collapse: collapse; }
                       
    table#tblProdInput td {border: solid gray 1px; 
                          padding-left: 10px;
                          height: 50px; }
                          
    .prodInputName {background-color: #e6fff2; 
                    font-weight: bold; }                                                 
   
   	.error {color: red; font-weight: bold; font-size: 9pt;}
   
   	div.fileDrop{ display: inline-block; 
                  width: 100%; 
                  height: 100px;
                  overflow: auto;
                  background-color: #fff;
                  padding-left: 10px;}
                 
    div.fileDrop > div.fileList > span.delete{display:inline-block; width: 20px; border: solid 1px gray; text-align: center;} 
    div.fileDrop > div.fileList > span.delete:hover{background-color: #000; color: #fff; cursor: pointer;}
    div.fileDrop > div.fileList > span.fileName{padding-left: 10px;}
    div.fileDrop > div.fileList > span.fileSize{padding-right: 20px; float:right;} 
    span.clear{clear: both;} 
   
</style>

<script type="text/javascript">

	$(document).ready(function(){
		
		$("span.error").hide();
		
		
		// "제품수량" 에 스피너 달아주기
		$("input#spinnerPqty").spinner({
			
		   spin:function(event,ui){
			   
		      if(ui.value > 100) {	// 100보다 작은 값이어야 함
		         $(this).spinner("value", 100);
		         return false;
		      }
		      else if(ui.value < 1) {
		         $(this).spinner("value", 1);
		         return false;
		      }
		   } // end of spin:function(event,ui) 
		
		});// end of $("input#spinnerPqty").spinner()-------
		
		
		
		
		// ==>> 제품이미지 파일선택을 선택하면 화면에 이미지를 미리 보여주기 시작 <<== //
		$(document).on("change", "input.img_file", function(e){
			
			const input_file = $(e.target).get(0);
			// jQuery선택자.get(0) 은 jQuery 선택자인 jQuery Object 를 DOM(Document Object Model) element 로 바꿔주는 것이다. 
	        // DOM element 로 바꿔주어야 순수한 javascript 문법과 명령어를 사용할 수 있게 된다.
			// input_file -> 선택자이다.
			
			// console.log(input_file);
			// <input type="file" name="pimage1" class="infoData img_file" accept="image/*">
			
			
			// console.log(input_file.files);
			/*
				FileList{0: File, length: 1}
				0: File {name: 'berkelekle심플라운드01.jpg', lastModified: 1605506138000, lastModifiedDate: Mon Nov 16 2020 14:55:38 GMT+0900 (GMT+09:00), webkitRelativePath: '', size: 71317, …}
				length: 1
				[[Prototype]]: FileList
			*/
			
			// console.log(input_file.files[0]);	// [0]은 배열이 아니고, 대괄호표기법(객체) 0은 KEY 값이다.
			// File {name: 'berkelekle심플라운드01.jpg', lastModified: 1605506138000, lastModifiedDate: Mon Nov 16 2020 14:55:38 GMT+0900 (GMT+09:00), webkitRelativePath: '', size: 71317, …}
			/*
				>>설명<<
	            name : 단순 파일의 이름 string타입으로 반환 (경로는 포함하지 않는다.)
	            lastModified : 마지막 수정 날짜 number타입으로 반환 (없을 경우, 현재 시간)
	            lastModifiedDate: 마지막 수정 날짜 Date객체타입으로 반환
	            size : 64비트 정수의 바이트 단위 파일의 크기 number타입으로 반환
	            type : 문자열인 파일의 MIME 타입 string타입으로 반환 
	                   MIME 타입의 형태는 type/subtype 의 구조를 가지며, 다음과 같은 형태로 쓰인다. 
	                  text/plain
	                  text/html
	                  image/jpeg
	                  image/png
	                  audio/mpeg
	                  video/mp4
	                  ...
	        */
			// console.log(input_file.files[0].name);
			// berkelekle심플라운드01.jpg
			
			
			// 자바스크립트에서 file 객체의 실제 데이터(내용물)에 접근하기 위해 FileReader 객체를 생성하여 사용한다.
	        const fileReader = new FileReader();
			
	        fileReader.readAsDataURL(input_file.files[0]); 
	        // FileReader.readAsDataURL() --> 파일을 읽고, result 속성에 파일을 나타내는 URL을 저장 시켜준다.
			
	        fileReader.onload = function(){	// FileReader.onload --> 파일 읽기 완료 성공시에만 작동하도록 하는 것임.
	        	
	        	// console.log(fileReader.result);
	        	/*
	              data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAeAB4AAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAg 
	              이러한 형태로 출력되며, img.src 의 값으로 넣어서 사용한다.
	            */
	            
	            document.getElementById("previewImg").src = fileReader.result;	// 이미지 미리보기
	        	
	        }; // end of fileReader.onload = function()
			
	        
		}); // end of $(document).on("change", "input.img_file", function(e)
		// ==>> 제품이미지 파일선택을 선택하면 화면에 이미지를 미리 보여주기 끝 <<== //
		
		
		
		
		
		
		<%-- === jQuery 를 사용하여 드래그앤드롭(DragAndDrop)을 통한 파일 업로드 시작 === --%>
		
		let file_arr = [];	// 첨부되어진 파일 정보를 담아둘 배열
		
		// == 파일 Drag & Drop 만들기 == //
		$("div#fileDrop").on("dragenter", function(){	/* "dragenter" 이벤트는 드롭대상인 박스 안에 Drag 한 파일이 최초로 들어왔을 때 */
			
			e.preventDefault();
           <%-- 
                     브라우저에 어떤 파일을 drop 하면 브라우저 기본 동작이 실행된다. 
                     이미지를 drop 하면 바로 이미지가 보여지게되고, 만약에 pdf 파일을 drop 하게될 경우도 각 브라우저의 pdf viewer 로 브라우저 내에서 pdf 문서를 열어 보여준다. 
                     이것을 방지하기 위해 preventDefault() 를 호출한다. 
                     즉, e.preventDefault(); 는 해당 이벤트 이외에 별도로 브라우저에서 발생하는 행동을 막기 위해 사용하는 것이다.
           --%>
           
           e.stopPropagation();
           <%--
               propagation 의 사전적의미는 전파, 확산이다.
               stopPropagation 은 부모태그로의 이벤트 전파를 stop 중지하라는 의미이다.
               즉, 이벤트 버블링을 막기위해서 사용하는 것이다. 
               사용예제 사이트 https://devjhs.tistory.com/142 을 보면 이해가 될 것이다. 
           --%>
		}).on("dragover", function(e){ 
			/* "dragover" 이벤트는 드롭대상인 박스 안에 Drag 한 파일이 머물러 있는 중일 때. 필수이벤트이다. dragover 이벤트를 적용하지 않으면 drop 이벤트가 작동하지 않음 */ 
        	
			e.preventDefault();
	        e.stopPropagation();
	        
	        $(this).css("background-color", "#ffd8d8");	// this 는 $("div#fileDrop")을 말함
	        
	    }).on("dragleave", function(e){ /* "dragleave" 이벤트는 Drag 한 파일이 드롭대상인 박스 밖으로 벗어났을 때  */
	        
	    	e.preventDefault();
	        e.stopPropagation();
	        
	        $(this).css("background-color", "#fff");
	        
		}).on("drop", function(e){   	/* "drop" 이벤트는 드롭대상인 박스 안에서 Drag 한것을 Drop(Drag 한 파일(객체)을 놓는것) 했을 때. 필수이벤트이다. */
	        
	    	e.preventDefault();
		
			// alert("헤헤헤");
	    
	    });
		
		
		
		
		
		<%-- === jQuery 를 사용하여 드래그앤드롭(DragAndDrop)을 통한 파일 업로드 끝 === --%>
		
		
		
		
	}); // end of $(document).ready(function(){})

</script>




	<div align="center" style="margin-bottom: 20px;">

   	<div style="border: solid green 2px; width: 250px; margin-top: 20px; padding-top: 10px; padding-bottom: 10px; border-left: hidden; border-right: hidden;">       
      	<span style="font-size: 15pt; font-weight: bold;">제품등록&nbsp;[관리자전용]</span>   
   	</div>
   	
   	<br/>
   
   	<%-- !!!!! ==== 중요 ==== !!!!! --%>
   	<%-- 폼에서 파일을 업로드 하려면 반드시 method 는 POST 이어야 하고 
        enctype="multipart/form-data" 으로 지정해주어야 한다.!! --%>
   	<form name="prodInputFrm" enctype="multipart/form-data"> 
         
      	<table id="tblProdInput" style="width: 80%;">
      	<tbody>
         	<tr>
            	<td width="25%" class="prodInputName" style="padding-top: 10px;">카테고리</td>
            	<td width="75%" align="left" style="padding-top: 10px;" >
               	<select name="fk_cnum" class="infoData">
                  	<option value="">:::선택하세요:::</option>
                  	<%-- 
                  		비추방법(카테고리가 신규로 생성될 수 있으므로)
                     	<option value="1">전자제품</option>
                     	<option value="2">의  류</option>
                     	<option value="3">도  서</option>
                  	--%> 
                  	<c:forEach var="cvo" items="${requestScope.categoryList}">
                  		<option value="${cvo.cnum}">${cvo.cname}</option>
                  	</c:forEach>
                  	
               	</select>
               	<span class="error">필수입력</span>
            	</td>   
         	</tr>
         <tr>
           	<td width="25%" class="prodInputName">제품명</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;" >
               	<input type="text" style="width: 300px;" name="pname" class="box infoData" />
               	<span class="error">필수입력</span>
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName">제조사</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;">
               <input type="text" style="width: 300px;" name="pcompany" class="box infoData" />
               <span class="error">필수입력</span>
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName">제품이미지</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;">
               <input type="file" name="pimage1" class="infoData img_file" accept='image/*' /><span class="error">필수입력</span>
               <input type="file" name="pimage2" class="infoData img_file" accept='image/*' /><span class="error">필수입력</span>
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName">제품설명서 파일첨부(선택)</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;">
               <input type="file" name="prdmanualFile" />
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName">제품수량</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;">
               <input id="spinnerPqty" name="pqty" value="1" style="width: 30px; height: 20px;"> 개
               <span class="error">필수입력</span>
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName">제품정가</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;">
               <input type="text" style="width: 100px;" name="price" class="box infoData" /> 원
               <span class="error">필수입력</span>
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName">제품판매가</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;">
               <input type="text" style="width: 100px;" name="saleprice" class="box infoData" /> 원
               <span class="error">필수입력</span>
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName">제품스펙</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;">
               <select name="fk_snum" class="infoData">
                  <option value="">:::선택하세요:::</option>
                  <%-- 
                     <option value="1">HIT</option>
                     <option value="2">NEW</option>
                     <option value="3">BEST</option> 
                  --%>
                  
                  <c:forEach var="svo" items="${requestScope.specList}">
                  		<option value="${svo.snum}">${svo.sname}</option>
                  </c:forEach>
                     
               </select>
               <span class="error">필수입력</span>
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName">제품설명</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden;">
               <textarea name="pcontent" rows="5" cols="60"></textarea>
            </td>
         </tr>
         <tr>
            <td width="25%" class="prodInputName" style="padding-bottom: 10px;">제품포인트</td>
            <td width="75%" align="left" style="border-top: hidden; border-bottom: hidden; padding-bottom: 10px;">
               <input type="text" style="width: 100px;" name="point" class="box infoData" /> POINT
               <span class="error">필수입력</span>
            </td>
         </tr>
         
         <%-- ==== 추가이미지파일을 마우스 드래그앤드롭(DragAndDrop)으로 추가하기 ==== --%>
          <tr>
                <td width="25%" class="prodInputName" style="padding-bottom: 10px;">추가이미지파일(선택)</td>
                <td>
                   <span style="font-size: 10pt;">파일을 1개씩 마우스로 끌어 오세요</span>
                <div id="fileDrop" class="fileDrop border border-secondary"></div>
                </td>
          </tr>
          
          <%-- ==== 이미지파일 미리보여주기 ==== --%>
          <tr>
                <td width="25%" class="prodInputName" style="padding-bottom: 10px;">이미지파일 미리보기</td>
                <td>
                   <img id="previewImg" width="300" />
                </td>
          </tr>
         
         <tr style="height: 70px;">
            <td colspan="2" align="center" style="border-left: hidden; border-bottom: hidden; border-right: hidden; padding: 50px 0;">
                <input type="button" value="제품등록" id="btnRegister" style="width: 120px;" class="btn btn-info btn-lg mr-5" /> 
                <input type="reset" value="취소"  style="width: 120px;" class="btn btn-danger btn-lg" />   
            </td>
         </tr>
      </tbody>
      </table>
      
   </form>

</div>
    
    
<jsp:include page="../../footer2.jsp" />      