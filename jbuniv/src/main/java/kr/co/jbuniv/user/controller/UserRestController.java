package kr.co.jbuniv.user.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.jbuniv.user.domain.Users;
import kr.co.jbuniv.user.service.UserService;
import kr.co.jbuniv.util.RoadAddress;

@RestController
public class UserRestController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="userListAjax", method = RequestMethod.POST)
	public Map<String,Object> userListAjaxCtrl(@RequestParam(value="relationType")int relationType,
			@RequestParam(value="relation1")String relation1,
			@RequestParam(value="startNum", defaultValue="0")int startNum,
			@RequestParam(value="type", defaultValue="none")String type){
		/*System.out.println("AJAX 넘어온 값 확인 : "+relationType+", "+relation1+", 시작인덱스 : "+startNum); */
		List<Users> userList = userService.readUsersByRelTypeServ(relationType, relation1, startNum, type);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", userList);
		map.put("startNum", startNum);
		map.put("relationType", relationType);
		map.put("relation1", relation1);
		return map;
	}
	
	@RequestMapping(value="/modifyUser", method = RequestMethod.POST)
	public Map<String, String> modifyUserCtrl(Model model, Users user) {
		/*System.out.println("유저수정 포스트요청");
		System.out.println("폼값확인 : "+user);*/
		int result = userService.modifyUserServ(user);
		/*System.out.println("입력성공 여부확인 : "+result);*/
		
		Map<String, String> map = new HashMap<String, String>();
		
		if(result == 0 ) map.put("result", "fail");
		else if(result > 0) map.put("result", "success");
		
		return map;
	}
	
	@RequestMapping(value="strangeListAjax", method = RequestMethod.POST)
	public Map<String,Object> strangeListAjaxCtrl(@RequestParam(value="startNum")int startNum){
		List<Users> userList = userService.splitData(startNum);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", userList);
		
		return map;
	}
	
	@RequestMapping(value="deleteData", method = RequestMethod.POST)
	public Map<String,Object> deleteDataCtrl(@RequestParam(value="seqNo")int seqNo){
		int result = userService.removeDataServ(seqNo);		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(result == 1) {
			map.put("check", "delete");
		}else if(result == 0) {
			map.put("check", "fail");
		}
		return map;
	}
	
	@RequestMapping(value="modifyData", method = RequestMethod.POST)
	public Map<String,Object> modifyDataCtrl(@RequestParam(value="seqNo")int seqNo,
			@RequestParam(value="seqNo2")int seqNo2){
		
		int result = userService.modifyDataServ(seqNo, seqNo2);
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(result == 1) {
			map.put("check", "delete");
		}else if(result == 0) {
			map.put("check", "fail");
		}
		return map;
	}
	
	//주소변환
	@RequestMapping(value="getAddrApi", method = RequestMethod.POST)
	public Map<String, Object> addChangeCtrl(@RequestParam(value="currentPage")String currentPage,
			@RequestParam(value="countPerPage")String countPerPage,
			@RequestParam(value="resultType")String resultType,
			@RequestParam(value="confmKey")String confmKey,
			@RequestParam(value="keyword",defaultValue="none")String keyword,
			@RequestParam(value="startNum", defaultValue="1")int startNum,
			@RequestParam(value="endNum", defaultValue="1")int endNum) throws Exception {
		// OPEN API 호출 URL 정보 설정
		
		//대상조회
		List<Users> list = userService.readUserAddServ(startNum, endNum);
		List<Integer> failList = new ArrayList<Integer>();
		List<Integer> wrongList = new ArrayList<Integer>();
		int success = 0;
		int searchCount = 0; //집주소
		int searchCount2 = 0; //회사주소
		int newAddCount = 0; //이미 신주소
		
		for(int i=0; i<list.size(); i++) {
			keyword = list.get(i).getHomeAddress().trim();
			if(keyword.contains("\\(") && keyword.contains("\\)")) {
				System.out.println("해당주소는 신주소입니다.");
				System.out.println("");
				newAddCount++;
			}else if(keyword.length() < 10){
				System.out.println(i+" 번째 주소는 잘못된주소입니다");
	    		System.out.println("");
	    		wrongList.add(list.get(i).getSeqNo());
			}else {
				String apiUrl = "http://www.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+currentPage+"&countPerPage="+countPerPage+"&keyword="+URLEncoder.encode(keyword,"UTF-8")+"&confmKey="+confmKey+"&resultType="+resultType;
				URL url = new URL(apiUrl);
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
				StringBuffer sb = new StringBuffer();
		    	
		    	while(true){
		    		String tempStr = br.readLine();
					
		    		if(tempStr == null) break;
		    		sb.append(tempStr);								// 응답결과 JSON 저장
		    	}
		    	br.close();		
		    	
		    	JSONObject data = new JSONObject(sb.toString());
		    	
		    	System.out.println(i+" 번째 집주소 도로명주소 확인중");
		    	System.out.println("");
		    	
		    	if(data.getJSONObject("results").getJSONArray("juso").length() > 0) {
		    		/*System.out.println(i+" 번째 도로명주소확인 : "+data.getJSONObject("results").getJSONArray("juso").getJSONObject(0).get("roadAddr"));*/
		    		System.out.println("***도로명주소 검색에 성공했습니다");
		    		System.out.println("***주소확인 : "+data.getJSONObject("results").getJSONArray("juso").getJSONObject(0).get("roadAddr"));
		    		System.out.println("");
		    		
		    		list.get(i).setHomeRoadAdd(data.getJSONObject("results").getJSONArray("juso").getJSONObject(0).get("roadAddr").toString());
		    		list.get(i).setZipCode(data.getJSONObject("results").getJSONArray("juso").getJSONObject(0).get("zipNo").toString());
		    		searchCount++;
		    	}else if(data.getJSONObject("results").getJSONArray("juso").length() == 0){
		    		System.out.println(i+" 번째 주소는 잘못된주소입니다");
		    		System.out.println("");
		    		wrongList.add(list.get(i).getSeqNo());
		    	}
			}
			
	    	
	    	if(list.get(i).getComAddress() != null && list.get(i).getComAddress() != "") {
	    		keyword = list.get(i).getComAddress().trim();
				
	    		if(keyword.contains("\\(") && keyword.contains("\\)")) {
					System.out.println("해당주소는 신주소입니다.");
					System.out.println("");
					
				}else if(keyword.length() < 10){
					System.out.println(i+" 번째 주소는 잘못된주소입니다");
		    		System.out.println("");		    		
				}else {
					String apiUrl = "http://www.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+currentPage+"&countPerPage="+countPerPage+"&keyword="+URLEncoder.encode(keyword,"UTF-8")+"&confmKey="+confmKey+"&resultType="+resultType;
					URL url = new URL(apiUrl);
					BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
					StringBuffer sb = new StringBuffer();
			    	
			    	while(true){
			    		String tempStr = br.readLine();
						
			    		if(tempStr == null) break;
			    		sb.append(tempStr);								// 응답결과 JSON 저장
			    	}
			    	br.close();		
			    	
			    	JSONObject data = new JSONObject(sb.toString());
			    	
			    	System.out.println(i+" 번째 회사주소 도로명주소 확인중");
			    	System.out.println("");
			    	
			    	if(data.getJSONObject("results").getJSONArray("juso").length() > 0) {
			    		System.out.println("***도로명주소 검색에 성공했습니다");
			    		System.out.println("***주소확인 : "+data.getJSONObject("results").getJSONArray("juso").getJSONObject(0).get("roadAddr"));
			    		System.out.println("");
			    		
			    		list.get(i).setComRoadAdd(data.getJSONObject("results").getJSONArray("juso").getJSONObject(0).get("roadAddr").toString());
			    		list.get(i).setComZipCode(data.getJSONObject("results").getJSONArray("juso").getJSONObject(0).get("zipNo").toString());
			    		searchCount2++;
			    	}else if(data.getJSONObject("results").getJSONArray("juso").length() == 0){
			    		System.out.println(i+" 번째 주소는 잘못된주소입니다");
			    		System.out.println("");
			    	}
				}			
		    } //회사주소 조건문 끝
	    	
	    	int result = 0;
	    	if(searchCount != 0 || searchCount2 !=0) {
	    		System.out.println("***주소를 갱신하겠습니다...");
		    	System.out.println("");
		    	
	    		result = userService.modifyAddToRoadAddServ(list.get(i));
	    		if(result == 1) {
	    			System.out.println("***주소를 도로명주소로 변경하였습니다");
	    			System.out.println("");
	    			if(searchCount != 0) success++;   			
	    		}
	    		else if(result == 0) {
	    			System.out.println("***주소 변경에 실패하였습니다");
	    			System.out.println("");
	    			failList.add(list.get(i).getSeqNo());
	    		}
	    	}
	    	
	    	searchCount = 0;
	    	searchCount2 = 0;
		} //반복문 끝
		System.out.println("===변환결과====================================================");
		System.out.println("***총 조회데이터 수 : "+list.size());
		System.out.println("***성공 : "+success);
		System.out.println("***실패( "+failList.size()+" )건 : ");
		System.out.println(failList);
		System.out.println("***잘못된 주소 ( "+wrongList.size()+" )건 : ");
		System.out.println(wrongList);
		System.out.println("원래 신주소 : "+newAddCount);
		System.out.println("============================================================");
		newAddCount = 0;
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", success);
		map.put("fail", failList.size());
		map.put("wrong", wrongList.size());
		map.put("startNum", endNum+1);
		map.put("total", list.size());
		return map;
		
	}
	
	@RequestMapping(value="/test", method = RequestMethod.POST)
	public String testCtrlAjax() {
		System.out.println("컨트롤러 왔다~!!");
		return "success";
	}
}
