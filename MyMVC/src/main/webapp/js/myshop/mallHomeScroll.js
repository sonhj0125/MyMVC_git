
$(document).ready(function(){

    $("span#totalHITCount").hide();
    $("span#countHIT").hide();
    
    let start = 1;
    let lenHIT = 8;
    // HIT 상품 "스크롤"을 할 때 보여줄 상품의 개수(단위)크기 

    // HIT상품 게시물을 더보기 위하여 "스크롤" 버튼 클릭액션에 대한 초기값 호출하기 
    // 즉, 맨처음에는 "스크롤" 을 하지 않더라도 스크롤한 것처럼 8개의 HIT상품을 게시해주어야 한다는 말이다.
    displayHIT(start);

    // === 스크롤 이벤트 발생시키기 시작 ===
    $(window).scroll(function() {

        // 스크롤탑의 위치값
        // console.log("$(window).scrollTop() => ", $(window).scrollTop());

        // 보여주어야 할 문서의 높이값(더보기를 해주므로 append 되어서 높이가 계속 증가될 것이다)
        // console.log("$(document).scrollTop() => ", $(document).scrollTop());

        // 웹브라우저창의 높이값(디바이스마다 다르게 표현되는 고정값) 
        // console.log("$(window).height() => ", $(window).height());

        // 아래는 스크롤된 스크롤탑의 위치값이 웹브라우저창의 높이만큼 내려갔을 경우를 알아보는 것이다.
        // console.log("$(window).scrollTop() => ", $(window).scrollTop());
        // console.log("$(document).height() - $(window).height() => ", ($(document).height() - $(window).height())); // 306
        // 문서의 높이 - 웹브라우저창의 높이(고정값)

        // 아래는 만약에 위의 값이 제대로 나오지 않는 경우 이벤트가 발생되는 숫자를 만들기 위해서 스크롤탑의 위치값에 +1 을 더해서 보정해준 것이다.
        // console.log( "$(window).scrollTop() + 1  => " + ( $(window).scrollTop() + 1  ) );
        // console.log( "$(document).height() - $(window).height() => " + ( $(document).height() - $(window).height() ) );

        if($(window).scrollTop() == ($(document).height() - $(window).height())) {
        // 만약에 위의 값대로 잘 되지 않으면 아래의 것을 사용하도록 한다.     
        // if( $(window).scrollTop() + 1 >= $(document).height() - $(window).height() ) {

            // alert("기존 문서내용을 끝까지 봤습니다. 이제 새로운 내용을 읽어다 보여드리겠습니다.");

            if($("span#totalHITCount").text() != $("span#countHIT").text()) {
                
                start += lenHIT;
                displayHIT(start);
            }

        }
        
        if($(window).scrollTop() == 0) {
            // 다시 처음부터 시작하도록 한다.
            $("div#displayHIT").empty();
            $("span#end").empty();
            $("span#countHIT").text("0");

            start = 1;
            displayHIT(start);
        }

    }); // end of $(window).scroll(function() {}) -----------------------
    
}); // end of $(document).ready(function(){})-------------------


// Function Declaration

let lenHIT = 8;
// HIT 상품 "스크롤" 을 할 때 보여줄 상품의 개수(단위)크기 

function displayHIT(start) { // start가  1 이라면   1~ 8  까지 상품 8개를 보여준다.
                      // start가  9 이라면   9~16  까지 상품 8개를 보여준다.
                      // start가 17 이라면  17~24  까지 상품 8개를 보여준다.
                      // start가 25 이라면  25~32  까지 상품 8개를 보여준다.
                      // start가 33 이라면  33~36  까지 상품 4개를 보여준다.(마지막 상품)
                      
   $.ajax({
      url:"mallDisplayJSON.up",
    //  type:"get",
        data:{"sname":"HIT", 
              "start":start,  //  "1"  "9"  "17"  "25"  "33"
              "len":lenHIT},  //   8    8     8     8     8 
        dataType:"json",
        success:function(json) {
        /*       
         console.log(json);
          console.log(typeof json); // object
         
         const str_json = JSON.stringify(json); // JSON 객체를 string 타입으로 변경시켜주는 것. 
         console.log(typeof str_json); // string
         console.log(str_json); 
         
         const obj_json = JSON.parse(str_json); // JSON 모양으로 되어진 string 을 실제 JSON 객체로 변경시켜주는 것. 
         console.log(typeof obj_json); // object
         console.log(obj_json);
      */
        
        /*
           json ==> [{"pnum":36,"discountPercent":17,"pname":"노트북30","pcompany":"삼성전자","cname":"전자제품","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"59.jpg","pqty":100,"pimage2":"60.jpg","pcontent":"30번 노트북","price":1200000,"sname":"HIT"}
                  ,{"pnum":35,"discountPercent":17,"pname":"노트북29","pcompany":"레노버","cname":"전자제품","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"57.jpg","pqty":100,"pimage2":"58.jpg","pcontent":"29번 노트북","price":1200000,"sname":"HIT"}
                  ,{"pnum":34,"discountPercent":17,"pname":"노트북28","pcompany":"아수스","cname":"전자제품","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"55.jpg","pqty":100,"pimage2":"56.jpg","pcontent":"28번 노트북","price":1200000,"sname":"HIT"}
                  ,{"pnum":33,"discountPercent":17,"pname":"노트북27","pcompany":"애플","cname":"전자제품","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"53.jpg","pqty":100,"pimage2":"54.jpg","pcontent":"27번 노트북","price":1200000,"sname":"HIT"}
                  ,{"pnum":32,"discountPercent":17,"pname":"노트북26","pcompany":"MSI","cname":"전자제품","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"51.jpg","pqty":100,"pimage2":"52.jpg","pcontent":"26번 노트북","price":1200000,"sname":"HIT"}
                  ,{"pnum":31,"discountPercent":17,"pname":"노트북25","pcompany":"삼성전자","cname":"전자제품","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"49.jpg","pqty":100,"pimage2":"50.jpg","pcontent":"25번 노트북","price":1200000,"sname":"HIT"}
                  ,{"pnum":30,"discountPercent":17,"pname":"노트북24","pcompany":"한성컴퓨터","cname":"전자제품","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"47.jpg","pqty":100,"pimage2":"48.jpg","pcontent":"24번 노트북","price":1200000,"sname":"HIT"}
                  ,{"pnum":29,"discountPercent":17,"pname":"노트북23","pcompany":"DELL","cname":"전자제품","saleprice":1000000,"point":60,"pinputdate":"2024-05-14","pimage1":"45.jpg","pqty":100,"pimage2":"46.jpg","pcontent":"23번 노트북","price":1200000,"sname":"HIT"}
                  ]

           또는
           
           json ==> []
        */
        
           let v_html = ``;

           if(start == "1" && json.length == 0) {
                // 처음부터 데이터가 존재하지 않는 경우
                /* !!!! 주의 !!!!
                    if(start == "1" && json == null) 이 아님!!!
                    if(start == "1" && json.length) 로 해야 함!!!
                */
                v_html = `현재 상품 준비중입니다...`;
                $("div#displayHIT").html(v_html);
            }
            
            else if(json.length > 0) {
                // 데이터가 존재하는 경우 

            /*   
                    // 자바스크립트 사용하는 경우
                    json.forEach(function(item, index, array){
                        
                    });
                    
                    // jQuery 를 사용하는 경우
                    $.each(json, function(index, item){
                        
                    });
                */   

                $.each(json, function(index, item){
                    v_html += `<div class='col-md-6 col-lg-3'>
                                <div class='card mb-3'>
                                    <img src='/MyMVC/images/${item.pimage1}' class='card-img-top' style='width: 100%' />    
                                    <div class='card-body' style='padding: 0; font-size: 11pt;'>
                                        <ul class='list-unstyled mt-3 pl-3'>
                                            <li><label class='prodInfo'>제품명</label>${item.pname}</li>
                                            <li><label class='prodInfo'>정가</label><span style='text-decoration: line-through; color: red;'>${item.price.toLocaleString('en')} 원</span></li>
                                            <li><label class='prodInfo'>판매가</label><span style='font-weight: bold; color: red;'>${item.saleprice.toLocaleString('en')} 원</span></li>
                                            <li><label class='prodInfo'>할인율</label><span style='font-weight: bold; color: blue;'>[${item.discountPercent}%]할인</span></li>
                                            <li><label class='prodInfo'>포인트</label><span style='font-weight: bold; color: orange;'>${item.point.toLocaleString('en')} POINT</span></li>
                                            <li class='text-center'><a href='/MyMVC/shop/prodView.up?pnum=${item.pnum}' class='btn btn-sm btn-outline-dark stretched-link' role='button'>자세히보기</a></li>
                                            ${''/* 카드 내부의 링크에 .stretched-link 클래스를 추가하면 전체 카드를 클릭할 수 있고 호버링할 수 있습니다(카드가 링크 역할을 함). */}  
                                        </ul>
                                    </div>
                                </div>
                            </div>`;
                });// end of $.each(json, function(index, item){})--------------

                // HIT 상품 결과를 출력하기
                $("div#displayHIT").append(v_html);

                // !!!!! 중요 !!!!!
                // 더보기... 버튼의 value 속성에 값을 지정하기
                $("button#btnMoreHIT").val(Number(start) + lenHIT);
                // 더보기... 버튼의 value 값이  "9" 로 변경된다.
                // 더보기... 버튼의 value 값이 "17" 로 변경된다.
                // 더보기... 버튼의 value 값이 "25" 로 변경된다.
                // 더보기... 버튼의 value 값이 "33" 로 변경된다.
                // 더보기... 버튼의 value 값이 "41" 로 변경된다.(존재하지 않는 것이다)

                // span#countHIT 에 지금까지 출력된 상품의 개수를 누적해서 기록한다.
                $("span#countHIT").text(Number($("span#countHIT").text()) + json.length); 

                // 더보기... 버튼을 계속해서 클릭하여 countHIT 값과 totalHITCount 값이 일치하는 경우 
                if( $("span#countHIT").text() == $("span#totalHITCount").text() ) {
                    $("span#end").html("더이상 조회할 제품이 없습니다.");
                    $("button#btnMoreHIT").text("처음으로");
                    $("span#countHIT").text("0");
                }

            }// end of else if(json.length > 0)--------------------- 

        },       
      error: function(request, status, error) {
         // alert("code: " + request.status + "\n" + "message: " + request.responseText + "\n" + "error: " + error);
      } 
   });                   
   
}// end of function displayHIT(start)------------------


function goTop() {
    $(window).scrollTop(0);
}