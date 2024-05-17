
$(document).ready(function(){

    $("button#btnSubmit").click(function(){
        goLogin();

    });

    $("input#loginPwd").bind("keydown", function(e){
        if(e.keyCode == 13) { //  암호입력란에 엔터를 했을 경우
            goLogin(); //  로그인 시도한다.
        }
    });





});// end of $(document).ready(function(){})------------------------------------


// function Declaration
 
// === 로그인 처리 함수 === //
function goLogin() {

    // alert("확인용 로그인 처리하러 간다");

    if( $("input#loginUserid").val().trim() == "" ) {
        alert("아이디를 입력하세요!!");
        $("input#loginUserid").val("").focus();
        return; // goLogin() 함수 종료
    }

    if( $("input#loginPwd").val().trim() == "" ) {
        alert("암호를 입력하세요!!");
        $("input#loginPwd").val("").focus();
        return; // goLogin() 함수 종료
    }


    if($("input:checkbox[id='saveid']").prop("checked")) { // ==> input:checkbox#saveid
        // alert("아이디 저장 체크를 하셨네요~");
        localStorage.setItem('saveid', $("input#loginUserid").val());
                            //  key                value
        
    } else {
        // alert("아이디 저장 체크를 해제하셨네요~");
        localStorage.removeItem('saveid');
    }

/*
    == 보안상 민감한 데이터는 로컬 스토리지 또는 세션 스토리지에 저장해두면 안 된다 !!! ==

    if($("input:checkbox[id='savepwd']").prop("checked")) {
        localStorage.setItem('savepwd', $("input#loginPwd").val());
                            //  key                value
        
    } else {
        localStorage.removeItem('savepwd');
    }
*/

    const frm = document.loginFrm;
    frm.submit();

} // end of function goLogin()------------------------



function goLogOut(ctx_Path) {

    // 로그아웃을 처리해주는 페이지로 이동
    location.href=`${ctx_Path}/login/logout.up`;

} // end of function goLogOut()-----------------------



// === 코인충전 결제금액 선택하기(실제로 카드 결제) === //
function goCoinPurchaseTypeChoice(userid, ctx_Path) {

    // 코인충전 결제금액 선택하기 팝업창 띄우기
    const url = `${ctx_Path}/member/coinPurchaseTypeChoice.up?userid=${userid}`;

    // 너비 650, 높이 570 인 팝업창을 화면 가운데 위치시키기
    const width = 650;
    const height = 570;

    const left = Math.ceil((window.screen.width - width) / 2); // {(모니터 너비 - 팝업창 너비) / 2} 를 정수로 만들기
    //           1400 - 650 = 750  ---->  750/2 ==> 375

    const top = Math.ceil((window.screen.height - height) / 2); // {(모니터 높이 - 팝업창 높이) / 2} 를 정수로 만들기
    //           900 - 570 = 330  ---->  330/2 ==> 165

    window.open(url, "coinPurchaseTypeChoice",
                `left=${left}, top=${top}, width=${width}, height=${height}`);

} // end of function goCoinPurchaseTypeChoice(userid, ctx_Path) ------------



// === 포트원(구 아임포트) 을 사용하여 결제를 해주는 함수 === //
function goCoinPurchaseEnd(ctxPath, coinmoney, userid) {

    // alert(`확인용 부모창의 함수 호출\n결제금액 : ${coinmoney}원, 사용자id : ${userid}`);

    // >>> 포트원(구 아임포트) 결제 팝업창 띄우기 <<<
    // 너비 1000, 높이 600 인 팝업창을 화면 가운데 위치시키기
    const width = 1000;
    const height = 600;
    
    const left = Math.ceil((window.screen.width - width) / 2); // {(모니터 너비 - 팝업창 너비) / 2} 를 정수로 만들기
    const top = Math.ceil((window.screen.height - height) / 2); // {(모니터 높이 - 팝업창 높이) / 2} 를 정수로 만들기
    
    const url = `${ctxPath}/member/coinPurchaseEnd.up?coinmoney=${coinmoney}&userid=${userid}`;

    window.open(url, "coinPurchaseEnd",
                `left=${left}, top=${top}, width=${width}, height=${height}`);
    
} // end of function goCoinPurchaseEnd() -------------------



// ==== DB 상의 tbl_member 테이블에 해당 사용자의 코인금액 및 포인트를 증가(update)시켜주는 함수 === //
function goCoinUpdate(ctxPath, userid, coinmoney) {

    // console.log(`~~ 확인용 userid : ${userid}, coinmoney : ${coinmoney}원`);
    $.ajax({
        url: ctxPath + "/member/coinUpdateLoginUser.up",
        data: {"userid":userid,
               "coinmoney":coinmoney}, // data 속성은 http://localhost:9090/MyMVC/member/coinUpdateLoginUser.up 로 전송해야 할 데이터를 말한다.
        type: "post",   // type을 생략하면 type : "get" 이다.
       
        async : true,   // async:true 가 비동기 방식을 말한다. async 을 생략하면 기본값이 비동기 방식인 async:true 이다.
                        // async:false 가 동기 방식이다. 지도를 할 때는 반드시 동기방식인 async:false 을 사용해야만 지도가 올바르게 나온다.

        dataType : "json", // Javascript Standard Object Notation.  dataType은 /MyMVC/member/coinUpdateLoginUser.up 로 부터 실행된 결과물을 받아오는 데이터타입을 말한다.
        // 만약에 dataType:"xml" 으로 해주면 /MyMVC/member/coinUpdateLoginUser.up 로 부터 받아오는 결과물은 xml 형식이어야 한다.
        // 만약에 dataType:"json" 으로 해주면 /MyMVC/member/coinUpdateLoginUser.up 로 부터 받아오는 결과물은 json 형식이어야 한다.

        success : function(json) {

            // console.log("~~~~ 확인용 json => ", json);
            // {loc: '/MyMVC/index.up', message: '김다영님의 300,000원 결제가 완료되었습니다.', n: 1}

            alert(json.message);
            location.href = json.loc;
        },
        
        error: function(request, status, error) {
            alert("code: " + request.status + "\n" + "message: " + request.responseText + "\n" + "error: " + error);
        }

    });

} // end of function goCoinUpdate(userid, coinmoney) -----------------


// === 나의정보 수정하기 === //
function goEditMyInfo(userid, ctx_Path) {
   
    // 나의정보 수정하기 팝업창 띄우기
    const url = `${ctx_Path}/member/memberEdit.up?userid=${userid}`;
 //  또는
 //  const url = ctx_Path+"/member/memberEdit.up?userid="+userid;   
    
    // 너비 800, 높이 680 인 팝업창을 화면 가운데 위치시키기
    const width = 800;
    const height = 680;
 
    const left = Math.ceil((window.screen.width - width)/2);  // 정수로 만듦 
    const top = Math.ceil((window.screen.height - height)/2); // 정수로 만듦
    window.open(url, "myInfoEdit", 
                `left=${left}, top=${top}, width=${width}, height=${height}`); 
    
 } // end of function goEditMyInfo(userid, ctx_Path) ----------