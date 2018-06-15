<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="../module/side.jsp"></c:import>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<!-- 우편번호(다음) -->
<script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
<style>
	th{
		margin:5px;
		padding:5px;
		background-color:#ddd;
	}
	td{
		margin:5px;
		padding:5px;
	}
</style>

<script>
	function deleteData(seqNo){
		alert(seqNo)
		/* location.href = 'deleteData?seqNo='+seqNo; */
		$.ajax({
			url : 'deleteData',
			data : {'seqNo':seqNo},
			type : 'post',
			dataType:'json',
			success: function(data){
				if(data.check == 'delete') {
					alert("삭제됨.");
					window.location.reload(true);
				}			
			}
		})
	}
	
	function modifyData(seqNo, seqNo2){
		/* location.href='refineData?seqNo='+seqNo+'&seqNo2='+seqNo2; */
		if(confirm('정말 수정하겠습니까?')){
			$.ajax({
				url : 'modifyData',
				data : {'seqNo':seqNo, 'seqNo2':seqNo2},
				type : 'post',
				dataType:'json',
				success: function(data){
					alert("수정됨.");
					window.location.reload(true);
				}
			})
		}	
	}
</script>
</head>
<body>
<div id="page-wrapper">
	<div class="row">
		<h3>비교원형</h3>
		<table>
			<tr>
				<th>번호</th>
				<th>이름</th>
				<th>단대</th>
				<th>학과</th>
				<th>입학</th>
				<th>졸업</th>
				<th>회사</th>
				<th>부서</th>
				<th>직책</th>
			</tr>
			<tr>
				<td>${user.seqNo }</td>
				<td>${user.name}</td>
				<td>${user.relation1}</td>
				<td>${user.relation2}</td>
				<td>${user.admission}</td>
				<td>${user.graduated}</td>
				<td>${user.company}</td>
				<td>${user.department}</td>
				<td>${user.position}</td>
			</tr>
			<tr>
				<th>핸드폰</th>
				<th>집번호</th>
				<th>메인폰</th>
				<th colspan="2">집주소</th>
				<th colspan="2">회사주소</th>
				<th>이메일</th>
				<th>학위</th>
			</tr>
			<tr>
				<td>${user.hp}</td>
				<td>${user.homePhone}</td>
				<td>${user.mainPhone}</td>
				<td colspan="2">${user.homeAddress}</td>
				<td colspan="2">${user.comAddress}</td>
				<td>${user.email}</td>
				<td>${user.etc3}</td>
			</tr>			
		</table>
	</div>
	<br/>
	<div class="row">
		<c:forEach var="list" items="${list}" varStatus="i">
			<h3>검색된 데이터 ${i.index +1} <%-- (${list.etc2 }) --%></h3>
			<table style="width:100%;">
				<tr>
					<th>번호</th>
					<th>이름</th>
					<th>단대</th>
					<th>학과</th>
					<th>입학</th>
					<th>졸업</th>
					<th>회사</th>
					<th>부서</th>
					<th>직책</th>
				</tr>
				<tr>
					<td>${list.seqNo }</td>
					<td>${list.name}</td>
					<td>${list.relation1}</td>
					<td>${list.relation2}</td>
					<td>${list.admission}</td>
					<td>${list.graduated}</td>
					<td>${list.company}</td>
					<td>${list.department}</td>
					<td>${list.position}</td>
				</tr>
				<tr>
					<th>핸드폰</th>
					<th>집번호</th>
					<th>메인폰</th>
					<th colspan="2">집주소</th>
					<th colspan="2">회사주소</th>
					<th>이메일</th>
					<th>학위</th>
				</tr>
				<tr>
					<td>${list.hp}</td>
					<td>${list.homePhone}</td>
					<td>${list.mainPhone}</td>
					<td colspan="2">${list.homeAddress}</td>
					<td colspan="2">${list.comAddress}</td>
					<td>${list.email}</td>
					<td>${list.etc3}</td>
				</tr>
				<tr>
					<td colspan="9">
						<%-- <button type="button" class="btn btn-danger" onclick="deleteData('${list.seqNo}')">삭제</button> --%>
						<button type="button" class="btn btn-success" onclick="modifyData('${list.seqNo}','${user.seqNo}')">수정</button>
					</td>
				</tr>
			</table>
			<br/>
		</c:forEach>		
	</div>
</div>
</body>
</html>