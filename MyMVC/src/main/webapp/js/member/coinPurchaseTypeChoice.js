
$(document).ready(function() {

    $("td#error").hide();

    $("input:radio[name='coinmoney']").bind("click", e => {
        // 라디오 버튼 선택 시 포인트에 css 강조 표시

        const index = $("input:radio[name='coinmoney']").index($(e.target));
        console.log("~~ 확인용 index => ", index);
    /*
        ~~ 확인용 index =>  0
        ~~ 확인용 index =>  1
        ~~ 확인용 index =>  2
    */
        // span 태그 css 모두 제거
        $("td > span").removeClass("stylePoint");

        // 해당되는 span 태그만 css 적용
        $("td > span").eq(index).addClass("stylePoint"); // $("td > span").eq(index) ==> 자바스크립트의 document.querySelectorAll("td > span")[index] 와 같음
        // $("td>span").eq(index); ==> $("td>span")중에 index 번째의 요소인 엘리먼트를 선택자로 보는 것이다.
        //                             $("td>span")은 마치 배열로 보면 된다. $("td>span").eq(index) 은 배열중에서 특정 요소를 끄집어 오는 것으로 보면 된다. 예를 들면 arr[i] 와 비슷한 뜻이다.
        
    }); // end of $("input:radio[name='coinmoney']").bind("click", () => {}) -------------


    // [충전결제하기] 에 마우스를 올리거나 마우스를 뺄 때
    $("td#purchase").hover(function(e) {
        // mouseover일 때
        $(e.target).addClass("purchase");

    }, function(e) {
        // mouseout일 때
        $(e.target).removeClass("purchase");

    }); // $("td#purchase").hover(function(e) {}, function(e) {}) -----------------


}); // end of $(document).ready(function() {}) -------------------------------------



// [충전결제하기] 를 클릭했을 때 이벤트 처리하기
function goCoinPayment(ctxPath, userid) {

    const checked_cnt = $("input:radio[name='coinmoney']:checked").length; // 라디오 태그 중 선택된 태그의 개수

    if(checked_cnt == 0) {
        // 결제금액을 선택하지 않았을 경우
        $("td#error").show();
        return; // 함수 종료

    } else {
        // == 결제하기 ==

        const coinmoney = $("input:radio[name='coinmoney']:checked").val(); // 충전금액
        // alert(`${coinmoney}원 결제하러 들어간다.`);

        /* === 팝업창에서 부모창 함수 호출 방법 3가지 ===
            1-1. 일반적인 방법
            opener.location.href = "javascript:부모창스크립트 함수명();";
                            
            1-2. 일반적인 방법
            window.opener.부모창스크립트 함수명();

            2. jQuery를 이용한 방법
            $(opener.location).attr("href", "javascript:부모창스크립트 함수명();");
        */

        opener.location.href = `javascript:goCoinPurchaseEnd("${ctxPath}", "${coinmoney}", "${userid}")`; // 매개변수가 문자일 경우 "${userid}" 와 같이 따옴표 붙이기!!

        self.close(); // 자신의 팝업창을 닫는다.
    }

} // end of function goCoinPayment(ctx_Path, userid) --------------------