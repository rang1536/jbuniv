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
	
	//DB정제 발전지원부 <> 어깨동무
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
		List<Integer> stList = new ArrayList<Integer>();
		
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
							}
						}
					}
					//이메일 비교
					if(list.get(i).getEmail() != null && searchList.get(j).getEmail() !=null){
						if(list.get(i).getEmail().equals(searchList.get(j).getEmail())) {
							System.out.println("이메일 데이터 일치!!");
							sameCount += 1;
						}
					}
					//회사명
					if(list.get(i).getCompany() != null && searchList.get(j).getCompany() != null) {
						if(list.get(i).getCompany().equals(searchList.get(j).getCompany())) {
							System.out.println("직장 데이터 일치!!");
							sameCount += 1;
						}
					}
					// 학과명
					if(list.get(i).getRelation2() != null && searchList.get(j).getRelation2() != null) {
						if(searchList.get(j).getRelation2().contains(list.get(i).getRelation2().substring(0, 2))) {
							System.out.println("학과 일치!!");
							sameCount += 1;
						}
					}
					
					// sameCount 1 혹은 2이상 이면 일치 // 0이면 불일치 , ?반복문 안에서 개수 이상이면 처리할것인가 기록했다가 반복문 종료후 가장 많은 일치항목 보유 데이터를 할것인가 ??
					if(sameCount > 0) {
						System.out.println(j+" 번째 조회 데이터 추가 필드 "+sameCount+" 개 일치!! 동일인일 확률 90%..");
						System.out.println("");
						int upDateResult = userDao.updateUser(setParamForUpdate(searchList.get(j), list.get(i)));
						if(upDateResult > 0) {
							updateCount++;
							checkStrange++;
						}
						sameCount = 0;
						
					}else if(sameCount == 0) {
						System.out.println(j+" 번째 추가 필드 일치항목이 없습니다. 동일인일 확률 0%");
						System.out.println("");						
					}
					
				}
				//2이상 일치자 확인시 목록 담기
				if(checkStrange > 1 || checkStrange == 0) stList.add(list.get(i).getSeqNo());
				checkStrange = 0;
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
				int upDateResult = userDao.updateUser(setParamForUpdate(searchList.get(0), list.get(i)));
				if(upDateResult > 0) updateCount++;
			} 
		}
		System.out.println("==================================================");
		System.out.println("					 정제결과   					  ");
		System.out.println("총카운트 : "+list.size()+"  건");
		System.out.println("정제성공 : "+updateCount+"  건");
		System.out.println("정제실패 : "+noUpdateCount+"  건");
		System.out.println("실패목록 : "+noUpList);
		System.out.println("이상데이터목록 : "+stList);
		System.out.println("==================================================");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("updateCount", updateCount);
		map.put("noUpdateCount", noUpdateCount);
		map.put("failList", failList);
		
		return map;
	}
	
	
	//인서트 NewUser >> Users 형식으로 세팅하는 함수
	public Users setUsers(NewUser newUser) {
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
	
	// 업데이트 > 필드값 세팅
	public Users setParamForUpdate(Users searchList, TbUser list) {
		searchList.setHp(setUpdateData(list.getHp(), searchList.getHp()));
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
		return searchList;
	}
	
	// 업데이트 > 필드값 비교 및 보존할 데이터 세팅하는 함수
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
	
	
	// 전화번호 형식 일원화 처리 하는 함수 1000건단위 실행 (퍼짐방지)
	public void refineHpServ() {
		//1000건씩 조회되는 전화번호 데이터 1부터 시작
		List<Users> list = userDao.selectHpForRefine();
		List<Integer> errorList = new ArrayList<Integer>();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getHp() != null) {
				String hp = list.get(i).getHp();
				if(hp.substring(0,1).equals("1")) {
					System.out.println(i+" 번째 핸드폰번호 : "+hp);
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
					int result = userDao.updateHp(list.get(i));
					/*System.out.println(i+" 번째 수정체크 : "+result);*/
				}
			}
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
	
	//이상데이터 쪼개기
	public List<Users> splitData() {
		int[] data2 = {120, 216, 228, 254, 320, 358, 487, 534, 955, 956, 1045, 1046, 1309, 1310, 1754, 1755, 1978, 1979, 1980, 1981, 3419, 3420, 3916, 3917, 4043, 4044, 4213, 4214, 5614, 5615, 5884, 6189, 6336, 6366, 6371, 6448, 6468, 6470, 6591, 6619, 6620, 6923, 6951, 6985, 6986, 7034, 7035, 7047, 7048, 7244, 7245, 7596, 7629, 7657, 7658, 7673, 7676, 7746, 7802, 8054, 8082, 8090, 8192, 8202, 8223, 8224, 8225, 8270, 8332, 8465, 8509, 8525, 8579, 8593, 8616, 8620, 8623, 8644, 8715, 8716, 8748, 8750, 8784, 8804, 8845, 8855, 8921, 8960, 8994, 9013, 9037, 9039, 9046, 9100, 9168, 9180, 9199, 9200, 9262, 9341, 9366, 9390, 9391, 9414, 9415, 9424, 9425, 9488, 9502, 9591, 9675, 9725, 9741, 9764, 9785, 9844, 9845};
		int[] data5 = {10858, 10904, 11334, 11335, 11928, 11929, 12125, 12126, 12879, 13135, 13160, 13928, 13929, 14047, 14087, 14088, 14097, 14117, 14565, 14566, 14580, 14581};
		int[] data6 = {15925, 15926, 16722, 16723, 16750, 17878, 17879, 18551, 18657, 18759, 18810, 18811, 18812, 18818, 18829, 18898, 18946, 19118, 19163, 19164, 19202, 19325, 19380, 19497, 19498, 19722, 19787, 19791, 19850, 19921, 19947, 20024, 20165, 20178, 20211, 20214, 20229, 20231, 20245, 20258, 20282, 20326, 20336, 20337, 20493, 20502, 20503, 20528, 20556, 20591, 20655, 20709, 20900, 20910, 20932, 20935, 20938, 20952, 20976, 20977, 21029, 21056, 21151, 21152, 21200, 21303, 21304, 21344, 21345, 21560, 21675, 21867, 21874, 21880, 21881, 21937, 22047, 22049, 22052, 22155, 22230, 22314, 22315, 23089, 23188, 23189, 23191, 23341, 23843, 24065, 24086, 24088, 24632, 24633, 24645, 24666, 25268, 25281, 27072, 27073, 27106, 27107, 27866, 28343, 28344, 28413, 28430, 28431, 28450, 28451, 29786, 29787, 29983, 30445, 30446, 30503, 30504, 30675, 30676, 30685, 30686, 30707, 30708, 30754, 30815, 30816, 30834, 30835, 30860, 30861, 30869, 30870, 30880, 30881, 30932, 30938, 30939, 30945, 31050, 31210, 31256, 31279, 31304, 31392, 31393, 31474, 31569, 31583, 31584, 31601, 31602, 31761, 31762, 32226, 32227, 32259, 32269, 32275, 32349, 32412, 32413, 32424, 32426, 32427, 32429, 32430, 32431, 32432, 32435, 32626, 32627, 32661, 32847, 32850, 32851, 32855, 32856, 32865, 32866, 32871, 32872, 32877, 32878, 32884, 32885, 32890, 32891, 32899, 32904, 32912, 32938, 32947, 32948, 32963, 32965, 32984, 33011, 33012, 33037, 33050, 33053, 33054, 33055, 33056, 33057, 33064, 33069, 33070, 33071, 33076, 33094, 33103, 33104, 33123, 33124, 33129, 33130, 33137, 33140, 33142, 33143, 33144, 33147, 33148, 33155, 33156, 33157, 33164, 33165, 33166, 33176, 33177, 33178, 33181, 33185, 33188, 33189, 33190, 33191, 33195, 33200, 33201, 33202, 33203, 33204, 33214, 33215, 33217, 33227, 33228, 33229, 33233, 33236, 33237, 33238, 33239, 33240, 33244, 33245, 33249, 33250, 33251, 33258, 33261, 33262, 33265, 33266, 33268, 33269, 33273, 33275, 33280, 33281, 33283, 33284, 33292, 33299, 33300, 33309, 33310, 33311, 33314, 33315, 33317, 33318, 33330, 33337, 33338, 33339, 33341, 33342, 33343, 33344, 33346, 33347, 33349, 33350, 33353, 33354, 33358, 33359, 33363, 33366, 33367, 33371, 33374, 33375, 33377, 33378, 33380, 33381, 33382, 33388, 33389, 33393, 33396, 33397, 33400, 33401, 33402, 33403, 33404, 33405, 33412, 33416, 33421, 33436, 33437, 33441, 33442, 33444, 33445, 33447, 33448, 33451, 33452, 33462, 33463, 33464, 33471, 33472, 33478, 33491, 33500, 33501, 33502, 33508, 33515, 33516, 33520, 33541, 33549, 33559, 33561, 33581, 33586, 33629, 33639, 33646, 33654, 33655, 33656, 33670, 33684, 33685, 33711, 33712, 33714, 33717, 33718, 33722, 33727, 33729, 33739, 33745, 33755, 33756, 33760, 33761, 33772, 33830, 33831, 33848, 33866, 33895, 33987, 33995, 34014, 34016, 34040, 34041, 34050, 34054, 34078, 34086, 34087, 34094, 34095, 34107, 34155, 34161, 34171, 34173, 34194, 34223, 34233, 34238, 34239, 34240, 34254, 34264, 34269, 34281, 34290, 34296, 34297, 34300, 34301, 34305, 34311, 34313, 34315, 34316, 34317, 34318, 34323, 34338, 34340, 34344, 34345, 34346, 34352, 34353, 34357, 34364, 34370, 34371, 34372, 34373, 34374, 34375, 34376, 34379, 34380, 34381, 34382, 34390, 34391, 34401, 34402, 34412, 34423, 34432, 34433, 34435, 34436, 34437, 34439, 34444, 34445, 34449, 34453, 34464, 34465, 34467, 34468, 34469, 34484, 34503, 34505, 34506, 34510, 34526, 34531, 34532, 34533, 34534, 34548, 34562, 34568, 34569, 34571, 34594, 34595, 34600, 34601, 34606, 34609, 34613, 34614, 34630, 34631, 34633, 34634, 34643, 34712, 34726, 34774, 34793, 34799, 34835, 34852, 34853, 34899, 34905, 34918, 34929, 34930, 34949, 34952, 34959, 35021, 35022, 35023, 35025, 35036, 35114, 35116, 35123, 35150, 35175, 35176, 35189, 35214, 35266, 35267, 35309, 35310, 35376, 35377, 35378, 35453, 35455, 35463, 35464, 35465, 35466, 35592, 35593, 35604, 35605, 35625, 35649, 35659, 35664, 35666, 35667, 35668, 35669, 35670, 35695, 35700, 35720, 35733, 35749, 35756, 35757, 35759, 35764, 35768, 35774, 35775, 35781, 35790, 35803, 35809, 35827, 35833, 35882, 35887, 35910, 35912, 35924, 35925, 35933, 35950, 35956, 35957, 35958, 35984, 35987, 35988, 35993, 35995, 36001, 36009, 36032, 36034, 36037, 36056, 36065, 36070, 36079, 36117, 36118, 36179, 36180, 36184, 36198, 36202, 36212, 36215, 36217, 36218, 36219, 36220, 36231, 36232, 36233, 36234, 36235, 36236, 36866, 36898, 36905, 36908, 36909, 36913, 36917, 36923, 36924, 36932, 36935, 36938, 36939, 36940, 36997, 37000, 37020, 37021, 37128, 37168, 37169, 37191, 37224, 37225, 37229, 37230, 37231, 37233, 37268, 37273, 37319, 37362, 37363, 37428, 37440, 37446, 37453, 37464, 37571, 37671, 37748, 37755, 37775, 37802, 37828, 37878, 37978, 37983, 37989, 37990, 37995, 37996, 38002, 38013, 38014, 38015, 38016, 38020, 38023, 38025, 38026, 38029, 38032, 38035, 38045, 38050, 38059, 38064, 38072, 38082, 38104, 38130, 38132, 38170, 38183, 38184, 38207, 38271, 38297, 38298, 38305, 38306, 38307, 38308, 38318, 38331, 38332, 38361, 38362, 38389, 38390, 38401, 38402, 38410, 38413, 38420, 38421, 38442, 38454, 38455, 38460, 38461, 38472, 38473, 38488, 38497, 38501, 38507, 38511, 38513, 38534, 38535, 38594, 38621, 38680, 38718, 38766, 38767, 38783, 38812, 38813, 38822, 38828, 38831, 38899, 38903, 38908, 38909, 38912, 38914, 38920, 38998, 39065, 39071, 39072, 39073, 39078, 39079, 39082, 39083, 39084, 39101, 39109, 39110, 39122, 39128, 39133, 39134, 39138, 39139, 39164, 39171, 39174, 39179, 39182, 39246, 39248, 39251, 39269, 39274, 39282, 39291, 39318, 39353, 39385, 39386, 39388, 39391, 39434, 39492, 39509, 39519, 39522, 39524, 39525, 39526, 39527, 39530, 39535, 39541, 39542, 39544, 39561, 39577, 39586, 39588, 39664, 39686, 39697, 39702, 39705, 39716, 39762, 39764, 39777, 39797, 39826, 39880, 39900, 39904, 39909, 39910, 39919, 39920, 39935, 39963, 39973, 39975, 39978, 39980, 39981, 39988, 39991, 39996, 40042, 40073, 40074, 40083, 40086, 40088, 40095, 40096, 40097, 40098, 40112, 40136, 40161, 40163, 40179, 40180, 40181, 40183, 40184, 40185, 40205, 40206, 40217, 40218, 40221, 40222, 40230, 40236, 40237, 40253, 40266, 40275, 40403, 40404, 40424, 40426, 40427, 40428, 40429, 40430, 40449, 40471, 40479, 40599, 40615, 40638, 40639, 40641, 40658, 40660, 40695, 40697, 40698, 40704, 40738, 40742, 40773, 40806, 40862, 40898, 40899, 40900, 40901, 40904, 40957, 40958, 40960, 40964, 40968, 40996, 41002, 41003, 41004, 41009, 41023, 41024, 41027, 41028, 41029, 41030, 41050, 41072, 41107, 41109, 41134, 41137, 41145, 41149, 41322, 41323, 42370, 42371, 42375, 42376, 42711, 42712, 43734, 43837, 43838, 44444, 44445, 44570, 44571, 45078, 45079, 45579, 45580, 45998, 45999, 47778, 48023, 48024, 48180, 48411, 48412, 48489, 48490, 48542, 48543, 48567, 48589, 48603, 48669, 48706, 48719, 48767, 48823, 49276, 49311, 49367, 49396, 49558, 49578, 49666, 49818, 49920};
		int[] data7 = {52094, 52135, 53233, 53234, 53241, 53242, 53363, 53364, 53396, 53397, 53450, 53451, 53452, 53453, 53456, 54065, 54899, 54900, 54915, 54916, 54934, 54938, 54940, 54954, 55011, 55012, 55030, 55031, 55044, 55045, 55091, 55114, 55115, 55150, 55151, 55365, 55366, 55568, 55569, 55745, 55746, 55793, 55794, 55958, 56407, 56408, 56873, 56996, 57023, 57129, 57152, 57178, 57179, 57193, 57201, 57205, 57228, 57253, 57254, 57271, 57282, 57294, 57306, 57404, 57425, 57437, 57475, 57504, 57505, 57508, 57733, 57734, 57767, 57768, 57779, 57780, 57803, 57804, 57819, 57820, 57824, 57825, 57834, 57835, 58071, 58085, 58226, 58325, 58349, 58350, 58460, 58461, 58621, 58622, 59047, 59048, 59621, 59622, 59808, 59809};
		int[] data8 = {60288, 60289, 60565, 60566, 60604, 60605, 60969, 60970, 61383, 61384, 61536, 61537, 61641, 61642, 62036, 62037, 62307, 62308, 63080, 63081, 63181, 63210, 63211, 63212, 63213, 63220, 63221, 63308, 63454, 63455, 63523, 63524, 63565, 63566, 63579, 63580, 63598, 63599, 64503, 64504, 65070, 65071, 66102, 66103, 66130, 66178, 66473, 66474, 66557, 66558, 66672, 66673, 66864, 66865, 66885, 66886, 67070, 67098, 67099, 67261, 67262, 67352, 67353, 67374, 67375, 67641, 67642, 67990, 67991, 68333, 68334, 68532, 68533, 68718, 68719, 68806, 68905, 68906, 68909, 69028, 69161, 69316, 69317, 69402, 69599, 69600, 69601, 69603};
		int[] data9 = {70540, 70541, 71186, 71187, 71271, 71293, 71321, 71512, 71575, 71710, 71866, 72034, 72123, 72124, 72318, 72319, 72610, 72611, 72929, 72930, 72978, 72979, 73118, 73119, 73306, 73307, 73413, 73664, 73665, 73801, 73911, 73912, 73956, 73996, 73997, 74018, 74072, 74221, 74222, 74256, 74294, 74329, 75687, 75688, 75706, 75707, 75852, 75853, 75854, 75855, 75929, 75930, 76020, 76021, 76346, 76347, 76790, 76791, 76953, 76954, 77064, 77065, 77095, 77096, 77117, 77259, 77301, 77435, 77436, 77631, 77697, 77698, 77776, 77777, 77835, 77836, 77843, 77844, 77950, 77951, 78000, 78001, 78105, 78106, 78224, 78225, 78349, 78357, 78358, 78457, 78473, 78528, 78529, 78561, 78681, 78772, 78773, 79020, 79021, 79045, 79063, 79064, 79302, 79303, 79335, 79427, 79428, 79641};
		int[] data10 = {80861, 80862, 81873, 81874, 82144, 82269, 82960, 82961, 83362, 83363, 83586, 83587, 83676, 83677, 84119, 84120, 84588, 84589, 84659, 84660, 85272, 85273, 85303, 85304, 85350, 85367, 85621, 85622, 85668, 85669, 85765, 85766, 85789, 85790, 85851, 85852, 85871, 85872, 85914, 85915, 85916, 86529, 86530, 86886, 86887, 87452, 87453, 88504, 88505, 88749, 88750, 88935, 88936, 89038, 89039, 89254, 89255, 89355, 89356, 89639, 89640, 89707, 89708, 89709, 89765, 89766, 89805, 89806, 89828, 89947, 89948, 89997};
		int[] data11 = {90052, 90146, 90212, 90213, 90301, 90302, 90408, 90460, 90461, 90531, 90532, 91708, 91709, 91987, 91988, 94092, 94093, 94154, 94155, 94181, 94182, 94393, 94394, 94427, 94428, 95244, 95245, 95415, 95416, 95474, 95497, 95996, 96148, 96149, 96228, 96289, 96290, 96291, 96293, 96294, 96295, 96296, 96298, 96299, 96300, 96301, 96302, 96303, 96304, 96306, 96307, 96309, 96310, 96311, 96312, 96313, 96314, 96315, 96316, 96317, 96318, 96319, 96320, 96321, 96322, 96323, 96324, 96326, 96327, 96328, 96329, 96330, 96331, 96333, 96335, 96336, 96337, 96338, 96340, 96341, 96342, 96343, 96344, 96345, 96346, 96347, 96348, 96349, 96350, 96351, 96352, 97324, 97325, 97467, 97468, 97567, 97568, 97985, 97986, 98016, 98017, 98067, 98068, 98178, 98179, 98215, 98216, 98441, 99034, 99093, 99094, 99271, 99272, 99629, 99630, 99631, 99701, 99702, 99934};
		int[] data12 = {100406, 100417, 100475, 100476, 100487, 100488, 100516, 100517, 100578, 100601, 100705, 100706, 100717, 100718, 100744, 100745, 100787, 100788, 100946, 101001, 101062, 101063, 101402, 102084, 102085, 102185, 102186, 102315, 102316, 102730, 102731, 102811, 102812, 104064, 104065, 105987, 105988, 106133, 106134, 106163, 106164, 106261, 106262, 106497, 106498, 106546, 106632, 106633, 106906, 106907, 107094, 107095, 107174, 107175, 107498, 107499, 107600, 107601, 107690, 107850, 108001, 108091, 108147, 108148, 108446, 108447, 108847, 109087, 109138, 109139, 109539, 109540, 109559, 109560, 109793, 109794, 109954, 109955};
		int[] data13 = {110097, 110098, 110489, 110490, 111707, 111708, 111729, 111730, 111872, 111910, 111911, 111913, 111915, 111937, 111939, 111947, 111952, 112062, 112211, 112233, 112439, 112471, 112491, 112730, 112762, 112765, 112823, 112852, 112898, 112913, 112914, 112925, 112926, 112929, 113015, 113087, 113135, 113176, 113292, 113302, 113311, 113312, 113346, 113382, 114054, 114055, 114485, 114486, 115564, 115565, 116105, 116106, 116419, 116464, 116474, 116581, 116582, 116584, 116697, 116698, 116707, 116708, 116799, 116991, 116992, 116999, 117000, 117037, 117038, 117159, 117160, 117314, 117315, 117393, 117394, 117438, 117454, 117617, 117629, 117784, 118068, 118069, 118270, 118271, 119140, 119141, 119356, 119357, 119579, 119580, 119582, 119583, 119903, 119904};
		int[] data14 = {120390, 120391, 120416, 120417, 120599, 120600, 121053, 121054, 121091, 121092, 121125, 121126, 121505, 121506, 121552, 121553, 121554, 121555, 121556, 121613, 121669, 121670, 123000, 123001, 123334, 123335, 123515, 123516, 123764, 123765, 123777, 123778, 123809, 123810, 123811, 123812, 123816, 123817, 123821, 123822, 123904, 123922, 123927, 123932, 123949, 123950, 123956, 123958, 123985, 123986, 123997, 124008, 124009, 124012, 124013, 124035, 124036, 124046, 124047, 124058, 124059, 124061, 124062, 124066, 124067, 124075, 124076, 124080, 124081, 124083, 124084, 124089, 124090, 124091, 124092, 124093, 124098, 124099, 124100, 124101, 124177, 124178, 124234, 124235, 124238, 124239, 124244, 124245, 124248, 124249, 124250, 124251, 124261, 124262, 124287, 124288, 124289, 124298, 124299, 124302, 124303, 124316, 124317, 124339, 124340, 124350, 124351, 124354, 124355, 124366, 124367, 124401, 124402, 124411, 124412, 124413, 124414, 124415, 124416, 124417, 124418, 124419, 124420, 124421, 124422, 124438, 124439, 124442, 124443, 124445, 124446, 124453, 124454, 124455, 124456, 124462, 124463, 124481, 124482, 124483, 124484};
		
		List<Users> userList = new ArrayList<Users>();
		
		for(int i=0; i<data2.length; i++) {
			Users user = userDao.selectUserStrange(data2[i]);
			userList.add(user);
		}
		
		for(int i=0; i<data5.length; i++) {
			Users user = userDao.selectUserStrange(data5[i]);
			userList.add(user);
		}
		for(int i=0; i<data6.length; i++) {
			Users user = userDao.selectUserStrange(data6[i]);
			userList.add(user);
		}
		for(int i=0; i<data7.length; i++) {
			Users user = userDao.selectUserStrange(data7[i]);
			userList.add(user);
		}
		for(int i=0; i<data8.length; i++) {
			Users user = userDao.selectUserStrange(data8[i]);
			userList.add(user);
		}
		for(int i=0; i<data9.length; i++) {
			Users user = userDao.selectUserStrange(data9[i]);
			userList.add(user);
		}
		for(int i=0; i<data10.length; i++) {
			Users user = userDao.selectUserStrange(data10[i]);
			userList.add(user);
		}
		for(int i=0; i<data11.length; i++) {
			Users user = userDao.selectUserStrange(data11[i]);
			userList.add(user);
		}
		for(int i=0; i<data12.length; i++) {
			Users user = userDao.selectUserStrange(data12[i]);
			userList.add(user);
		}
		for(int i=0; i<data13.length; i++) {
			Users user = userDao.selectUserStrange(data13[i]);
			userList.add(user);
		}
		for(int i=0; i<data14.length; i++) {
			Users user = userDao.selectUserStrange(data14[i]);
			userList.add(user);
		}
		return userList;
	}
	
	
}
