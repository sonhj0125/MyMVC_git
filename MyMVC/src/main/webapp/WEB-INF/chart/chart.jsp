<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% 
   String ctxPath = request.getContextPath(); 
        // MyMVC
%>

<jsp:include page="../header1.jsp" />

<style type="text/css">   

   .highcharts-figure,
   .highcharts-data-table table {
      min-width: 320px;
      max-width: 800px;
      margin: 1em auto;
   }
   
   .highcharts-data-table table {
       font-family: Verdana, sans-serif;
       border-collapse: collapse;
       border: 1px solid #ebebeb;
       margin: 10px auto;
       text-align: center;
       width: 100%;
       max-width: 500px;
   }
   
   .highcharts-data-table caption {
       padding: 1em 0;
       font-size: 1.2em;
       color: #555;
   }
   
   .highcharts-data-table th {
       font-weight: 600;
       padding: 0.5em;
   }
   
   .highcharts-data-table td,
   .highcharts-data-table th,
   .highcharts-data-table caption {
       padding: 0.5em;
   }
   
   .highcharts-data-table thead tr,
   .highcharts-data-table tr:nth-child(even) {
       background: #f8f8f8;
   }
   
   .highcharts-data-table tr:hover {
       background: #f1f7ff;
   }
   
   input[type="number"] {
       min-width: 50px;
   }
 
    div#table_container table {width: 100%}
    div#table_container th, div#table_container td {border: solid 1px gray; text-align: center;} 
    div#table_container th {background-color: #595959; color: white;}
    
</style>

<script src="<%= ctxPath%>/Highcharts-10.3.1/code/highcharts.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/exporting.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/export-data.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/accessibility.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/series-label.js"></script>

<div style="display: flex;">   
<div style="width: 80%; min-height: 1100px; margin:auto; ">

   <h2 style="margin: 50px 0;">${sessionScope.loginuser.name} 님의 주문통계 차트</h2>
   
   <form name="searchFrm" style="margin: 20px 0 50px 0; ">
      <select name="searchType" id="searchType" style="height: 40px;">
         <option value="">통계선택하세요</option>
         <option value="myPurchase_byCategory">나의 카테고리별주문 통계</option>
         <option value="myPurchase_byMonth_byCategory">나의 카테고리별 월별주문 통계</option>
      </select>
   </form>
   
   <div id="chart_container"></div>
   <div id="table_container" style="margin: 40px 0 0 0;"></div>

</div>
</div>

<script type="text/javascript">

   $(document).ready(function(){
      
      $("select#searchType").change(function(e){
         func_choice($(e.target).val());
         //$(e.target).val() 은 "" 또는 "myPurchase_byCategory" 또는 "myPurchase_byMonth_byCategory" 이다.
      });
      
      // 문서가 로드 되어지면 나의 카테고리별주문 통계 페이지가 보이도록 한다.
      $("select#searchType").val("myPurchase_byCategory").trigger("change");
      
   });// end of $(document).ready(function(){})----------------------
   
   //Function Declaration
   function func_choice(searchTypeVal) {
      
      // alert(searchTypeVal);
      
      switch (searchTypeVal) {
      
         case "": // 통계선택하세요 를 선택한 경우
         
         	$("div#chart_container").empty();
            $("div#table_container").empty();
           	$("div.highcharts-data-table").empty();
           	
            break;
            
         case "myPurchase_byCategory": // 나의 카테고리별주문 통계 를 선택한 경우
            
            $.ajax({
               url:"<%= ctxPath%>/shop/myPurchase_byCategoryJSON.up",
               data:{"userid":"${sessionScope.loginuser.userid}"},
               dataType:"json",
               success:function(json){
	               
            	   // console.log(JSON.stringify(json));
                   /*
                     [{"sumpay_pct":"61.88","cname":"전자제품","cnt":"1","sumpay":"1000000"},
                      {"sumpay_pct":"29.95","cname":"의류","cnt":"5","sumpay":"484000"},
                      {"sumpay_pct":"8.17","cname":"도서","cnt":"1","sumpay":"132000"}]
                   */
	            
                  let resultArr =[];
                  
                  for(let i=0; i<json.length; i++){
                     
                     let obj;
                     
                     if(i==0) {
                        obj = {name: json[i].cname,
                                y: Number(json[i].sumpay_pct),
                                sliced: true,
                                selected: true};
                     }
                     else {
                        obj = {name: json[i].cname,
                               y: Number(json[i].sumpay_pct)};
                     }
                     
                     resultArr.push(obj); // 배열 속에 객체 넣어주기
                     
                  }// end of for()----------------------------------
                  
                  /////////////////////////////////////////////////////////////////
                  Highcharts.chart('chart_container', {
                      chart: {
                          plotBackgroundColor: null,
                          plotBorderWidth: null,
                          plotShadow: false,
                          type: 'pie'
                      },
                      title: {
                          text: '나의주문 통계'
                      },
                      tooltip: {
                          pointFormat: '{series.name}: <b>{point.percentage:.2f}%</b>'
                      },
                      accessibility: {
                          point: {
                              valueSuffix: '%'
                          }
                      },
                      plotOptions: {
                          pie: {
                              allowPointSelect: true,
                              cursor: 'pointer',
                              dataLabels: {
                                  enabled: true,
                                  format: '<b>{point.name}</b>: {point.percentage:.2f} %'
                              }
                          }
                      },
                      series: [{
                          name: '주문금액비율',
                          colorByPoint: true,
                          data: resultArr
                      }]
                  });
                  //////////////////////////////////////////////////////////////////////
                  
                  let html = "<table>";
                           html += "<tr>" +
                                 "<th>카테고리</th>" +
                                 "<th>주문수</th>" +
                                 "<th>총주문금액</th>" +
                                 "<th>퍼센티지</th>" +
                                 "</tr>";
     
                  $.each(json, function(index, item){
                  	html += "<tr>" +
                            "<td>"+ item.cname +"</td>" +
                            "<td>"+ item.cnt +"</td>" +
                            "<td style='text-align: right;'>"+ Number(item.sumpay).toLocaleString('en') +" 원</td>" +
                            "<td>"+ Number(item.sumpay_pct) +" %</td>" +
                            "</tr>";
                  });        
                              
                    html += "</table>";
                    
                    $("div#table_container").html(html);
                  
               },
               error: function(request, status, error){
                        alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
                     }
            });
         
            break;
         case "myPurchase_byMonth_byCategory": // 나의 카테고리별 월별주문 통계 를 선택한 경우
         
         	$.ajax({
				url:"<%= ctxPath%>/shop/myPurchase_byMonth_byCategoryJSON.up",
	            data:{"userid":"${sessionScope.loginuser.userid}"},
	            dataType:"JSON",
	            success:function(json){
	         
	            // console.log(JSON.stringify(json));
	            /*
	             [{"m_11":"0","m_01":"0","m_12":"0","m_10":"0","m_04":"0","sumpay_pct":"95.06","m_05":"0","m_02":"0","m_03":"1000000","cname":"전자제품","cnt":"1","m_08":"0","m_09":"0","m_06":"0","m_07":"0","sumpay":"1000000"}
	             ,{"m_11":"0","m_01":"52000","m_12":"0","m_10":"0","m_04":"0","sumpay_pct":"4.94","m_05":"0","m_02":"0","m_03":"0","cname":"의류","cnt":"1","m_08":"0","m_09":"0","m_06":"0","m_07":"0","sumpay":"52000"}]
	            */
	          	 $("div#chart_container").empty();
	          	 $("div#table_container").empty();
	           	 $("div.highcharts-data-table").empty();
	                                                   
	             var resultArr = [];
	             
	             for(var i=0; i<json.length; i++) {
	                var month_arr = [];
	                month_arr.push(Number(json[i].m_01));
	                month_arr.push(Number(json[i].m_02));
	                month_arr.push(Number(json[i].m_03));
	                month_arr.push(Number(json[i].m_04));
	                month_arr.push(Number(json[i].m_05));
	                month_arr.push(Number(json[i].m_06));
	                month_arr.push(Number(json[i].m_07));
	                month_arr.push(Number(json[i].m_08));
	                month_arr.push(Number(json[i].m_09));
	                month_arr.push(Number(json[i].m_10));
	                month_arr.push(Number(json[i].m_11));
	                month_arr.push(Number(json[i].m_12));
	                var obj= {name: json[i].cname, 
	                        data: month_arr};
	                
	                resultArr.push(obj); // 배열속에 객체를 넣기 
	             }// end of for--------------------------
	             
	                ////////////////////////////////////////////////////////
	                Highcharts.chart('chart_container', {
	                    
	                    title: {
	                        text: new Date().getFullYear()+'년 카테고리별 월별주문 통계'
	                    },
	                    
	                    subtitle: {
	                        text: 'Source: <a href="http://localhost:9090/board/emp/empList.action" target="_blank">HR.employees</a>'
	                    },
	
	                    yAxis: {
	                        title: {
	                            text: '주문 금액'
	                        }
	                    },
	
	                    xAxis: {
	                        accessibility: {
	                            rangeDescription: '범위: 1 to 12'
	                        }
	                    },
	
	                    legend: {
	                        layout: 'vertical',
	                        align: 'right',
	                        verticalAlign: 'middle'
	                    },
	
	                    plotOptions: {
	                        series: {
	                            label: {
	                                connectorAllowed: false
	                            },
	                            pointStart: 1
	                        }
	                    },
	                    
	                    series: resultArr,
	                    
	                    responsive: {
	                        rules: [{
	                            condition: {
	                                maxWidth: 500
	                            },
	                            chartOptions: {
	                                legend: {
	                                    layout: 'horizontal',
	                                    align: 'center',
	                                    verticalAlign: 'bottom'
	                                }
	                            }
	                        }]
	                    }
                        
                	});
                    ////////////////////////////////////////////////////////   
                    
                    var html =  "<table>";
                             html += "<tr>" +
                                       	"<th>카테고리</th>" +
                                       	"<th>01월</th>" +
                                       	"<th>02월</th>" +
                                       	"<th>03월</th>" +
                                       	"<th>04월</th>" +
                                       	"<th>05월</th>" +
                                       	"<th>06월</th>" +
                                       	"<th>07월</th>" +
                                       	"<th>08월</th>" +
                                      	"<th>09월</th>" +
                                       	"<th>10월</th>" +
                                       	"<th>11월</th>" +
                                       	"<th>12월</th>" +
                                     "</tr>";
                    
                         $.each(json, function(index, item){
                            html += "<tr>" +
                                        "<td>"+ item.cname +"</td>" +
                                        "<td>"+ Number(item.m_01).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_02).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_03).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_04).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_05).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_06).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_07).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_08).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_09).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_10).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_11).toLocaleString('en') +"</td>" +
                                        "<td>"+ Number(item.m_12).toLocaleString('en') +"</td>" +
                                    "</tr>";
                         });        
                                 
                         html += "</table>";
                         
                         $("div#table_container").html(html);
                 },
                 error: function(request, status, error){
                    alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
                 }
              });
            break;

      }// end of switch (searchTypeVal)---------------
      
   }// end of function func_choice(searchTypeVal)-----------------


</script>

<jsp:include page="../footer1.jsp" />