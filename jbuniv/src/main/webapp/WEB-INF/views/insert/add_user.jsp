<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<c:import url="../module/side.jsp"></c:import>
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
	//대분류 단과/대학원등 선택시 카테고리 동적 생성
	function createCate(val){
		var select = '<option>::선택::</option>';
		//단과대
		var sub1 = ['공공인재학부','지미카터국제학부','간호대학','공과대학','농업생명과학대학','사범대학','사회과학대학','상과대학','생물과학대학','수의과대학','예술대학','인문대학','자연과학대학','환경생명자원대학'];
		//일반대학원
		var sub2 = ['인문사회계열','자연과학계열','공학계열','예체능계열', '의학계열','학과간협동과정','학연간협동과정'];
		//특수대학원
		var sub3 = ['경영대학원','교육대학원','생명자원과학대학원','법무대학원','보건대학원','산업기술대학원','정보과학대학원','행정대학원','환경대학원'];
		//전문대학원
		var sub4 = ['의학전문대학원','치의학전문대학원','법학전문대학원','유연인쇄전자전문대학원']
		//mot, 최고위과정
		var sub5 = 'MOT과정';
		var sub6 = '최고위과정';
		
		if(val == '1'){
			$('#relation1').find('option').remove().end().append(select);
			for(var i=0; i<sub1.length; i++){
				$('#relation1').append('<option value="'+sub1[i]+'">'+sub1[i]+'</option>');
			}
		}else if(val == '2'){
			$('#relation1').find('option').remove().end().append(select);
			for(var i=0; i<sub2.length; i++){
				$('#relation1').append('<option value="'+sub2[i]+'">'+sub2[i]+'</option>');
			}
		}else if(val == '3'){
			$('#relation1').find('option').remove().end().append(select);
			for(var i=0; i<sub3.length; i++){
				$('#relation1').append('<option value="'+sub3[i]+'">'+sub3[i]+'</option>');
			}
		}else if(val == '4'){
			$('#relation1').find('option').remove().end().append(select);
			for(var i=0; i<sub4.length; i++){
				$('#relation1').append('<option value="'+sub4[i]+'">'+sub4[i]+'</option>');
			}
		}else if(val == '5'){
			$('#relation1').find('option').remove().end().append(select);
			$('#relation1').append('<option value="'+sub5+'">'+sub5+'</option>');
			
		}else if(val == '6'){
			$('#relation1').find('option').remove().end().append(select);
			$('#relation1').append('<option value="'+sub6+'">'+sub6+'</option>');
			
		}
	}
	
	//관계선택시 학과입력창 가기
	function nextText(){
		$('#relation2').focus();
	}
	
	//폰번호 "-" 넣고빼기
	function focus1(num, type) {
		num = num.replace(/[^0-9]/g, '');
		
		if(type=='1') $("#hp").val(num);
		else if(type=='2') $("#directPhone").val(num);
		else if(type=='3') $("#mainPhone").val(num);
		else if(type=='4') $("#comFax").val(num);
		else if(type=='5') $("#homePhone").val(num);
	}

	function blur1(num, type) {
		if(num != ''){
			num = num.replace(/[^0-9]/g, '');
			var tmp = '';
			
			if(num.length == 10){ //옛휴대폰, 일반번호 10자리
				if(num.substr(0,2) != '02'){
					tmp += num.substr(0, 3);
					tmp += '-';
					tmp += num.substr(3, 3);
					tmp += '-';
					tmp += num.substr(6);
				}else if(num.substr(0,2) == '02'){ //서울일때
					tmp += num.substr(0, 2);
					tmp += '-';
					tmp += num.substr(2, 4);
					tmp += '-';
					tmp += num.substr(6);
				}
				
			}else if(num.length == 11){ //11자리 폰번호				
					tmp += num.substr(0, 3);
					tmp += '-';
					tmp += num.substr(3, 4);
					tmp += '-';
					tmp += num.substr(7);			
			}
			
			if(type == '1') $("#hp").val(tmp);
			else if(type == '2') $("#directPhone").val(tmp);
			else if(type == '3') $("#mainPhone").val(tmp);	
			else if(type == '4') $("#comFax").val(tmp);
			else if(type == '5') $("#homePhone").val(tmp);
		}		
	}
	
	// 우편번호 찾기 - 
	function execDaumPostCode(type) {
		if(type == 1){
			new daum.Postcode({
				oncomplete: function(data) {
				
				var fullAddr = ''; // 최종 주소 변수
				var extraAddr = ''; // 조합형 주소 변수
				
				// 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
				if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
				    fullAddr = data.roadAddress;
				
				} else { // 사용자가 지번 주소를 선택했을 경우(J)
				    fullAddr = data.jibunAddress;
				}
				
				// 사용자가 선택한 주소가 도로명 타입일때 조합한다.
				if(data.userSelectedType === 'R'){
				//법정동명이 있을 경우 추가한다.
				if(data.bname !== ''){
				    extraAddr += data.bname;
				}
				// 건물명이 있을 경우 추가한다.
				if(data.buildingName !== ''){
				    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
				}
				
				// 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
				fullAddr += (extraAddr !== '' ? ' ('+ extraAddr +')' : '');
				}
				var zipCode = data.zonecode;//우편번호
				// 우편번호와 주소 정보를 해당 필드에 넣는다.
				$('#homeAddress').val(fullAddr);
				$('#zipCode').val(zipCode);
				// 커서를 상세주소 필드로 이동한다.
				$('#homeSangseAdd').focus();
				}
			}).open();
		}else if(type==2){
			new daum.Postcode({
				oncomplete: function(data) {
				
				var fullAddr = ''; // 최종 주소 변수
				var extraAddr = ''; // 조합형 주소 변수
				
				// 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
				if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
				    fullAddr = data.roadAddress;
				
				} else { // 사용자가 지번 주소를 선택했을 경우(J)
				    fullAddr = data.jibunAddress;
				}
				
				// 사용자가 선택한 주소가 도로명 타입일때 조합한다.
				if(data.userSelectedType === 'R'){
				//법정동명이 있을 경우 추가한다.
				if(data.bname !== ''){
				    extraAddr += data.bname;
				}
				// 건물명이 있을 경우 추가한다.
				if(data.buildingName !== ''){
				    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
				}
				
				// 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
				fullAddr += (extraAddr !== '' ? ' ('+ extraAddr +')' : '');
				}
				
				var zipCode = data.zonecode;
				
				// 우편번호와 주소 정보를 해당 필드에 넣는다.
				$('#comAddress').val(fullAddr);
				$('#comZipCode').val(zipCode);
				// 커서를 상세주소 필드로 이동한다.
				$('#comAddress').focus();
				}
			}).open();
		}
		
	}
	
	//기본유효성검사
	function formSubmit(){
		var name = $('#name').val();
		var relation1 = $('#relation1').val();
		var relation2 = $('#relation2').val();
		
		if(name == '' || name == null){
			alert('이름을 입력해주세요');
			$('#name').focus();
			return;
		}else if(relation1 == '0'){
			alert('학부를 선택해주세요');
			$('#relationType').focus();
			return;
		}else if(relation2 == '' || relation2 == null){
			alert('학과를 입력해주세요');
			$('#relation2').focus();
			return;
		}
		$('#addUserForm').submit();
	}
</script>
</head>
<body>
<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
            <h3 class="page-header">등록</h3>
        </div>
	</div>
	
	<div class="row">
		<form id="addUserForm" method="post" action="addUserPost">
			<table>
				<tr>
					<th>이름</th>
					<td>
						<input type="text" name="name" id="name" class="form-control"/>
					</td>
					<th>성별</th>
					<td>
						<input type="radio" name="gender">남
						<input type="radio" name="gender">여
					</td>
					<th>휴대폰</th>
					<td><input type="tel" name="hp" id="hp" class="form-control" onfocus="focus1(this.value,1);" onblur="blur1(this.value,1);" maxlength="13"/></td>
					
				</tr>
				<tr>
					<th>집전화</th>
					<td><input type="tel" name="homePhone" id="homePhone" class="form-control" onfocus="focus1(this.value,5);" onblur="blur1(this.value,5);" maxlength="13"/></td>
					<th>이메일</th>
					<td><input type="text" name="email" class="form-control"/></td>
					<th>주민번호</th>
					<td><input type="text" name="ssn" class="form-control"/></td>
				</tr>
				<tr>
					<th>생일</th>
					<td><input type="text" name="birth" class="form-control"/></td>
					<th>양력/음력</th>
					<td>
						<input type="radio" name="lunar" value="+"/>양력
						<input type="radio" name="lunar" value="-"/>음력
					</td>
					<th>결혼기념일</th>
					<td><input type="date" name="wedding" class="form-control"/></td>
				</tr>
				<tr>
					<th style="border-bottom:1px solid blue;">주소(집)</th>
					<td colspan="3" style="border-bottom:1px solid blue;">
						<button type="button" onclick="execDaumPostCode(1);">주소찾기</button>
						<input type="text" name="homeAddress" id="homeAddress" class="form-control"/>
						<input type="text" name="homeSangseAdd" id="homeSangseAdd" class="form-control"/>
					</td>
					<th style="border-bottom:1px solid blue;">우편번호</th>
					<td style="border-bottom:1px solid blue;"><input type="text" name="zipCode" id="zipCode" class="form-control"/></td>
				</tr>
				<tr>
					<td colspan="6" style="color:red;font-weight:bold;font-size:15px;">*대분류(학사,대학원) 을 선택하시면 분류(단과,학부)목록이 자동로딩 됩니다.</td>
				</tr>
				<tr>
					<th>대분류(타입)</th>
					<td>
						<select name="relationType" class="form-control" onchange="createCate(this.value);">
							<option>::선택::</option>
							<option value="1">학사</option>
							<option value="2">일반대학원</option>
							<option value="3">특수대학원</option>
							<option value="4">전문대학원</option>
							<option value="5">MOT과정</option>
							<option value="6">최고위과정</option>
						</select>
					</td>
					<th>분류(관계1)</th>
					<td>
						<select name="relation1" id="relation1" class="form-control" onchange="nextText();">
							<option value="0">::선택::</option>
						</select>
					</td>
					<th>학과(관계2)</th>
					<td>
						<input type="text" name="relation2" id="relation2" class="form-control"/>
					</td>
				</tr>
				<tr>
					<th style="border-bottom:1px solid blue;">학번</th>
					<td style="border-bottom:1px solid blue;"><input type="text" name="studentId" class="form-control"/></td>
					<th style="border-bottom:1px solid blue;">입학일</th>
					<td style="border-bottom:1px solid blue;"><input type="date" name="admission" class="form-control" placeholder="년-월-일"/></td>
					<th style="border-bottom:1px solid blue;">졸업일</th>
					<td style="border-bottom:1px solid blue;"><input type="date" name="graduated" class="form-control" placeholder="년-월-일"/></td>
				</tr>
				<tr>
					<th>회사명</th>
					<td><input type="text" name="company" class="form-control"/></td>
					<th>부서</th>
					<td><input type="text" name="department" class="form-control"/></td>
					<th>직책</th>
					<td><input type="text" name="position" class="form-control"/></td>
				</tr>
				<tr>
					<th>직통번호</th>
					<td><input type="tel" name="directPhone" id="directPhone" class="form-control" onfocus="focus1(this.value,2);" onblur="blur1(this.value,2);" maxlength="13"/></td>
					<th>대표번호</th>
					<td><input type="tel" name="mainPhone" id="mainPhone" class="form-control" onfocus="focus1(this.value,3);" onblur="blur1(this.value,3);" maxlength="13"/></td>
					<th>팩스번호</th>
					<td><input type="tel" name="comFax" id="comFax" class="form-control" onfocus="focus1(this.value,4);" onblur="blur1(this.value,4);" maxlength="13"/></td>				
				</tr>
				<tr>
					<th>I/MYPIN</th>
					<td><input type="text" name="myPin" class="form-control"/></td>
					<th>관리번호</th>
					<td><input type="text" name="controlNumber" class="form-control"/></td>
					<th>내선번호</th>
					<td><input type="tel" name="extension" id="extension" class="form-control"/></td>
				</tr>
				
				<tr>
					<th>주소(회사)</th>
					<td colspan="3">
						<button type="button" onclick="execDaumPostCode(2);">주소찾기</button>
						<input type="text" name="comAddress" id="comAddress" class="form-control"/>
					</td>
					<th>우편번호</th>
					<td><br/><input type="text" name="comZipCode" id="comZipCode" class="form-control"/></td>
				</tr>
				<tr>
					<th>우편발송지</th>
					<td>
						<select name="postAddress" class="form-control" >
							<option value="집">집</option>
							<option value="회사">회사</option>
						</select>
					</td>
					<th>존칭</th>
					<td><input type="text" name="respect" class="form-control" /></td>
					<th>홈페이지</th>
					<td><input type="text" name="homepage" class="form-control"/></td>
				</tr>
				<tr>
					<th>소속팀</th>
					<td><input type="text" name="team" class="form-control"/></td>		
					<th>호응</th>
					<td><input type="text" name="response" class="form-control"/></td>		
					<th>비고</th>
					<td><input type="text" name="etc2" class="form-control"/></td>		
				</tr>
				<tr>
					<th>검색키</th>
					<td colspan="5"><input type="text" name="searchKey" class="form-control"/></td>			
				</tr>
				<tr>
					<th>메모</th>
					<td colspan="5"><textarea name="memo" class="form-control" rows="5"></textarea></td>	
				</tr>
				<tr>
					<td colspan="6" style="text-align:center;font-weight:bold;">
						<button type="button" onclick="formSubmit();">등록</button>
						<input type="reset" value="취소"/>
					</td>
				</tr>
			</table>
		</form>
		<br/><br/><br/><br/><br/>
	</div>
</div>
</body>
</html>