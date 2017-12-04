<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="../module/side.jsp"></c:import>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style>
	th{
		width:25%;
		backgorund-color:#ddd;
		text-align:center;
	}
	
	td{
		text-align:center;
	}
	
	.listTh{
		width:16%;
	}
</style>

<script>
	function openList(check){
		if(check == 1){
			$('#listTb').css('display','');
		}else if(check == 2){
			$('#listTb').css('display','none');
		}
	}
</script>
</head>
<body>
<div id="page-wrapper">	
	<div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">데이터정제결과</h1>
        </div>
        <!-- /.col-lg-12 -->
    </div>
    
    <div class="row">
		<form id="inputForm1" action="dbRefine" method="post">
			<input type="text" name="startNum" id="startNum" placeholder="시작"/>
			<input type="text" name="endNum" id="endNum" placeholder="끝"/>
			<button type="button" onclick="submit(1);" style="background-color:#9FC93C;color:black;font-weight:bold;">정제하기</button>
		</form>
	</div>
	<br/>
	<div class="row">
		<form id="inputForm2" action="dbRefine" method="post">
			<input type="text" name="startNum" id="startNum2" placeholder="시작" />
			<button type="button" onclick="submit(2);" style="background-color:#9FC93C;color:black;font-weight:bold;">1000개 정제</button>
		</form>
	</div>
	
	<div class="row">
		<table style="width:100%;">
			<tr>
				<th>총카운트</th>
				<th>비교수정</th>
				<th>신규등록</th>
				<th>-</th>
			</tr>
			<tr>
				<td>${totalCount }</td>
				<td>${updateCount }</td>
				<td>${noUpdateCount}</td>
				<td><button type="button" onclick="openList(1);">목록</button></td>
			</tr>			
		</table>
		
		<br/>
		<table id="listTb" style="display:none;width:100%;">
			<tr>
				<th colspan="5" style="border:2px solid #ddd;">
					신규업로드 목록
				</th>
				<th>
					<button type="button" onclick="openList(2);">목록닫기</button>
				</th>
			</tr>
			<tr>
				<th class="listTh">이름</th>
				<th class="listTh">관계1</th>
				<th class="listTh">관계2</th>
				<th class="listTh">핸드폰</th>
				<th class="listTh">집</th>
				<th class="listTh">회사</th>
			</tr>
			<c:forEach var="list" items="${failList }">
				<td>${list.name }</td>
				<td>${list.relation1}</td>
				<td>${list.relation2}</td>
				<td>${list.hp}</td>
				<td>${list.homePhone}</td>
				<td>${list.mainPhone}</td>
			</c:forEach>
		</table>
	</div>
</div>
</body>
</html>