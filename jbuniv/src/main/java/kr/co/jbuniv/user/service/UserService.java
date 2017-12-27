package kr.co.jbuniv.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.jbuniv.user.dao.UserDao;
import kr.co.jbuniv.user.domain.NewUser;
import kr.co.jbuniv.user.domain.TbUser;
import kr.co.jbuniv.user.domain.Users;

@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;
	
	//회원입력
	public int addUserServ(Users user) {
		System.out.println("입력전 값확인 : "+user);
		int result = userDao.insertUser(user);
		return result;
	}
	
	//카테고리별 회원조회
	public List<Users> readUsersByRelTypeServ(int relationType, String relation1, int startNum, String type){
		String[] list1 = {"간호대학","공과대학","농업생명과학대학","사범대학","사회과학대학","상과대학","생활과학대학","수의과대학","예술대학","인문대학","자연과학대학","환경생명자원대학","의과대학","법과대학","치과대학"};
		String list2 = "농업개발대";
		String[] list3 = {"경영대학원","교육대학원","법무대학원","보건대학원","산업기술대학원","생명자원과학대학원","정보과학대학원","행정대학원","환경대학원"};
		String[] list4 = {"법학전문대학원","의학전문대학원","치의학전문대학원"};
		String list5 = "MOT과정";
		String list6 = "최고위과정";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("startNum", startNum);
		params.put("endNum", startNum+100);
		params.put("relationType", relationType);
		
		System.out.println("확인 : "+list1[Integer.parseInt(relation1)]);
		if(relationType == 1) params.put("relation1", list1[Integer.parseInt(relation1)]);
		else if(relationType == 2) params.put("relation1", list2);
		else if(relationType == 3) params.put("relation1", list3[Integer.parseInt(relation1)]);
		else if(relationType == 4) params.put("relation1", list4[Integer.parseInt(relation1)]);
		else if(relationType == 5) params.put("relation1", list5);
		else if(relationType == 6) params.put("relation1", list6);
		
		/*System.out.println("파라미터값 확인 : "+params);*/
		
		if(type.equals("all")) {
			List<Users> userList = userDao.selectUsersByRelTypeAll(params);
			return userList;
		}else {
			List<Users> userList = userDao.selectUsersByRelType(params);
			return userList;
		}
	}
	
	//시퀀스넘버로 하나의 회원조회
	public Users readUserBySeqNo(int seqNo, int type) {
		Users user = new Users();
		if(type == 1)user = userDao.selectUserBySeqNo(seqNo);
		else if(type==2) user = userDao.selectUserStrange(seqNo);
		return user;
	}
	
	//유저정보수정
	public int modifyUserServ(Users user) {
		return userDao.updateUser(user);
	}
	
	//DB카운트 
	public Map<String, Integer> readDbCountServ(){
		int count1 = userDao.selectCountAll1(); //정체DB카운트
		int count2 = userDao.selectCountAll2(); //발전지원부
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		map.put("count1", count1);
		map.put("count2", count2);
		
		return map;
	}
	
	//DB정제 캡쳐
	public void captureServ() {
		System.out.println("==================================================");
		System.out.println("정제결과   		              						  ");
		System.out.println("");
		System.out.println("총카운트 : 17,809  건");
		System.out.println("정제성공 : 13,513  건");
		System.out.println("검색실패 : 2,319  건");
		System.out.println("이상데이터(중복)( 1,094 건) ");
		System.out.println("필수 확인요망(졸업일,학과 다름) 이상데이터( 3,386건)");
		System.out.println("==================================================");
	}
	
	//DB정제 재경인명록 <> 어깨동무
	public Map<String, Object> dbRefineThirdServ(int startNum, int endNum){
		List<TbUser> failList = new ArrayList<TbUser>();
		// 1. 발전지원부 선택된 범위로 조회 
		Map<String, Integer> params = new HashMap<String, Integer>();
		if(endNum == 0) endNum = startNum + 1000; //1000명씩 조회 선택시, 범위 끝 시작숫자+1000으로 세팅.
		params.put("startNum", startNum);
		params.put("endNum", endNum);
		
		List<TbUser> list = userDao.selectDataSelectedInSeoul(params);
		System.out.println("최초조회된 값 카운트 : "+list.size());
		/*1의 검색결과로 이름, 단대 조건으로 중복자료 반복문으로 조회한 후 조회된 데이터가 있으면 세부 비교 하여 동일인인지 파악하고,
		동일인이면 각 필드값 확인하여 어깨동무쪽이 null이면 발전지원부 데이터 필드 덮어쓰고 , 
		전화번호는 010인쪽 번호 씌우고 기존 번호는 기타2 필드로 빽업,
		동일인이 없다면 신규 인서트.*/
		 
		Users user = null;
		String admission = null; //입학일의 년도만 추출해 할당할 변수
		int sameCount = 0; // 데이터 비교시 같은부분 체크하고 같은데이터 수 카운트할 변수
		int insertCount = 0;
		int updateCount = 0;
		int noUpdateCount = 0;
		List<Integer> noUpList = new ArrayList<Integer>();
		int checkStrange = 0;
		List<Integer> stList = new ArrayList<Integer>(); //중복데이터 의심 목록
		List<Integer> stList2 = new ArrayList<Integer>(); //동일인인지 확인해야할 목록
		String graduated1= null;
		String graduated2 = null;
		int checkStrange2 = 0; //동일인인지 의심을 위한 체크카운트(졸업일,학과 다르고 타 정보 일치항목있을시)
		int notSameCount = 0; // 동명, 입학일 일치 데이터중 졸업일, 학과 가 다를때 
		int nullCount = 0;
		int subCheckCount = 0;
		List<Users> waitList = new ArrayList<Users>();
		int upDateResult = 0;
		
		for(int i=0; i<list.size(); i++) {
			System.out.println(i+" 번째 처리중 ..");
			/*System.out.println(i+" 번째 데이터 : "+list.get(i));*/
			
			// 입학일 년도만 세팅
			if(list.get(i).getAdmission() != null && list.get(i).getAdmission() != "") { //입학일이 있을때
				admission = list.get(i).getAdmission();
				
				if(admission.length() > 4) { //입학일 년월일이 다 입력되있을때
					admission = admission.substring(admission.length()-4, admission.length()); // 날짜형식이 dd/mm/yyyy 임
				}else if(admission.length() == 2) {
					if(Integer.parseInt(admission.substring(0,1)) > 4) admission = "19"+admission;
					else if(Integer.parseInt(admission.substring(0,1)) < 4) admission = "20"+admission;
				}
			}
			
			if(list.get(i).getName() != null) {
				String name = list.get(i).getName().replaceAll("\\(", "");
				name = name.replaceAll("\\)", "");
				list.get(i).setName(name);
			}
			// 어깨동무 데이터에서 이름, 단과, 입학일(있을때) 조건으로 중복검색
			List<Users> searchList = userDao.selectUserByNameNRel(list.get(i));
			System.out.println("정제될 데이터 조회 갯수 확인 : "+searchList.size());
			
			if(searchList.size() > 1) { // 1. 2개이상의 자료검색될 시 추가 비교하여 동일인여부 확인
				for(int j=0; j<searchList.size(); j++) {
					System.out.println(searchList.get(j).getName()+"님  세부비교 : "+(j+1)+" 번째 비교중..");
					//추가 필드 비교	후 동일인이면 업데이트 하고 아니면 그냥 인서트	
					//휴대폰 뒷4자리 비교
					if(list.get(i).getHp() != null && searchList.get(j).getHp() != null) {
						if(list.get(i).getHp().length() > 4 && searchList.get(j).getHp().length() > 4) {
							if(list.get(i).getHp().substring(list.get(i).getHp().length()-4, list.get(i).getHp().length()).equals(searchList.get(j).getHp().substring(searchList.get(j).getHp().length()-4, searchList.get(j).getHp().length()))) {
								System.out.println("핸드폰 데이터 일치!!");
								sameCount += 1;
							}else {
								subCheckCount +=1;
							}
						}
					}
					
					//집전화 비교
					if(list.get(i).getHomePhone() != null && searchList.get(j).getHomePhone() != null) {
						if(list.get(i).getHomePhone().length() > 4 && searchList.get(j).getHomePhone().length() > 4) {
							if(list.get(i).getHomePhone().substring(list.get(i).getHomePhone().length()-4, list.get(i).getHomePhone().length()).equals(searchList.get(j).getHomePhone().substring(searchList.get(j).getHomePhone().length()-4, searchList.get(j).getHomePhone().length()))) {
								System.out.println("집전화 데이터 일치!!");
								sameCount += 1;
							}else {
								subCheckCount +=1;
							}
						}
					}
					
					//대표전화 비교
					if(list.get(i).getMainPhone() != null && searchList.get(j).getMainPhone() != null) {
						if(list.get(i).getMainPhone().length() > 4 && searchList.get(j).getMainPhone().length() > 4) {
							if(list.get(i).getMainPhone().substring(list.get(i).getMainPhone().length()-4, list.get(i).getMainPhone().length()).equals(searchList.get(j).getMainPhone().substring(searchList.get(j).getMainPhone().length()-4, searchList.get(j).getMainPhone().length()))) {
								System.out.println("대표전화 데이터 일치!!");
								sameCount += 1;
							}else {
								subCheckCount +=1;
							}
						}
					}
					
					/*// 집주소 비교- 발전지원부는 집주소가 없슴.
					if(list.get(i).getHomeAddress() != null && searchList.get(j).getHomeAddress() != null) {
						if(list.get(i).getHomeAddress().trim().equals(searchList.get(j).getHomeAddress().trim())) {
							System.out.println("집주소 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}*/
					
					//이메일 비교
					if(list.get(i).getEmail() != null && searchList.get(j).getEmail() !=null){
						if(list.get(i).getEmail().equals(searchList.get(j).getEmail())) {
							System.out.println("이메일 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}
					/*//회사명
					if(list.get(i).getCompany() != null && searchList.get(j).getCompany() != null) {
						if(list.get(i).getCompany().equals(searchList.get(j).getCompany())) {
							System.out.println("직장 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}*/
					// 학과명
					if(list.get(i).getRelation2() != null && searchList.get(j).getRelation2() != null) {
						if(searchList.get(j).getRelation2().contains(list.get(i).getRelation2().substring(0, 2))) {
							System.out.println("학과 일치!!");
							sameCount += 1;
						}else {
							System.out.println("학과 불일치!!");
							notSameCount +=1;
						}
					}else {
						sameCount += 1;
						nullCount += 1;
					}
					
					//졸업일
					if(list.get(i).getGraduated() != null && searchList.get(j).getGraduated() != null) {
						if(list.get(i).getGraduated().length() == 4) graduated1 = list.get(i).getGraduated();
						else if(list.get(i).getGraduated().length() > 4) graduated1 = list.get(i).getGraduated().substring(list.get(i).getGraduated().length()-4, list.get(i).getGraduated().length());
						else if(list.get(i).getGraduated().length() == 2) {
							graduated1 = list.get(i).getGraduated();
							graduated1 = graduated1.substring(0, 1);
							if(Integer.parseInt(graduated1) < 5) graduated1 = "20"+graduated1;
							else if(Integer.parseInt(graduated1) > 4) graduated1 = "19"+graduated1;
						}
						
						if(searchList.get(j).getGraduated().length() == 4) graduated2 = searchList.get(j).getGraduated();
						else if(searchList.get(j).getGraduated().length() > 4) graduated2 = searchList.get(j).getGraduated().substring(searchList.get(j).getGraduated().length()-4, searchList.get(j).getGraduated().length());
						else if(searchList.get(j).getGraduated().length() == 2) {
							graduated2 = searchList.get(j).getGraduated();
							graduated2 = graduated2.substring(0,1);
							if(Integer.parseInt(graduated2) < 5) graduated2 = "20"+graduated2;
							else if(Integer.parseInt(graduated2) > 4) graduated2 = "19"+graduated2;
						}
						if(graduated1.equals(graduated2)) {
							System.out.println("졸업일 일치!!");
							sameCount += 1;
						}else {
							System.out.println("졸업일 불일치!!");
							notSameCount +=1;
						}
					}else {
						sameCount += 1;
						nullCount += 1;
					}
					
					// sameCount 1 혹은 2이상 이면 일치 // 0이면 불일치 , ?반복문 안에서 개수 이상이면 처리할것인가 기록했다가 반복문 종료후 가장 많은 일치항목 보유 데이터를 할것인가 ??
					if(notSameCount == 0 && nullCount == 0) { //학과,졸업일 일치
						if(sameCount > 2) { //추가 데이터 일치항목이 있을때 (동일확률100%)
							System.out.println(j+" 번째 조회 데이터 추가 필드 "+sameCount+" 개 일치!! 동일인일 확률 100%..");
							System.out.println("");
							
							if(searchList.get(j).getEtc2() != null && searchList.get(j).getEtc2() != "") {
								if(searchList.get(j).getEtc2().equals("통화")) {
									System.out.println("최근 통화된 자료라 수정안함.");
									upDateResult=1;
								}else {
									upDateResult = userDao.updateUser(setParamForUpdate3(searchList.get(j), list.get(i)));
								}	
							}else {
								upDateResult = userDao.updateUser(setParamForUpdate3(searchList.get(j), list.get(i)));
							}						
							
							if(upDateResult > 0) {
								updateCount++;
								checkStrange++;
							}
							
						}else if(sameCount == 2) { //학과,졸업일만 같고 나머지 일치 항목이 없을때 .. 전 필드값이 null일수도 있으므로 동일인확률 50% . 확인요망
							System.out.println(j+" 번째 조회 데이터 가변필드 "+(sameCount-2)+" 개 일치! 학과,졸업일만 일치!! 동일인일 확률 50% 확인요망");
							System.out.println("");	
							if(subCheckCount == 0) {
								checkStrange2++;
								System.out.println(j+"번째 데이터 대기목록에 추가~!1!");
								waitList.add(searchList.get(j));
							}							
						}
					}else if(notSameCount ==0 && nullCount > 0){ // 학과,졸업일 불일치는 아니나 널값인 필드가 있을때
						if(sameCount > nullCount && sameCount > 2) { //null값을 제외한 추가(가변)필드 일치 항목있을때 - 동일인 확률 100%
							System.out.println(j+" 번째 조회 데이터 추가 필드 "+(sameCount-2)+" 개 일치!! 동일인일 확률 100%..");
							System.out.println("");
							
							if(searchList.get(j).getEtc2() != null && searchList.get(j).getEtc2() != "") {
								if(searchList.get(j).getEtc2().equals("통화")) {
									System.out.println("최근 통화된 자료라 수정안함.");
									upDateResult=1;
								}else {
									upDateResult = userDao.updateUser(setParamForUpdate3(searchList.get(j), list.get(i)));
								}	
							}else {
								upDateResult = userDao.updateUser(setParamForUpdate3(searchList.get(j), list.get(i)));
							}		
							
							
							if(upDateResult > 0) {
								updateCount++;
								checkStrange++;
							}							
						}else if(sameCount == 2 && nullCount >= 1){ //가변항목 일치 없고 학과,졸업일 1항목 널 1항목 일치시 - 동일인일 확률 50%
							System.out.println(j+" 번째 조회 데이터 가변필드 "+(sameCount-2)+" 개 일치!!  학과 졸업일 null 혹은 일치!! 동일인일 확률 50% 확인요망");
							System.out.println("");	
							if(subCheckCount == 0) {
								checkStrange2++;
								System.out.println(j+"번째 데이터 대기목록에 추가~!2!");
								System.out.println("");
								waitList.add(searchList.get(j));
							}		
						}
					}else if(notSameCount > 0 && nullCount >= 0) { //학과,졸업일 2항목(혹은1) 불일치 상황
						if(sameCount > 1 && nullCount != 0) { //가변필드 일치항목이 있을경우- 확인요망 확률 50%
							System.out.println(j+" 번째 조회 데이터 학과,졸업일 불일치! 가변 필드 "+(sameCount-1)+" 개 일치!! 동일인일 확률 30% 확인요망");
							System.out.println("");	
							checkStrange2++;
						}else { //  확률0%
							System.out.println(j+" 번째 추가 필드 일치항목이 없습니다. 동일인일 확률 0%");
							System.out.println("");
						}
					}
					sameCount = 0;
					nullCount = 0;
					notSameCount = 0;
					subCheckCount = 0;										
				} // j인자 for문끝
				//2이상 일치자 확인시 목록 담기(중복일 확률이 높음)
				if(checkStrange > 1) stList.add(list.get(i).getSeqNo()); //2회이상수정 (100%중복)
				/*else if(checkStrange == 0 && waitList.size() == 0) stList2.add(list.get(i).getSeqNo()); */
				
				
				//다른 필드가 일치하나 학과 혹은 졸업일 혹은 둘다 다를때 확인요망 리스트
				if(waitList.size() == 1 && checkStrange ==0) { //로직실행후 졸업,학과 일치자가 1개일때 동일인으로 인정. 업데이트(수정된 데이터 없을때)
					System.out.println("대기 row 1개~!! 업데이트 처리!!");
					System.out.println("");
					if(waitList.get(0).getEtc2() != null && waitList.get(0).getEtc2() != "") {
						if(waitList.get(0).getEtc2().equals("통화")) {
							System.out.println("최근 통화된 자료라 수정안함.");
							upDateResult=1;
						}else {
							upDateResult = userDao.updateUser(setParamForUpdate3(waitList.get(0), list.get(i)));
						}
					}else {
						upDateResult = userDao.updateUser(setParamForUpdate3(waitList.get(0), list.get(i)));
					}
					
					if(upDateResult > 0) {
						updateCount++;
					}	
				}else if(checkStrange2 > 0 && checkStrange == 0){ // 수정된 데이터가 없고 확인목록이 있을때
					System.out.println("수정된 내용이 없고 확인목록만 있어 확인목록에 추가합니다.");
					System.out.println("");
					stList2.add(list.get(i).getSeqNo());					
				}else if(checkStrange == 0 && checkStrange2 == 0) { //검색데이터는 있으나 수정도 안되었고 확인목록도 없을때,, 신규인서트
					System.out.println("조회목록은 있으나 일치데이터 및 확인요망 데이터가 없습니다.");
					System.out.println("신규인서트 처리.");
					System.out.println("시퀀스 넘버 : "+list.get(i).getSeqNo());
					System.out.println("");
					noUpList.add(list.get(i).getSeqNo());
					failList.add(list.get(i));
					int result = userDao.insertUser(setUsers(list.get(i)));
					noUpdateCount++;
				}
				waitList = new ArrayList<Users>();
				checkStrange = 0;
				checkStrange2 = 0;
				upDateResult = 0;
				
			}else if(searchList.size() == 0) { //2. 조회된 결과가 없을때 -> 데이터 인서트처리
				System.out.println("일치하는 데이터가 없어 신규 인서트 처리 ");
				System.out.println("시퀀스 넘버 : "+list.get(i).getSeqNo());
				System.out.println("");
				noUpList.add(list.get(i).getSeqNo());
				failList.add(list.get(i));
				int result = userDao.insertUser(setUsers(list.get(i)));
				noUpdateCount++;
				
			}else if(searchList.size() == 1) { //3. 한건만 조회 됐을때 -> 기존데이터와 비교하여 빈곳 업데이트 처리
				System.out.println("한건만 조회되어 업데이트 처리 ");
				System.out.println("");
				if(searchList.get(0).getEtc2() != null && searchList.get(0).getEtc2() != "") {
					if(searchList.get(0).getEtc2().equals("통화")) {
						System.out.println("최근통화된 자료라 수정안함.");
						upDateResult=1;
					}else {
						upDateResult = userDao.updateUser(setParamForUpdate3(searchList.get(0), list.get(i)));
					}
				}else {
					upDateResult = userDao.updateUser(setParamForUpdate3(searchList.get(0), list.get(i)));
				}				
				if(upDateResult > 0) updateCount++;
			} 
		}
		System.out.println("==================================================");
		System.out.println("					 정제결과   					  ");
		System.out.println("총카운트 : "+list.size()+"  건");
		System.out.println("정제성공 : "+updateCount+"  건");
		System.out.println("신규등록 : "+noUpdateCount+"  건");
		System.out.println("신규목록 : "+noUpList);
		System.out.println("이상데이터(중복)("+stList.size()+"건)목록 : "+stList);
		System.out.println("필수 확인요망(졸업일,학과 다름) 이상데이터("+stList2.size()+"건)목록 : "+stList2);
		System.out.println("==================================================");
		
		/*captureServ();*/
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("updateCount", updateCount);
		map.put("noUpdateCount", noUpdateCount);
		map.put("failList", failList);
		
		return map;
	}
	
	//DB정제 발전지원부 <> 어깨동무
	public Map<String, Object> dbRefineSecondServ(int startNum, int endNum){
		List<TbUser> failList = new ArrayList<TbUser>();
		// 1. 발전지원부 선택된 범위로 조회 
		Map<String, Integer> params = new HashMap<String, Integer>();
		if(endNum == 0) endNum = startNum + 1000; //1000명씩 조회 선택시, 범위 끝 시작숫자+1000으로 세팅.
		params.put("startNum", startNum);
		params.put("endNum", endNum);
		
		List<TbUser> list = userDao.selectDataSelectedNewUser(params);
		System.out.println("최초조회된 값 카운트 : "+list.size());
		/*1의 검색결과로 이름, 단대 조건으로 중복자료 반복문으로 조회한 후 조회된 데이터가 있으면 세부 비교 하여 동일인인지 파악하고,
		동일인이면 각 필드값 확인하여 어깨동무쪽이 null이면 발전지원부 데이터 필드 덮어쓰고 , 
		전화번호는 010인쪽 번호 씌우고 기존 번호는 기타2 필드로 빽업,
		동일인이 없다면 신규 인서트.*/
		 
		Users user = null;
		String admission = null; //입학일의 년도만 추출해 할당할 변수
		int sameCount = 0; // 데이터 비교시 같은부분 체크하고 같은데이터 수 카운트할 변수
		int insertCount = 0;
		int updateCount = 0;
		int noUpdateCount = 0;
		List<Integer> noUpList = new ArrayList<Integer>();
		int checkStrange = 0;
		List<Integer> stList = new ArrayList<Integer>(); //중복데이터 의심 목록
		List<Integer> stList2 = new ArrayList<Integer>(); //동일인인지 확인해야할 목록
		String graduated1= null;
		String graduated2 = null;
		int checkStrange2 = 0; //동일인인지 의심을 위한 체크카운트(졸업일,학과 다르고 타 정보 일치항목있을시)
		int notSameCount = 0; // 동명, 입학일 일치 데이터중 졸업일, 학과 가 다를때 
		int nullCount = 0;
		int subCheckCount = 0;
		List<Users> waitList = new ArrayList<Users>();
		int upDateResult = 0;
		
		for(int i=0; i<list.size(); i++) {
			System.out.println(i+" 번째 처리중 ..");
			/*System.out.println(i+" 번째 데이터 : "+list.get(i));*/
			
			// 입학일 년도만 세팅
			if(list.get(i).getAdmission() != null && list.get(i).getAdmission() != "") { //입학일이 있을때
				admission = list.get(i).getAdmission();
				
				if(admission.length() > 4) { //입학일 년월일이 다 입력되있을때
					admission = admission.substring(admission.length()-4, admission.length()); // 날짜형식이 dd/mm/yyyy 임
				}
			}
			
			// 어깨동무 데이터에서 이름, 단과, 입학일(있을때) 조건으로 중복검색
			List<Users> searchList = userDao.selectUserByNameNRel(list.get(i));
			System.out.println("정제될 데이터 조회 갯수 확인 : "+searchList.size());
			
			if(searchList.size() > 1) { // 1. 2개이상의 자료검색될 시 추가 비교하여 동일인여부 확인
				for(int j=0; j<searchList.size(); j++) {
					System.out.println(searchList.get(j).getName()+"님  세부비교 : "+(j+1)+" 번째 비교중..");
					//추가 필드 비교	후 동일인이면 업데이트 하고 아니면 그냥 인서트	
					//휴대폰 뒷4자리 비교
					if(list.get(i).getHp() != null && searchList.get(j).getHp() != null) {
						if(list.get(i).getHp().length() > 4 && searchList.get(j).getHp().length() > 4) {
							if(list.get(i).getHp().substring(list.get(i).getHp().length()-4, list.get(i).getHp().length()).equals(searchList.get(j).getHp().substring(searchList.get(j).getHp().length()-4, searchList.get(j).getHp().length()))) {
								System.out.println("핸드폰 데이터 일치!!");
								sameCount += 1;
							}else {
								subCheckCount +=1;
							}
						}
					}
					
					//집전화 비교
					if(list.get(i).getMainPhone() != null && searchList.get(j).getHomePhone() != null) {
						if(list.get(i).getMainPhone().length() > 4 && searchList.get(j).getHomePhone().length() > 4) {
							if(list.get(i).getMainPhone().substring(list.get(i).getMainPhone().length()-4, list.get(i).getMainPhone().length()).equals(searchList.get(j).getHomePhone().substring(searchList.get(j).getHomePhone().length()-4, searchList.get(j).getHomePhone().length()))) {
								System.out.println("집전화 데이터 일치!!");
								sameCount += 1;
							}else {
								subCheckCount +=1;
							}
						}
					}
					
					//대표전화 비교
					if(list.get(i).getMainPhone() != null && searchList.get(j).getMainPhone() != null) {
						if(list.get(i).getMainPhone().length() > 4 && searchList.get(j).getMainPhone().length() > 4) {
							if(list.get(i).getMainPhone().substring(list.get(i).getMainPhone().length()-4, list.get(i).getMainPhone().length()).equals(searchList.get(j).getMainPhone().substring(searchList.get(j).getMainPhone().length()-4, searchList.get(j).getMainPhone().length()))) {
								System.out.println("대표전화 데이터 일치!!");
								sameCount += 1;
							}else {
								subCheckCount +=1;
							}
						}
					}
					
					/*// 집주소 비교- 발전지원부는 집주소가 없슴.
					if(list.get(i).getHomeAddress() != null && searchList.get(j).getHomeAddress() != null) {
						if(list.get(i).getHomeAddress().trim().equals(searchList.get(j).getHomeAddress().trim())) {
							System.out.println("집주소 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}*/
					
					//이메일 비교
					if(list.get(i).getEmail() != null && searchList.get(j).getEmail() !=null){
						if(list.get(i).getEmail().equals(searchList.get(j).getEmail())) {
							System.out.println("이메일 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}
					//회사명
					if(list.get(i).getCompany() != null && searchList.get(j).getCompany() != null) {
						if(list.get(i).getCompany().equals(searchList.get(j).getCompany())) {
							System.out.println("직장 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}
					// 학과명
					if(list.get(i).getRelation2() != null && searchList.get(j).getRelation2() != null) {
						if(searchList.get(j).getRelation2().contains(list.get(i).getRelation2().substring(0, 2))) {
							System.out.println("학과 일치!!");
							sameCount += 1;
						}else {
							System.out.println("학과 불일치!!");
							notSameCount +=1;
						}
					}else {
						sameCount += 1;
						nullCount += 1;
					}
					
					//졸업일
					if(list.get(i).getGraduated() != null && searchList.get(j).getGraduated() != null) {
						if(list.get(i).getGraduated().length() == 4) graduated1 = list.get(i).getGraduated();
						else if(list.get(i).getGraduated().length() > 4) graduated1 = list.get(i).getGraduated().substring(list.get(i).getGraduated().length()-4, list.get(i).getGraduated().length());
						else if(list.get(i).getGraduated().length() == 2) {
							graduated1 = list.get(i).getGraduated();
							graduated1 = graduated1.substring(0, 1);
							if(Integer.parseInt(graduated1) < 5) graduated1 = "20"+graduated1;
							else if(Integer.parseInt(graduated1) > 4) graduated1 = "19"+graduated1;
						}
						
						if(searchList.get(j).getGraduated().length() == 4) graduated2 = searchList.get(j).getGraduated();
						else if(searchList.get(j).getGraduated().length() > 4) graduated2 = searchList.get(j).getGraduated().substring(searchList.get(j).getGraduated().length()-4, searchList.get(j).getGraduated().length());
						else if(searchList.get(j).getGraduated().length() == 2) {
							graduated2 = searchList.get(j).getGraduated();
							graduated2 = graduated2.substring(0,1);
							if(Integer.parseInt(graduated2) < 5) graduated2 = "20"+graduated2;
							else if(Integer.parseInt(graduated2) > 4) graduated2 = "19"+graduated2;
						}
						if(graduated1.equals(graduated2)) {
							System.out.println("졸업일 일치!!");
							sameCount += 1;
						}else {
							System.out.println("졸업일 불일치!!");
							notSameCount +=1;
						}
					}else {
						sameCount += 1;
						nullCount += 1;
					}
					
					// sameCount 1 혹은 2이상 이면 일치 // 0이면 불일치 , ?반복문 안에서 개수 이상이면 처리할것인가 기록했다가 반복문 종료후 가장 많은 일치항목 보유 데이터를 할것인가 ??
					if(notSameCount == 0 && nullCount == 0) { //학과,졸업일 일치
						if(sameCount > 2) { //추가 데이터 일치항목이 있을때 (동일확률100%)
							System.out.println(j+" 번째 조회 데이터 추가 필드 "+sameCount+" 개 일치!! 동일인일 확률 100%..");
							System.out.println("");
							
							if(searchList.get(j).getEtc2() != null && searchList.get(j).getEtc2() != "") {
								if(searchList.get(j).getEtc2().equals("통화")) {
									/*upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));*/
									System.out.println("최근 통화된 자료라 수정안함.");
									upDateResult = 1;
								}else {
									upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));
								}	
							}else {
								upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));
							}						
							
							if(upDateResult > 0) {
								updateCount++;
								checkStrange++;
							}
							
						}else if(sameCount == 2) { //학과,졸업일만 같고 나머지 일치 항목이 없을때 .. 전 필드값이 null일수도 있으므로 동일인확률 50% . 확인요망
							System.out.println(j+" 번째 조회 데이터 가변필드 "+(sameCount-2)+" 개 일치! 학과,졸업일만 일치!! 동일인일 확률 50% 확인요망");
							System.out.println("");	
							if(subCheckCount == 0) {
								checkStrange2++;
								System.out.println(j+"번째 데이터 대기목록에 추가~!1!");
								waitList.add(searchList.get(j));
							}							
						}
					}else if(notSameCount ==0 && nullCount > 0){ // 학과,졸업일 불일치는 아니나 널값인 필드가 있을때
						if(sameCount > nullCount && sameCount > 2) { //null값을 제외한 추가(가변)필드 일치 항목있을때 - 동일인 확률 100%
							System.out.println(j+" 번째 조회 데이터 추가 필드 "+(sameCount-2)+" 개 일치!! 동일인일 확률 100%..");
							System.out.println("");
							
							if(searchList.get(j).getEtc2() != null && searchList.get(j).getEtc2() != "") {
								if(searchList.get(j).getEtc2().equals("통화")) {
									/*upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));*/
									System.out.println("최근 통화된 자료라 수정안함.");
								}else {
									upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));
								}	
							}else {
								upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));
							}		
							
							
							if(upDateResult > 0) {
								updateCount++;
								checkStrange++;
							}							
						}else if(sameCount == 2 && nullCount >= 1){ //가변항목 일치 없고 학과,졸업일 1항목 널 1항목 일치시 - 동일인일 확률 50%
							System.out.println(j+" 번째 조회 데이터 가변필드 "+(sameCount-2)+" 개 일치!!  학과 졸업일 null 혹은 일치!! 동일인일 확률 50% 확인요망");
							System.out.println("");	
							if(subCheckCount == 0) {
								checkStrange2++;
								System.out.println(j+"번째 데이터 대기목록에 추가~!2!");
								waitList.add(searchList.get(j));
							}		
						}
					}else if(notSameCount > 0 && nullCount >= 0) { //학과,졸업일 2항목(혹은1) 불일치 상황
						if(sameCount > 1 && nullCount != 0) { //가변필드 일치항목이 있을경우- 확인요망 확률 50%
							System.out.println(j+" 번째 조회 데이터 학과,졸업일 불일치! 가변 필드 "+(sameCount-1)+" 개 일치!! 동일인일 확률 30% 확인요망");
							System.out.println("");	
							checkStrange2++;
						}else { //  확률0%
							System.out.println(j+" 번째 추가 필드 일치항목이 없습니다. 동일인일 확률 0%");
							System.out.println("");
						}
					}
					sameCount = 0;
					nullCount = 0;
					notSameCount = 0;
					subCheckCount = 0;										
				} // j인자 for문끝
				//2이상 일치자 확인시 목록 담기(중복일 확률이 높음)
				if(checkStrange > 1) stList.add(list.get(i).getSeqNo()); //2회이상수정 (100%중복)
				else if(checkStrange == 0 && waitList.size() == 0) stList2.add(list.get(i).getSeqNo()); // 
				
				
				//다른 필드가 일치하나 학과 혹은 졸업일 혹은 둘다 다를때 확인요망 리스트
				if(waitList.size() == 1 && checkStrange ==0) { //로직실행후 졸업,학과 일치자가 1개일때 동일인으로 인정. 업데이트
					System.out.println("대기 row 1개~!! 업데이트 처리!!");
					upDateResult = userDao.updateUser(setParamForUpdate2(waitList.get(0), list.get(i)));
					if(upDateResult > 0) {
						updateCount++;
					}	
				}else if(checkStrange2 > 0 && checkStrange == 0){ // 업데이트 대기 리스트가 없거나 2개이상일때 확인리스트에 추가하여 확인하기
					stList2.add(list.get(i).getSeqNo());					
				}	
				waitList = new ArrayList<Users>();
				checkStrange = 0;
				checkStrange2 = 0;
				upDateResult = 0;
				
			}else if(searchList.size() == 0) { //2. 조회된 결과가 없을때 -> 데이터 인서트처리
				System.out.println("일치하는 데이터가 없어 신규 인서트 처리 ");
				System.out.println("시퀀스 넘버 : "+list.get(i).getSeqNo());
				System.out.println("");
				noUpList.add(list.get(i).getSeqNo());
				failList.add(list.get(i));
				int result = userDao.insertUser(setUsers(list.get(i)));
				noUpdateCount++;
				
			}else if(searchList.size() == 1) { //3. 한건만 조회 됐을때 -> 기존데이터와 비교하여 빈곳 업데이트 처리
				System.out.println("한건만 조회되어 업데이트 처리 ");
				System.out.println("");
				if(searchList.get(0).getEtc2() != null && searchList.get(0).getEtc2() != "") {
					if(searchList.get(0).getEtc2().equals("통화")) {
						/*upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));*/
						System.out.println("최근 통화된 자료라 수정안함.");
						upDateResult++;
					}else {
						upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(0), list.get(i)));
					}	
				}else {
					upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(0), list.get(i)));
				}
				
				if(upDateResult > 0) updateCount++;
			} 
		}
		System.out.println("==================================================");
		System.out.println("					 정제결과   					  ");
		System.out.println("총카운트 : "+list.size()+"  건");
		System.out.println("정제성공 : "+updateCount+"  건");
		System.out.println("정제실패 : "+noUpdateCount+"  건");
		System.out.println("실패목록 : "+noUpList);
		System.out.println("이상데이터(중복)("+stList.size()+"건)목록 : "+stList);
		System.out.println("필수 확인요망(졸업일,학과 다름) 이상데이터("+stList2.size()+"건)목록 : "+stList2);
		System.out.println("==================================================");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("updateCount", updateCount);
		map.put("noUpdateCount", noUpdateCount);
		map.put("failList", failList);
		
		return map;
	}
		
	//DB정제 어깨동무 원본 <> 어깨동무 수정본
	public Map<String, Object> dbRefineServ(int startNum, int endNum){
		List<TbUser> failList = new ArrayList<TbUser>();
		// 1. 발전지원부 선택된 범위로 조회 
		Map<String, Integer> params = new HashMap<String, Integer>();
		if(endNum == 0) endNum = startNum + 1000; //1000명씩 조회 선택시, 범위 끝 시작숫자+1000으로 세팅.
		params.put("startNum", startNum);
		params.put("endNum", endNum);
		
		List<TbUser> list = userDao.selectDataSelected(params);
		System.out.println("최초조회된 값 카운트 : "+list.size());
		/*1의 검색결과로 이름, 단대 조건으로 중복자료 반복문으로 조회한 후 조회된 데이터가 있으면 세부 비교 하여 동일인인지 파악하고,
		동일인이면 각 필드값 확인하여 어깨동무쪽이 null이면 발전지원부 데이터 필드 덮어쓰고 , 
		전화번호는 010인쪽 번호 씌우고 기존 번호는 기타2 필드로 빽업,
		동일인이 없다면 신규 인서트.*/
		 
		Users user = null;
		String admission = null; //입학일의 년도만 추출해 할당할 변수
		int sameCount = 0; // 데이터 비교시 같은부분 체크하고 같은데이터 수 카운트할 변수
		int insertCount = 0;
		int updateCount = 0;
		int noUpdateCount = 0;
		List<Integer> noUpList = new ArrayList<Integer>();
		int checkStrange = 0;
		List<Integer> stList = new ArrayList<Integer>(); //중복데이터 의심 목록
		List<Integer> stList2 = new ArrayList<Integer>(); //동일인인지 확인해야할 목록
		String graduated1= null;
		String graduated2 = null;
		int checkStrange2 = 0; //동일인인지 의심을 위한 체크카운트(졸업일,학과 다르고 타 정보 일치항목있을시)
		int notSameCount = 0; // 동명, 입학일 일치 데이터중 졸업일, 학과 가 다를때 
		int nullCount = 0;
		int subCheckCount = 0;
		List<Users> waitList = new ArrayList<Users>();
		int upDateResult = 0;
		
		for(int i=0; i<list.size(); i++) {
			System.out.println(i+" 번째 처리중 ..");
			/*System.out.println(i+" 번째 데이터 : "+list.get(i));*/
			
			// 입학일 년도만 세팅
			if(list.get(i).getAdmission() != null && list.get(i).getAdmission() != "") { //입학일이 있을때
				admission = list.get(i).getAdmission();
				
				if(admission.length() > 4) { //입학일 년월일이 다 입력되있을때
					admission = admission.substring(admission.length()-4, admission.length()); // 날짜형식이 dd/mm/yyyy 임
				}
			}
			
			// 어깨동무 데이터에서 이름, 단과, 입학일(있을때) 조건으로 중복검색
			List<Users> searchList = userDao.selectUserByNameNRel(list.get(i));
			System.out.println("정제될 데이터 조회 갯수 확인 : "+searchList.size());
			
			if(searchList.size() > 1) { // 1. 2개이상의 자료검색될 시 추가 비교하여 동일인여부 확인
				for(int j=0; j<searchList.size(); j++) {
					System.out.println(searchList.get(j).getName()+"님  세부비교 : "+(j+1)+" 번째 비교중..");
					//추가 필드 비교	후 동일인이면 업데이트 하고 아니면 그냥 인서트	
					//휴대폰 뒷4자리 비교
					if(list.get(i).getHp() != null && searchList.get(j).getHp() != null) {
						if(list.get(i).getHp().length() > 4 && searchList.get(j).getHp().length() > 4) {
							if(list.get(i).getHp().substring(list.get(i).getHp().length()-4, list.get(i).getHp().length()).equals(searchList.get(j).getHp().substring(searchList.get(j).getHp().length()-4, searchList.get(j).getHp().length()))) {
								System.out.println("핸드폰 데이터 일치!!");
								sameCount += 1;
							}else {
								subCheckCount +=1;
							}
						}
					}
					
					//집전화 비교
					if(list.get(i).getHomePhone() != null && searchList.get(j).getHomePhone() != null) {
						if(list.get(i).getHomePhone().length() > 4 && searchList.get(j).getHomePhone().length() > 4) {
							if(list.get(i).getHomePhone().substring(list.get(i).getHomePhone().length()-4, list.get(i).getHomePhone().length()).equals(searchList.get(j).getHomePhone().substring(searchList.get(j).getHomePhone().length()-4, searchList.get(j).getHomePhone().length()))) {
								System.out.println("집전화 데이터 일치!!");
								sameCount += 1;
							}else {
								subCheckCount +=1;
							}
						}
					}
					
					// 집주소 비교
					if(list.get(i).getHomeAddress() != null && searchList.get(j).getHomeAddress() != null) {
						if(list.get(i).getHomeAddress().trim().equals(searchList.get(j).getHomeAddress().trim())) {
							System.out.println("집주소 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}
					
					//이메일 비교
					if(list.get(i).getEmail() != null && searchList.get(j).getEmail() !=null){
						if(list.get(i).getEmail().equals(searchList.get(j).getEmail())) {
							System.out.println("이메일 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}
					//회사명
					if(list.get(i).getCompany() != null && searchList.get(j).getCompany() != null) {
						if(list.get(i).getCompany().equals(searchList.get(j).getCompany())) {
							System.out.println("직장 데이터 일치!!");
							sameCount += 1;
						}else {
							subCheckCount +=1;
						}
					}
					// 학과명
					if(list.get(i).getRelation2() != null && searchList.get(j).getRelation2() != null) {
						if(searchList.get(j).getRelation2().contains(list.get(i).getRelation2().substring(0, 2))) {
							System.out.println("학과 일치!!");
							sameCount += 1;
						}else if(list.get(i).getRelation2().equals("문헌정보학과") && searchList.get(j).getRelation2().equals("도서관학과")){
							System.out.println("학과 일치!!");
							sameCount += 1;
						}else {
							System.out.println("학과 불일치!!");
							notSameCount +=1;
						}
					}else {
						sameCount += 1;
						nullCount += 1;
					}
					
					//졸업일
					if(list.get(i).getGraduated() != null && searchList.get(j).getGraduated() != null) {
						if(list.get(i).getGraduated().length() == 4) graduated1 = list.get(i).getGraduated();
						else if(list.get(i).getGraduated().length() > 4) graduated1 = list.get(i).getGraduated().substring(list.get(i).getGraduated().length()-4, list.get(i).getGraduated().length());
						else if(list.get(i).getGraduated().length() == 2) {
							graduated1 = list.get(i).getGraduated();
							graduated1 = graduated1.substring(0, 1);
							if(Integer.parseInt(graduated1) < 5) graduated1 = "20"+graduated1;
							else if(Integer.parseInt(graduated1) > 4) graduated1 = "19"+graduated1;
						}
						
						if(searchList.get(j).getGraduated().length() == 4) graduated2 = searchList.get(j).getGraduated();
						else if(searchList.get(j).getGraduated().length() > 4) graduated2 = searchList.get(j).getGraduated().substring(searchList.get(j).getGraduated().length()-4, searchList.get(j).getGraduated().length());
						else if(searchList.get(j).getGraduated().length() == 2) {
							graduated2 = searchList.get(j).getGraduated();
							graduated2 = graduated2.substring(0,1);
							if(Integer.parseInt(graduated2) < 5) graduated2 = "20"+graduated2;
							else if(Integer.parseInt(graduated2) > 4) graduated2 = "19"+graduated2;
						}
						if(graduated1.equals(graduated2)) {
							System.out.println("졸업일 일치!!");
							sameCount += 1;
						}else {
							System.out.println("졸업일 불일치!!");
							notSameCount +=1;
						}
					}else {
						sameCount += 1;
						nullCount += 1;
					}
					
					// sameCount 1 혹은 2이상 이면 일치 // 0이면 불일치 , ?반복문 안에서 개수 이상이면 처리할것인가 기록했다가 반복문 종료후 가장 많은 일치항목 보유 데이터를 할것인가 ??
					if(notSameCount == 0 && nullCount == 0) { //학과,졸업일 일치
						if(sameCount > 2) { //추가 데이터 일치항목이 있을때 (동일확률100%)
							System.out.println(j+" 번째 조회 데이터 추가 필드 "+sameCount+" 개 일치!! 동일인일 확률 100%..");
							System.out.println("");
							
							if(list.get(i).getEtc2().equals("통화")) {
								upDateResult = userDao.updateUser(setParamForUpdate(searchList.get(j), list.get(i)));
							}else {
								upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));
							}
							if(upDateResult > 0) {
								updateCount++;
								checkStrange++;
							}
							
						}else if(sameCount == 2) { //학과,졸업일만 같고 나머지 일치 항목이 없을때 .. 전 필드값이 null일수도 있으므로 동일인확률 50% . 확인요망
							System.out.println(j+" 번째 조회 데이터 가변필드 "+(sameCount-2)+" 개 일치! 학과,졸업일만 일치!! 동일인일 확률 50% 확인요망");
							System.out.println("");	
							if(subCheckCount == 0) {
								checkStrange2++;
								System.out.println(j+"번째 데이터 대기목록에 추가~!1!");
								waitList.add(searchList.get(j));
							}							
						}
					}else if(notSameCount ==0 && nullCount > 0){ // 학과,졸업일 불일치는 아니나 널값인 필드가 있을때
						if(sameCount > nullCount && sameCount > 2) { //null값을 제외한 추가(가변)필드 일치 항목있을때 - 동일인 확률 100%
							System.out.println(j+" 번째 조회 데이터 추가 필드 "+(sameCount-2)+" 개 일치!! 동일인일 확률 100%..");
							System.out.println("");
							
							if(list.get(i).getEtc2().equals("통화")) {
								upDateResult = userDao.updateUser(setParamForUpdate(searchList.get(j), list.get(i)));
							}else {
								upDateResult = userDao.updateUser(setParamForUpdate2(searchList.get(j), list.get(i)));
							}
							
							if(upDateResult > 0) {
								updateCount++;
								checkStrange++;
							}							
						}else if(sameCount == 2 && nullCount >= 1){ //가변항목 일치 없고 학과,졸업일 1항목 널 1항목 일치시 - 동일인일 확률 50%
							System.out.println(j+" 번째 조회 데이터 가변필드 "+(sameCount-2)+" 개 일치!!  학과 졸업일 null 혹은 일치!! 동일인일 확률 50% 확인요망");
							System.out.println("");	
							if(subCheckCount == 0) {
								checkStrange2++;
								System.out.println(j+"번째 데이터 대기목록에 추가~!2!");
								waitList.add(searchList.get(j));
							}		
						}
					}else if(notSameCount > 0 && nullCount >= 0) { //학과,졸업일 2항목(혹은1) 불일치 상황
						if(sameCount > notSameCount) { //가변필드 일치항목이 있을경우- 확인요망 확률 50%
							System.out.println(j+" 번째 조회 데이터 학과,졸업일 불일치! 가변 필드 "+sameCount+" 개 일치!! 동일인일 확률 30% 확인요망");
							System.out.println("");	
							checkStrange2++;
						}else { //  확률0%
							System.out.println(j+" 번째 추가 필드 일치항목이 없습니다. 동일인일 확률 0%");
							System.out.println("");
						}
					}
					sameCount = 0;
					nullCount = 0;
					notSameCount = 0;
					subCheckCount = 0;										
				} // j인자 for문끝
				//2이상 일치자 확인시 목록 담기(중복일 확률이 높음)
				if(checkStrange > 1) stList.add(list.get(i).getSeqNo());
				else if(checkStrange == 0 && waitList.size() == 0) stList.add(list.get(i).getSeqNo());
				
				
				//다른 필드가 일치하나 학과 혹은 졸업일 혹은 둘다 다를때 확인요망 리스트
				if(waitList.size() == 1 && checkStrange ==0) { //로직실행후 졸업,학과 일치자가 1개일때 동일인으로 인정. 업데이트
					System.out.println("대기 row 1개~!! 업데이트 처리!!");
					upDateResult = userDao.updateUser(setParamForUpdate(waitList.get(0), list.get(i)));
					if(upDateResult > 0) {
						updateCount++;
					}	
				}else { // 업데이트 대기 리스트가 없거나 2개이상일때 확인리스트에 추가하여 확인하기
					if(checkStrange2 > 0 && checkStrange == 0) stList2.add(list.get(i).getSeqNo());
					
				}	
				waitList = new ArrayList<Users>();
				checkStrange = 0;
				checkStrange2 = 0;
				upDateResult = 0;
				
			}else if(searchList.size() == 0) { //2. 조회된 결과가 없을때 -> 데이터 인서트처리
				System.out.println("일치하는 데이터가 없어 신규 인서트 처리 ");
				System.out.println("시퀀스 넘버 : "+list.get(i).getSeqNo());
				System.out.println("");
				noUpList.add(list.get(i).getSeqNo());
				failList.add(list.get(i));
				/*int result = userDao.insertUser(setUsers(list.get(i)));*/
				noUpdateCount++;
				
			}else if(searchList.size() == 1) { //3. 한건만 조회 됐을때 -> 기존데이터와 비교하여 빈곳 업데이트 처리
				System.out.println("한건만 조회되어 업데이트 처리 ");
				System.out.println("");
				upDateResult = userDao.updateUser(setParamForUpdate(searchList.get(0), list.get(i)));
				if(upDateResult > 0) updateCount++;
			} 
		}
		/*captureServ();*/
		System.out.println("==================================================");
		System.out.println("					 정제결과   					  ");
		System.out.println("총카운트 : "+list.size()+"  건");
		System.out.println("정제성공 : "+updateCount+"  건");
		System.out.println("정제실패 : "+noUpdateCount+"  건");
		System.out.println("실패목록 : "+noUpList);
		System.out.println("이상데이터(중복)("+stList.size()+"건)목록 : "+stList);
		System.out.println("필수 확인요망(졸업일,학과 다름) 이상데이터("+stList2.size()+"건)목록 : "+stList2);
		System.out.println("==================================================");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("updateCount", updateCount);
		map.put("noUpdateCount", noUpdateCount);
		map.put("failList", failList);
		
		return map;
	}
	
	
	//인서트 NewUser >> Users 형식으로 세팅하는 함수
	public Users setUsers(TbUser newUser) {
		Users user = new Users();
		user.setAdmission(newUser.getAdmission());
		user.setCompany(newUser.getCompany());
		user.setDepartment(newUser.getDepartment());
		user.setEmail(newUser.getEmail());
		user.setGraduated(newUser.getGraduated());
		user.setStudentId(newUser.getStudentId());
		user.setName(newUser.getName());
		user.setPosition(newUser.getPosition());
		user.setRelation1(newUser.getRelation1());
		user.setRelation2(newUser.getRelation2());
		user.setZipCode(newUser.getZipCode());
		user.setHomeAddress(newUser.getHomeAddress());
		user.setHp(newUser.getHp());
		user.setMainPhone(newUser.getMainPhone());
		user.setHomepage(newUser.getHomepage());
		
		return user;
	}
	
	// 업데이트 > 필드값 세팅(재경) list재경, searchList어깨동무 || setUpdateData(a,b) 는 a보존, setUpdateData2(a,b) 는 b보존 || 어깨동무 통화됬을때 사용
	public Users setParamForUpdate4(Users searchList, TbUser list) {
		if(list.getHp() != null && searchList.getHp() !=null) {
			if(list.getHp().length() > 3 && searchList.getHp().length() > 3) {
				if(list.getHp().substring(0,3).equals("010") && !searchList.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
				else if(searchList.getHp().substring(0,3).equals("010") && !list.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData2(list.getHp(), searchList.getHp()));
				else searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
			}			
		}else {
			searchList.setHp(setUpdateData2(list.getHp(), searchList.getHp()));
		}		
		
		searchList.setCompany(setUpdateData2(list.getCompany(), searchList.getCompany()));
		searchList.setDepartment(setUpdateData2(list.getDepartment(), searchList.getDepartment()));
		searchList.setPosition(setUpdateData2(list.getPosition(), searchList.getPosition()));
		searchList.setMainPhone(setUpdateData2(list.getMainPhone(), searchList.getMainPhone()));
		searchList.setHomeAddress(setUpdateData2(list.getHomeAddress(), searchList.getHomeAddress()));
		searchList.setHomePhone(setUpdateData2(list.getHomePhone(), searchList.getHomePhone()));
		searchList.setEmail(setUpdateData2(list.getEmail(), searchList.getEmail()));
		searchList.setZipCode(setUpdateData2(list.getZipCode(), searchList.getZipCode()));
		searchList.setMemo(searchList.getMemo()+" 재경");
		searchList.setComAddress(setUpdateData2(list.getComAddress(), searchList.getComAddress()));
		return searchList;
	}
	
	// 업데이트 > 필드값 세팅(재경) list재경, searchList어깨동무 || setUpdateData(a,b) 는 a보존, setUpdateData2(a,b) 는 b보존 || 어깨동무 통화 안됐을때 사용
	public Users setParamForUpdate3(Users searchList, TbUser list) {
		if(list.getHp() != null && searchList.getHp() !=null) {
			if(list.getHp().length() > 3 && searchList.getHp().length() > 3) {
				if(list.getHp().substring(0,3).equals("010") && !searchList.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
				else if(searchList.getHp().substring(0,3).equals("010") && !list.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData2(list.getHp(), searchList.getHp()));
				else searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
			}			
		}else {
			searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
		}		
		
		if(list.getCompany() != null) {
			if(list.getCompany().length()==5 && list.getCompany().equals("전북대학교")) {
				searchList.setCompany(setUpdateData2(list.getCompany(), searchList.getCompany()));
			}else {
				searchList.setCompany(setUpdateData(list.getCompany(), searchList.getCompany()));
			}
		}else {
			searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
		}
		
		
		if(list.getPosition() != null) {
			if(list.getPosition().length()>3) {
				if(list.getPosition().substring(0, 2).equals("학생")) {
					searchList.setPosition(setUpdateData2(list.getPosition(), searchList.getPosition()));
				}else {
					searchList.setPosition(setUpdateData(list.getPosition(), searchList.getPosition()));
				}
			}
		}else {
			searchList.setPosition(setUpdateData2(list.getPosition(), searchList.getPosition()));
		}
		
		searchList.setHomeAddress(setUpdateData(list.getHomeAddress(), searchList.getHomeAddress()));
		if(list.getMainPhone() != null) {
			if(list.getMainPhone().length() > 2 && !list.getMainPhone().substring(0, 2).equals("01")) {
				searchList.setMainPhone(setUpdateData(list.getMainPhone(), searchList.getMainPhone()));
			}else if(list.getMainPhone().length() > 2 && list.getMainPhone().substring(0, 2).equals("01")) {
				searchList.setMainPhone(setUpdateData2(list.getMainPhone(), searchList.getMainPhone()));
			}
		}else {
			searchList.setMainPhone(setUpdateData(list.getMainPhone(), searchList.getHomePhone()));
		}
		
		if(list.getHomePhone() != null) {
			if(list.getHomePhone().length() > 2 && !list.getHomePhone().substring(0, 2).equals("01")) {
				searchList.setHomePhone(setUpdateData(list.getHomePhone(), searchList.getHomePhone()));
			}else if(list.getHomePhone().length() > 2 && list.getHomePhone().substring(0, 2).equals("01")) {
				searchList.setHomePhone(setUpdateData2(list.getHomePhone(), searchList.getHomePhone()));
			}
		}else {
			searchList.setHomePhone(setUpdateData(list.getHomePhone(), searchList.getHomePhone()));
		}
		
		searchList.setEmail(setUpdateData(list.getEmail(), searchList.getEmail()));
		searchList.setZipCode(setUpdateData(list.getZipCode(), searchList.getZipCode()));
		searchList.setDepartment(setUpdateData(list.getDepartment(), searchList.getDepartment()));
		searchList.setComAddress(setUpdateData(list.getComAddress(), searchList.getComAddress()));
		return searchList;
	}

	// 업데이트 > 필드값 세팅 (어깨동무 신구)
	public Users setParamForUpdate(Users searchList, TbUser list) {
		if(list.getHp() != null && searchList.getHp() !=null) {
			if(list.getHp().length() > 3 && searchList.getHp().length() > 3) {
				if(list.getHp().substring(0,3).equals("010") && !searchList.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
				else if(searchList.getHp().substring(0,3).equals("010") && !list.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData2(list.getHp(), searchList.getHp()));
				else searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
			}			
		}else {
			searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
		}
		
		
		searchList.setCompany(setUpdateData(list.getCompany(), searchList.getCompany()));
		searchList.setPosition(setUpdateData(list.getPosition(), searchList.getPosition()));
		searchList.setMainPhone(setUpdateData(list.getMainPhone(), searchList.getMainPhone()));
		searchList.setHomeAddress(setUpdateData(list.getHomeAddress(), searchList.getHomeAddress()));
		searchList.setHomeSangseAdd(setUpdateData(list.getHomeSangseAdd(), searchList.getHomeSangseAdd()));
		searchList.setHomePhone(setUpdateData(list.getHomePhone(), searchList.getHomePhone()));
		searchList.setEmail(setUpdateData(list.getEmail(), searchList.getEmail()));
		searchList.setEtc2(setUpdateData(list.getEtc2(), searchList.getEtc2()));
		searchList.setCallDate(setUpdateData(list.getCallDate(), searchList.getCallDate()));
		searchList.setZipCode(setUpdateData(list.getZipCode(), searchList.getZipCode()));
		searchList.setMemo(searchList.getMemo()+" "+list.getMemo());
		return searchList;
	}
	
	// 업데이트 > 필드값 세팅 (발전지원부)
	public Users setParamForUpdate2(Users searchList, TbUser list) {
		if(list.getHp() != null && searchList.getHp() !=null) {
			if(list.getHp().length() > 3 && searchList.getHp().length() > 3) {
				if(list.getHp().substring(0,3).equals("010") && !searchList.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
				else if(searchList.getHp().substring(0,3).equals("010") && !list.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData2(list.getHp(), searchList.getHp()));
				else searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
			}			
		}else {
			searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
		}		
		
		if(list.getCompany() != null) {
			if(list.getCompany().length()==5 && list.getCompany().equals("전북대학교")) {
				searchList.setCompany(setUpdateData2(list.getCompany(), searchList.getCompany()));
			}else {
				searchList.setCompany(setUpdateData(list.getCompany(), searchList.getCompany()));
			}
		}else {
			searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
		}
		
		if(list.getPosition() != null) {
			if(list.getPosition().length()>3) {
				if(list.getPosition().substring(0, 2).equals("학생")) {
					searchList.setPosition(setUpdateData2(list.getPosition(), searchList.getPosition()));
				}else {
					searchList.setPosition(setUpdateData(list.getPosition(), searchList.getPosition()));
				}
			}
		}else {
			searchList.setPosition(setUpdateData2(list.getPosition(), searchList.getPosition()));
		}
		
		searchList.setHomeAddress(setUpdateData(list.getHomeAddress(), searchList.getHomeAddress()));
		if(list.getMainPhone() != null) {
			if(list.getMainPhone().length() > 2 && !list.getMainPhone().substring(0, 2).equals("01")) {
				searchList.setHomePhone(setUpdateData(list.getMainPhone(), searchList.getHomePhone()));
			}else if(list.getMainPhone().length() > 2 && list.getMainPhone().substring(0, 2).equals("01")) {
				searchList.setHomePhone(setUpdateData2(list.getMainPhone(), searchList.getHomePhone()));
			}
		}else {
			searchList.setHomePhone(setUpdateData(list.getMainPhone(), searchList.getHomePhone()));
		}
		searchList.setEmail(setUpdateData(list.getEmail(), searchList.getEmail()));
		searchList.setZipCode(setUpdateData(list.getZipCode(), searchList.getZipCode()));
		return searchList;
	}
		
	// 업데이트 > 필드값 비교 및 보존할 데이터 세팅하는 함수(통화일경우)
	public String setUpdateData(String a, String b) {
		//둘다 널이 아닐때 a 파리미터 데이터 보존 우선
		String result = null;
		if(a == null && b != null) { 
			result = b;
		}else if(a != null && b == null) {
			result = a;
		}else if(a != null && b != null) {
			result = a; //보존데이터 선정시 바꿔야함
		}
		return result;
	}
	
	// 업데이트 > 필드값 비교 및 보존할 데이터 세팅하는 함수(통화일경우 외 )
	public String setUpdateData2(String a, String b) {
		//둘다 널이 아닐때 a 파리미터 데이터 보존 우선
		String result = null;
		if(a == null && b != null) { 
			result = b;
		}else if(a != null && b == null) {
			result = a;
		}else if(a != null && b != null) {
			result = b; //보존데이터 선정시 바꿔야함
		}
		return result;
	}
	
	// 전화번호 형식 일원화 처리 하는 함수 1000건단위 실행 (퍼짐방지)
	public void refineHpServ(int startNum, int endNum) {
		//1000건씩 조회되는 전화번호 데이터 1부터 시작
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("startNum", startNum);
		params.put("endNum", endNum);
		
		List<Users> list = userDao.selectHpForRefine(params);
		List<Integer> errorList = new ArrayList<Integer>();
		
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getHp() != null) {
				String hp = list.get(i).getHp();
				if(hp.substring(0,1).equals("1")) {
					hp = "0" + hp;
					if(hp.length() == 10) {
						hp = hp.substring(0, 3) + "-" + hp.substring(3, 6)+"-"+hp.substring(6, hp.length());
					}else if(hp.length() == 11) {
						hp = hp.substring(0, 3) + "-" + hp.substring(3, 7)+"-"+hp.substring(7, hp.length());
					}else {
						errorList.add(list.get(i).getSeqNo());
					}
					/*System.out.println("일원화된 폰번호 확인 : "+hp);*/
					list.get(i).setHp(hp);					
				}
			}
			if(list.get(i).getAdmission() != null && list.get(i).getAdmission().length() > 4) {
				String admission = list.get(i).getAdmission();
				System.out.println("원형 입학일 확인:"+admission);
				System.out.println("원형 입학일 길이 확인:"+admission.length());
				if(admission.length() == 10) {
					System.out.println("10자리 입학일 확인 : "+admission);
					String first = admission.substring(admission.length()-4, admission.length());
					String second = admission.substring(0,2);
					String third = admission.substring(3,5);
					admission = first+"-"+second+"-"+third;
					System.out.println("입학일 변환값 확인 : "+admission);
				}else if(admission.length() == 6){
					admission = admission.substring(0,4);
				}
				list.get(i).setAdmission(admission);
				
			}
			if(list.get(i).getGraduated() != null && list.get(i).getGraduated().length() > 4) {
				String graduated = list.get(i).getGraduated();
				if(graduated.length() == 10) {
					String first = graduated.substring(graduated.length()-4, graduated.length());
					String second = graduated.substring(0,2);
					String third = graduated.substring(3,5);
					graduated = first+"-"+second+"-"+third;
				}else if(graduated.length() != 10){
					graduated = graduated.substring(0,4);
				}
				list.get(i).setGraduated(graduated);
			}
			if(list.get(i).getHomePhone() != null) {
				String hp = list.get(i).getHomePhone();
				if(hp.substring(0,1).equals("1") || hp.substring(0,1).equals("2")) {
					hp = "0" + hp;
					if(hp.length() == 10) {
						hp = hp.substring(0, 3) + "-" + hp.substring(3, 6)+"-"+hp.substring(6, hp.length());
					}else if(hp.length() == 11) {
						hp = hp.substring(0, 3) + "-" + hp.substring(3, 7)+"-"+hp.substring(7, hp.length());
					}else {
						errorList.add(list.get(i).getSeqNo());
					}														
				}else if(hp.substring(0,1).equals("3") || hp.substring(0,1).equals("4") || hp.substring(0,1).equals("5") || hp.substring(0,1).equals("6") || hp.substring(0,1).equals("7") || hp.substring(0,1).equals("8")) {
					hp = "0" + hp;
					if(hp.length() == 10) {
						hp = hp.substring(0, 3) + "-" + hp.substring(3, 6)+"-"+hp.substring(6, hp.length());
					}					
				}
				list.get(i).setHomePhone(hp);
			}
			if(list.get(i).getMainPhone() != null) {
				String mp = list.get(i).getMainPhone();
				if(mp.substring(0,1).equals("1") || mp.substring(0,1).equals("2")) {
					mp = "0" + mp;
					if(mp.length() == 10) {
						mp = mp.substring(0, 3) + "-" + mp.substring(3, 6)+"-"+mp.substring(6, mp.length());
					}else if(mp.length() == 11) {
						mp = mp.substring(0, 3) + "-" + mp.substring(3, 7)+"-"+mp.substring(7, mp.length());
					}else {
						errorList.add(list.get(i).getSeqNo());
					}														
				}else if(mp.substring(0,1).equals("3") || mp.substring(0,1).equals("4") || mp.substring(0,1).equals("5") || mp.substring(0,1).equals("6") || mp.substring(0,1).equals("7") || mp.substring(0,1).equals("8")) {
					mp = "0" + mp;
					if(mp.length() == 10) {
						mp = mp.substring(0, 3) + "-" + mp.substring(3, 6)+"-"+mp.substring(6, mp.length());
					}					
				}
				list.get(i).setMainPhone(mp);
			}
			int result = userDao.updateHp(list.get(i));
			
			if(result == 1) System.out.println(i+" 번째 업데이트 성공!!");
			else if(result == 0) System.out.println(i+" 번째 업데이트 실패!!");
			
		}
		/*System.out.println("전화번호 확인 해야할 시퀀스 목록 : "+errorList);*/
	}
	
	// 0000-000-0000 >> 000-0000-0000변환
	public void refineHp2Serv() {
		//1000건씩 조회되는 전화번호 데이터 1부터 시작
		List<Users> list = userDao.selectHpForRefine2();
		
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getHp() != null) {
				String hp = list.get(i).getHp();
				hp = hp.replaceAll("-", "");
				/*System.out.println("하이픈제거된 번호 확인 : "+hp);*/
				hp = hp.substring(0, 3) + "-" + hp.substring(3, 7)+"-"+hp.substring(7, hp.length());
				/*System.out.println("일원화된 폰번호 확인 : "+hp);*/
				list.get(i).setHp(hp);
				
				int result = userDao.updateHp(list.get(i));
				/*System.out.println(i+" 번째 수정체크 : "+result);*/
			
			}
		}		
	}
	
	// HP필드값은 null이고 집전화 notNull이고 집전화 형식이 "01"로 시작할때 핸드폰번호 필드로 값 옮기기
	public void refineHpChangFiledServ() {
		// 핸드폰번호 필드가 널인 행들 조회
		List<Users> list = userDao.selectHpIsNull();
		
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getMainPhone() != null && list.get(i).getMainPhone().length() > 9) {
				String homePhone = list.get(i).getMainPhone();
				if(homePhone.substring(0,2).equals("01")) {
					list.get(i).setHp(homePhone);
					System.out.println("필드옮길"+i+" 번째 전화번호 확인 : "+homePhone);
					int result = userDao.updateHp(list.get(i));
					System.out.println(i+" 번째 수정체크 : "+result);
				}
			}
		}
	}
	
	//이상데이터 목록
	public List<Users> splitData(int startNum) {
		/* 어깨동무 체크리스트
		 * int[] data = {58, 8336, 8585, 9390, 9391, 9414, 9415, 9424, 9425, 10648, 14087, 14088, 14822, 18759, 18898, 19448,
				20976, 20977, 22341, 22746, 23191, 23843, 30276, 30675, 30676, 30685, 30686, 30834, 30835, 30880, 30881, 30938, 30939, 30941, 30945, 31304, 31393, 32349, 32412, 32413, 32426, 32427, 32429, 32430, 32847, 32855, 32872, 32877, 32965, 32984, 33011, 33012, 33137, 33204, 33214, 33215, 33236, 33244, 33258, 33314, 33330, 33339, 33346, 33393, 33416, 33464, 33549, 33561, 33646, 33670, 33714, 33987, 33995, 34014, 34078, 34171, 34264, 34269, 34323, 34352, 34503, 34505, 34526, 34571, 34601, 34609, 34643, 34726, 34835, 34899, 34905, 34918, 35036, 35114, 35116, 35123, 35150, 35189, 35463, 35464, 35465, 35466, 35666, 35670, 36179, 36184, 36212, 36215, 36217, 36218, 36220, 36233, 36234, 36236, 36251, 36264, 36269, 36290, 36291, 36305, 36306, 36320, 36347, 36350, 36390, 36392, 36393, 36403, 36409, 36410, 36433, 36435, 36436, 36447, 36448, 36452, 36454, 36457, 36478, 36479, 36486, 36488, 36489, 36514, 36515, 36519, 36520, 36535, 36543, 36557, 36578, 36582, 36584, 36590, 36591, 36596, 36597, 36606, 36616, 36637, 36641, 36663, 36679, 36696, 36707, 36708, 36715, 36719, 36724, 36738, 36742, 36750, 36777, 36784, 36785, 36793, 36800, 36801, 36805, 36812, 36813, 36905, 36923, 36932, 37020, 37021, 37168, 37229, 37230, 37231, 37428, 37440, 37446, 37571, 38072, 38307, 38308, 38331, 38389, 38390, 38401, 38402, 38410, 38454, 38455, 38460, 38461, 38488, 38511, 38718, 38766, 38812, 38813, 38828, 38831, 39073, 39079, 39110, 39182, 39248, 39251, 39509, 39525, 39526, 39530, 39535, 39541, 39542, 39544, 39664, 39797, 39826, 39935, 39978, 40086, 40184, 40217, 40221, 40222, 40404, 40639, 40641, 40660, 40697, 40698, 40806, 40904, 40960, 41072, 41107, 41137, 41145, 42375, 42376, 42399, 45079, 47778, 48180,
				51785, 53363, 53364, 53450, 53451, 53452, 53453, 53456, 53513, 54065, 54603, 54808, 54809, 54812, 54813, 54823, 54825, 54826, 54842, 54843, 55030, 55031, 55793, 55794, 57254, 61641, 61642, 62036, 62037, 62307, 62308, 63178, 63523, 63524, 63579, 63580, 66473, 66474, 66868, 66885, 66886, 66952, 66978, 67098, 67099, 67261, 67262, 67315, 67352, 67353, 67694, 67876, 67933, 67991, 68019, 68333, 68628, 68718, 68719, 69316, 70304, 70524, 71538, 71710, 71718, 72611, 73271, 73996, 73997, 74221, 77950, 77951,
				83586, 83587, 83676, 83677, 85350, 89707, 89708, 90212, 90213, 90301, 90302, 94427, 94428, 95415, 96289, 96290, 96293, 96294, 96295, 96296, 96299, 96300, 96301, 96302, 96307, 96310, 96311, 96313, 96314, 96316, 96319, 96320, 96323, 96326, 96335, 96336, 96337, 96338, 96340, 96341, 96342, 96343, 96346, 96348, 96349, 96350, 96351, 96352, 100487, 100488, 101001, 107500, 109900,
				111729, 111730, 117038, 117438, 123777, 123822, 123949, 123950, 123956, 123958, 123985, 124008, 124009, 124061, 124062, 124083, 124084, 124089, 124090, 124098, 124099, 124100, 124101, 124177, 124178, 124234, 124238, 124239, 124244, 124245, 124248, 124250, 124261, 124262, 124287, 124298, 124302, 124316, 124339, 124350, 124401, 124402, 124411, 124412, 124413, 124414, 124415, 124416, 124417, 124418, 124421, 124422, 124438, 124439, 124442, 124443, 124445, 124446, 124453, 124454, 124455, 124456, 124462, 124463, 124481, 124482, 124483, 124484};
		 */
		
		// 발전지원부(체크리스트)
		int[] data = {
				1010, 1117, 1203, 1212, 1543, 2376, 2474, 2482, 4090, 4091, 12459, 12708, 13513, 13514, 14771, 18255, 22882, 23021, 23571, 25100, 34399, 34798, 34808, 35064, 37496, 38961, 46881, 46882, 49700,
				50890, 56738, 57133, 62449, 63815, 63816, 73047, 73748, 74432, 78773, 89926, 90511, 91385, 91447, 102596, 104996, 106826, 112133, 112534, 119186, 119273, 119340, 119346, 119357, 119398, 119435
		};
		
		/* 재경인명록
		 * int[] data = {
				80077, 80504, 81181, 85023, 85180, 85521, 85714, 85774, 86495, 86803, 86869, 86958, 87084, 87148, 87292, 87603, 88188, 88416, 88478, 88507, 88989, 89095, 89284, 95418, 98343, 98988, 99069, 99735, 99842, 106282, 107278, 107359, 107459, 107638, 108416, 108420, 108432, 108605, 108606, 108635, 108636, 108673, 108675, 108705, 108735, 109588, 109619, 109622, 109627, 109628, 109648, 109719, 109778, 109960, 110054, 110066, 110069, 110071, 110331, 110345, 110354, 110356, 110388, 110462, 110477, 110478, 110515, 110523, 110531, 110573, 110588, 110603, 110626, 110673, 110805, 110851, 110852, 110963, 110968, 110984, 111001, 111021, 111038, 112117, 112122, 112123, 112129, 112131, 112134, 112147, 112168, 112174, 112184, 112187, 112189, 112240, 112243, 112248, 112251, 112254, 112271, 112288, 112292, 112309, 112311, 112334, 112338, 112368, 112369, 112378, 112506, 112509, 113686, 113701, 113722, 113725, 113728, 113732, 113750, 113754, 113862, 114026, 115085, 115086
		};*/
		
		int dataLength = data.length;
		int endNum = startNum + 9;
		if(endNum > dataLength) endNum = dataLength;
		
		List<Users> userList = new ArrayList<Users>();
		
		for(int i=startNum-1; i<endNum; i++) {
			/* 어깨동무 신구
			 * Users user = userDao.selectUserStrange(data[i]);*/
			
			Users user = userDao.selectUserStrange(data[i]);
			
			/* 재경인명록
			 * Users user = userDao.selectUserStrange3(data[i]);*/
			userList.add(user);
		}
		
		return userList;
	}
	
	// 주소변환 > 대상회원조회
	public List<Users> readUserAddServ(int startNum, int endNum){
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("startNum", startNum);
		params.put("endNum", endNum);
		
		System.out.println("시작 : "+startNum+" , "+" 끝 : "+endNum);
		
		List<Users> list = userDao.selectUserAdd(params);
		System.out.println("조회확인 : "+list.size());
		
		return list;
	}
	
	// 주소변환 
	public int modifyAddToRoadAddServ(Users users) {
		int result = userDao.updateAddToRoadAdd(users);
		return result;
	}
	
	//중복체크
	public Map<String, Object> sameCheckServ(int seqNo){
		Map<String, Object> map = new HashMap<String, Object>();
		//목록 원본테이블에서 조회
		
		/* 어깨동무 신구*/
		 Users user = userDao.selectUserStrange(seqNo);
		
		/*Users user = userDao.selectUserStrange2(seqNo);*/ //발전지원부
		
		/*Users user = userDao.selectUserStrange3(seqNo);*/ // 재경인명록
		
		TbUser tbUser = new TbUser();
		//조회된 데이터로 파라미터 세팅
		tbUser.setName(user.getName());
		tbUser.setRelation1(user.getRelation1());
		tbUser.setAdmission(user.getAdmission());
		
		List<Users> list = userDao.selectUserByNameNRel(tbUser);
		System.out.println("조회된 값 확인 : "+list);
		map.put("user", user);
		map.put("list", list);
		return map;
	}
	
	//데이터삭제
	public int removeDataServ(int seqNo) {
		return userDao.deleteData(seqNo);
	}
	
	//데이터 수정
	public int modifyDataServ(int seqNo, int seqNo2) {
		// 수정데이터 조회
		/* 어깨동무 신구
		 * Users user = userDao.selectUserStrange(seqNo2);*/
		
		Users user = userDao.selectUserStrange(seqNo2);
		
		/* 재경인명록
		 * Users user = userDao.selectUserStrange3(seqNo2);*/
		
		// 수정해야될 데이터 조회
		Users user2 = userDao.selectUserBySeqNo(seqNo);
		
		user2 = setParamForModify(user2, user);
		int result = userDao.updateUser(user2);
		return result;
	}
	
	// 업데이트 > 필드값 세팅 (중복 - 어깨동무 신구)
	public Users setParamForModify(Users searchList, Users list) {
		if(list.getHp() != null && searchList.getHp() !=null) {
			if(list.getHp().length() > 3 && searchList.getHp().length() > 3) {
				if(list.getHp().substring(0,3).equals("010") && !searchList.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
				else if(searchList.getHp().substring(0,3).equals("010") && !list.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData2(list.getHp(), searchList.getHp()));
				else searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
			}			
		}else {
			searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
		}		
		
		searchList.setCompany(setUpdateData(list.getCompany(), searchList.getCompany()));
		searchList.setPosition(setUpdateData(list.getPosition(), searchList.getPosition()));
		searchList.setMainPhone(setUpdateData(list.getMainPhone(), searchList.getMainPhone()));
		searchList.setHomeAddress(setUpdateData(list.getHomeAddress(), searchList.getHomeAddress()));
		searchList.setHomeSangseAdd(setUpdateData(list.getHomeSangseAdd(), searchList.getHomeSangseAdd()));
		searchList.setHomePhone(setUpdateData(list.getHomePhone(), searchList.getHomePhone()));
		searchList.setEmail(setUpdateData(list.getEmail(), searchList.getEmail()));
		searchList.setEtc2(setUpdateData(list.getEtc2(), searchList.getEtc2()));
		searchList.setCallDate(setUpdateData(list.getCallDate(), searchList.getCallDate()));
		searchList.setZipCode(setUpdateData(list.getZipCode(), searchList.getZipCode()));
		searchList.setMemo(searchList.getMemo()+" "+list.getMemo());
		return searchList;
	}
	
	// 업데이트 > 필드값 세팅 (중복 - 발전지원부)
	public Users setParamForModify2(Users searchList, Users list) {
		if(list.getHp() != null && searchList.getHp() !=null) {
			if(list.getHp().length() > 3 && searchList.getHp().length() > 3) {
				if(list.getHp().substring(0,3).equals("010") && !searchList.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
				else if(searchList.getHp().substring(0,3).equals("010") && !list.getHp().substring(0,3).equals("010")) searchList.setHp(setUpdateData2(list.getHp(), searchList.getHp()));
				else searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
			}			
		}else {
			searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
		}		
		
		if(list.getCompany() != null) {
			if(list.getCompany().length()==5 && list.getCompany().equals("전북대학교")) {
				searchList.setCompany(setUpdateData2(list.getCompany(), searchList.getCompany()));
			}else {
				searchList.setCompany(setUpdateData(list.getCompany(), searchList.getCompany()));
			}
		}else {
			searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
		}
		
		if(list.getPosition() != null) {
			if(list.getPosition().length()>3) {
				if(list.getPosition().substring(0, 2).equals("학생")) {
					searchList.setPosition(setUpdateData2(list.getPosition(), searchList.getPosition()));
				}else {
					searchList.setPosition(setUpdateData(list.getPosition(), searchList.getPosition()));
				}
			}
		}else {
			searchList.setPosition(setUpdateData2(list.getPosition(), searchList.getPosition()));
		}
		
		searchList.setHomeAddress(setUpdateData(list.getHomeAddress(), searchList.getHomeAddress()));
		if(list.getMainPhone() != null) {
			if(list.getMainPhone().length() > 2 && !list.getMainPhone().substring(0, 2).equals("01")) {
				searchList.setHomePhone(setUpdateData(list.getMainPhone(), searchList.getHomePhone()));
			}else if(list.getMainPhone().length() > 2 && list.getMainPhone().substring(0, 2).equals("01")) {
				searchList.setHomePhone(setUpdateData2(list.getMainPhone(), searchList.getHomePhone()));
			}
		}else {
			searchList.setHomePhone(setUpdateData(list.getMainPhone(), searchList.getHomePhone()));
		}
		searchList.setEmail(setUpdateData(list.getEmail(), searchList.getEmail()));
		searchList.setZipCode(setUpdateData(list.getZipCode(), searchList.getZipCode()));
		return searchList;
	}
	
	//주소치환
	public void changeAddBlankServ(){
		List<Users> list = userDao.selectBlankAdd();
		
		for(int i=0; i<list.size(); i++) {
			list.get(i).setHomeRoadAdd(list.get(i).getHomeAddress());
			int result = userDao.updateBlankRoadAdd(list.get(i));
			if(result > 0) System.out.println(i+"번째 덮어쓰기 성공!!!");
			else if(result==0) System.out.println(i+"번째 덮으쓰기 실패!!!!");
		}		
	}
	
	// 구주소 + 구 상세주소
	public void changeAddPlusSangseServ(){
		List<Users> list = userDao.selectAddNSangseAdd();
		
		for(int i=0; i<list.size(); i++) {
			list.get(i).setHomeAddress(list.get(i).getHomeAddress()+" "+list.get(i).getHomeSangseAdd());
			int result = userDao.updateAddInSangseAdd(list.get(i));
			if(result > 0) System.out.println(i+"번째 덮어쓰기 성공!!!");
			else if(result==0) System.out.println(i+"번째 덮으쓰기 실패!!!!");
		}		
	}
	
	// 전화번호 날짜 형식 통일
	public void refineHpNDateServ() {
		
	}
}
