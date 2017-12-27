<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<c:import url="./module/side.jsp"></c:import>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- <script src="resources/vendor/jquery/jquery.min.js"></script> -->

<script language="javascript">
function getAddr(){
	alert("주소변환시작!");
	/* $.ajax({
		url : 'test',
		type:'post',
		dataType : 'json',
		success:function(data){
			alert("test success!!");
		}
	}) */
	$.ajax({
		 url :"getAddrApi"  //인터넷망
		,type:"post"
		,data:$("#form").serialize()
		,dataType:"json"
		,success:function(data){
			console.log(data)
			$("#list").html("");
			var html='';
			html += '<table>';
			html += '<tr>';
			html += '<th>총조회카운트</th>';
			html += '<td>'+data.total+'</td>';
			html += '</tr>';
			html += '<tr>';
			html += '<th>성공</th>';
			html += '<td>'+data.success+'</td>';
			html += '</tr>';
			html += '<tr>';
			html += '<th>실패</th>';
			html += '<td>'+data.fail+'</td>';
			html += '</tr>';
			html += '<tr>';
			html += '<th>잘못된주소</th>';
			html += '<td>'+data.wrong+'</td>';
			html += '</tr>';
			html += '</table>';
			
			$("#list").html(html);
			$('#startNum').val(data.startNum);
			$('#endNum').focus();
		}
	    ,error: function(xhr,status, error){
	    	alert("에러발생");
	    }
	}); 
}

function makeListJson(jsonStr){
	var htmlStr = "";
	htmlStr += "<table>";
	$(jsonStr.results.juso).each(function(){
		htmlStr += "<tr>";
		htmlStr += "<td>"+this.roadAddr+"</td>";
		htmlStr += "<td>"+this.roadAddrPart1+"</td>";
		htmlStr += "<td>"+this.roadAddrPart2+"</td>";
		htmlStr += "<td>"+this.jibunAddr+"</td>";
		htmlStr += "<td>"+this.engAddr+"</td>";
		htmlStr += "<td>"+this.zipNo+"</td>";
		htmlStr += "<td>"+this.admCd+"</td>";
		htmlStr += "<td>"+this.rnMgtSn+"</td>";
		htmlStr += "<td>"+this.bdMgtSn+"</td>";
		htmlStr += "<td>"+this.detBdNmList+"</td>";
		/** API 서비스 제공항목 확대 (2017.02) **/
		htmlStr += "<td>"+this.bdNm+"</td>";
		htmlStr += "<td>"+this.bdKdcd+"</td>";
		htmlStr += "<td>"+this.siNm+"</td>";
		htmlStr += "<td>"+this.sggNm+"</td>";
		htmlStr += "<td>"+this.emdNm+"</td>";
		htmlStr += "<td>"+this.liNm+"</td>";
		htmlStr += "<td>"+this.rn+"</td>";
		htmlStr += "<td>"+this.udrtYn+"</td>";
		htmlStr += "<td>"+this.buldMnnm+"</td>";
		htmlStr += "<td>"+this.buldSlno+"</td>";
		htmlStr += "<td>"+this.mtYn+"</td>";
		htmlStr += "<td>"+this.lnbrMnnm+"</td>";
		htmlStr += "<td>"+this.lnbrSlno+"</td>";
		htmlStr += "<td>"+this.emdNo+"</td>";
		htmlStr += "</tr>";
	});
	htmlStr += "</table>";
	$("#list").html(htmlStr);
}

function enterSearch() {
	var evt_code = (window.netscape) ? ev.which : event.keyCode;
	if (evt_code == 13) {    
		event.keyCode = 0;  
		getAddr(); //jsonp사용시 enter검색 
	} 
}
</script>
<title>Insert title here</title>
</head>
<body>
<div id="page-wrapper">
	<div class="row">
		<h2>주소변환</h2>
	</div>
	<br/>
	<div class="row">
		<form name="form" id="form" method="post">
			<input type="hidden" name="currentPage" value="1"/> <!-- 요청 변수 설정 (현재 페이지. currentPage : n > 0) -->
			<input type="hidden" name="countPerPage" value="10"/><!-- 요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100) -->
			<input type="hidden" name="resultType" value="json"/> <!-- 요청 변수 설정 (검색결과형식 설정, json) --> 
			<input type="hidden" name="confmKey" value="U01TX0FVVEgyMDE3MTEyMTE3MDczNTEwNzQ5NzU="/><!-- 요청 변수 설정 (승인키) -->
			<input type="text" name="keyword" value="" placeholder="키워드"/><!-- 요청 변수 설정 (키워드) -->
			<input type="text" name="startNum" id="startNum" placeholder="시작"/>
			<input type="text" name="endNum" id="endNum" placeholder="종료"/>
			<input type="button" onClick="getAddr();" value="주소검색하기"/>
			
		</form>
	</div>
	<br/><br/>
	<div class="row">
		<div id="list" ></div><!-- 검색 결과 리스트 출력 영역 -->
	</div>
</div>
</body>
</html>