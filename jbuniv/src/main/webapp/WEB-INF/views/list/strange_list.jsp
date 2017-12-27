<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<c:import url="../module/side.jsp"></c:import>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/css/bootstrap.css"/>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.15/css/dataTables.bootstrap.css"/>

<!-- <script type="text/javascript" src="https://code.jquery.com/jquery-2.2.4.js"></script> -->
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/js/bootstrap.js"></script>

<script type="text/javascript" src="https://cdn.datatables.net/1.10.15/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/buttons.bootstrap.min.js"></script>

<script type="text/javascript" src="https://cdn.datatables.net/1.10.15/js/dataTables.bootstrap.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/buttons.colVis.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.27/build/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.27/build/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
               
<link rel="StyleSheet" href="<c:url value='resources/css/datatable.css'/>" type="text/css">
<link rel="StyleSheet" href="<c:url value='resources/css/datatableUse.css'/>" type="text/css"> 

<script>
	$(document).ready(function(){
		var startNum = '${startNum}';
		
   		var table = 
        $('#payList').DataTable( {
          dom: 'Bfrtip',
          lengthChange: false,
          autoWidth : false,
          processing: false,
          ordering: true,
          serverSide: false,
          searching: true,
          //lengthMenu : [ [ 3, 5, 10, -1 ], [ 3, 5, 10, "All" ] ],
          pageLength: 10,
          bPaginate: true,
          bLengthChange: false,
          bAutoWidth: false,
          bStateSave: true,
             buttons: ['excel','print'], 
          oLanguage : {
             sProcessing : "처리중...",
             sZeroRecords : "데이터가 없습니다(전체보기시 10초정도 소요될 수 있습니다)",
             oPaginate : {
                   sFirst : "처음",
                   sNext : ">",
                   sPrevious : "<",
                   sLast : "끝"
 
             },
             sInfo : "총_TOTAL_건 중, _START_건부터_END_건까지 표시",
             sInfoEmpty : "0 건 중, 0부터 0 까지 표시", 
             sInfoFiltered : "(총 _MAX_ 건에서 추출 )",
             sSearch : "상세 검색 : "                
       },
	    ajax : {
	   
	      "url":"strangeListAjax?startNum="+startNum,
	      "type":"POST",
	      "dataSrc": function(json){
	    	   var list = json.list;
	    	   
	    	   for(var i=0; i<list.length; i++){
					// 데이터 수정버튼 추가
	   	      		list[i].updateBtn ="<div align='center'><a href='sameCheck?seqNo="+list[i].seqNo+"'><input type='button' value='중복체크'></a></div>";		   	    			   	    	
   	           }
	    	    return json.list
	      	}
		  },            
		  columns : [
		      {data: "seqNo"},
		      {data: "name"},
		      {data: "relation1"},
		      {data: "relation2"},
		      {data: "hp"},
		      {data: "company"},      
		      {data: "homePhone"},
		      {data: "directPhone"},
		      {data: "mainPhone"},
		      {data: "updateBtn"}
		  ],
	         initComplete : function() {
	  
	        	 $('#payList_filter').prepend( $('#buttonWrap')) ;
	        	 
	         } 
	   });               
	});
	
	function nextData(){
		var startNum = parseInt($('#startNum').val()) + 10;
	
		location.href = 'strangeList?startNum='+startNum;
	}
	
	function prevData(){
		if($('#startNum').val() == 0){
			alert("첫페이지 입니다.");
			return;
		}
		var startNum = parseInt($('#startNum').val()) - 10;
		
		location.href = 'strangeList?startNum='+startNum;
	}
	
	function totalData(){
		var relationType = $('#relationType').val();
		var relation1 = $('#relation1').val();
		var startNum = 0;
		var type = 'all';
		
		location.href = 'strangeList?startNum='+startNum;
	}
</script>
</head>
<body>
<input type="hidden" id="startNum" value="${startNum}"/>
<div id="buttonWrap">
	<input class="btn btn-info buttons-excel buttons-html5"  type="button" style="width:89px !important" value="10개 --" onclick="prevData();">
	<input class="btn btn-success buttons-excel buttons-html5"  type="button" style="width:89px !important" value="10개 ++" onclick="nextData();">		  
</div>

<div id="page-wrapper">
	
	<div class="row">
		<div id="content">
			<table id="payList">
				<colgroup>
					<col width="50px">
					<col width="60px">
					<col width="80px">
					<col width="80px">               
					<col width="120px">
					<col width="150px">
					<col width="120px">
					<col width="120px">
					<col width="120px">
					<col width="50px">
				</colgroup>
				<thead>
					<tr>
						<th>NO</th>
						<th>이름</th>
						<th>관계1</th>
						<th>관계2</th>
						<th>핸드폰</th>
						<th>회사명</th>
						<th>집전화</th>
						<th>직통번호</th>
						<th>대표번호</th>
						<th>수정</th>
					</tr>
				</thead>
				<tbody>
				    
				</tbody>
			</table>
		</div>
	</div>
</div>

</body>
</html>