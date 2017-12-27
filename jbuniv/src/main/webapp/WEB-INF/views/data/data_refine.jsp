<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="../module/side.jsp"></c:import>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Insert title here</title>
<script>
	function submit(type){
		if(type == 1){
			var startNum = $('#startNum').val();
			var endNum = $('#endNum').val();
			
			if(startNum == null || startNum == ''){
				alert("시작번호를 입력하세요");
				return;
			}else if(endNum == null || endNum == ''){
				alert('끝번호를 입력하세요');
				return;
			}
			
			if(confirm('정제하시겠습니까?') == true) $('#inputForm1').submit();
			
		}else if(type == 2){
			var startNum = $('#startNum2').val();
			if(startNum == null || startNum == ''){
				alert('시작번호를 입력하세요');
				return;
			}
			
			$('#inputForm2').submit();
		}
	}
</script>
</head>
<body>
<div id="page-wrapper">	
	<div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">데이터정제</h1>
        </div>
        <!-- /.col-lg-12 -->
    </div>
	<div class="row">
		<form id="inputForm1" action="dbRefine" method="post">
			<select name="defineType" id="defineType">
				<option value="3">재경인명록</option>
				<option value="2">발전지원부</option>
				<option value="1">어깨동무</option>
			</select>
			<input type="text" name="startNum" id="startNum" placeholder="시작"/>
			<input type="text" name="endNum" id="endNum" placeholder="끝"/>
			<button type="button" onclick="submit(1);" style="background-color:#9FC93C;color:black;font-weight:bold;">정제하기</button>			
		</form>
	</div>
	<br/><br/>
	<div class="row">
		<form id="inputForm2" action="dbRefine" method="post">
			<input type="text" name="startNum" id="startNum2" placeholder="시작" />
			<button type="button" onclick="submit(2);" style="background-color:#9FC93C;color:black;font-weight:bold;">1000개 정제</button>
		</form>
	</div>
</div>
</body>
</html>