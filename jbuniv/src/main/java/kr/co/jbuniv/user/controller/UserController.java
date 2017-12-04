package kr.co.jbuniv.user.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.jbuniv.user.domain.Users;
import kr.co.jbuniv.user.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/addUser", method = RequestMethod.GET)
	public String addUserCtrl() {
		/*System.out.println("유저등록");*/
		return "insert/add_user";
	}
	
	@RequestMapping(value="/addUserPost", method = RequestMethod.POST)
	public String addUserPostCtrl(Users user) {
		/*System.out.println("유저등록 포스트요청");
		System.out.println("폼값확인 : "+user);*/
		int result = userService.addUserServ(user);
		/*System.out.println("입력성공 여부확인 : "+result);*/
		return "index";
	}
	
	@RequestMapping(value="/userList", method = RequestMethod.GET)
	public String userListCtrl(Model model,
			@RequestParam(value="relationType")int relationType,
			@RequestParam(value="relation1")String relation1,
			@RequestParam(value="startNum", defaultValue="0")int startNum,
			@RequestParam(value="type", defaultValue="none")String type) {
		System.out.println("키값확인 : "+relationType + ", "+relation1+", "+startNum);
		
		model.addAttribute("relationType", relationType);
		model.addAttribute("relation1", relation1);
		model.addAttribute("startNum", startNum);
		model.addAttribute("type", type);
		
		return "list/user_list";
	}
	
	@RequestMapping(value="/modifyUser", method = RequestMethod.GET)
	public String modifyUserCtrl(Model model,
			@RequestParam(value="seqNo")int seqNo,
			@RequestParam(value="type", defaultValue="1")int type) {
		System.out.println("유저시퀀스 넘버확인 : "+seqNo);
		Users user = userService.readUserBySeqNo(seqNo, type);
		/*System.out.println("조회된 유저정보 확인 : "+user);*/
		
		model.addAttribute("user", user);
		return "update/update_user";
	}
		
	@RequestMapping(value="/dataRefine", method = RequestMethod.GET)
	public String dataRefineCtrl() {
		/*System.out.println("유저등록");*/
		return "data/data_refine";
	}
	
	//db정제 포스트요청!
	@RequestMapping(value="/dbRefine", method = RequestMethod.POST)
	public String dbRefineCtrl(Model model, 
			@RequestParam(value="startNum")int startNum,
			@RequestParam(value="endNum", defaultValue="0")int endNum) {
		System.out.println("시작 : "+startNum+",  끝 : "+endNum);
		
		Map<String, Object> map = userService.dbRefineServ(startNum, endNum);
		
		model.addAttribute("updateCount", map.get("updateCount"));
		model.addAttribute("noUpdateCount", map.get("noUpdateCount"));
		model.addAttribute("failList", map.get("failList"));
		model.addAttribute("totalCount", (endNum-startNum)+1);
		
		return "data/refine_list";
	}
	
	@RequestMapping(value="/addressChange", method = RequestMethod.GET)
	public String addChangeCtrl() {
		/*System.out.println("유저등록");*/
		return "apiSampleJSON";
	}
	
	//strangeList
	@RequestMapping(value="/strangeList", method = RequestMethod.GET)
	public String strangeListCtrl() {
		/*System.out.println("유저등록");*/
		return "list/strange_list";
	}
	
	@RequestMapping(value="/sample/getAddrApi.do", method = RequestMethod.POST)
	public String addChangeCtrl(@RequestParam(value="currentPage")String currentPage,
			@RequestParam(value="countPerPage")String countPerPage,
			@RequestParam(value="resultType")String resultType,
			@RequestParam(value="confmKey")String confmKey,
			@RequestParam(value="keyword")String keyword) {
		// OPEN API 호출 URL 정보 설정
		
		String apiUrl = null;
		try {
			apiUrl = "http://www.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+currentPage+"&countPerPage="+countPerPage+"&keyword="+URLEncoder.encode(keyword,"UTF-8")+"&confmKey="+confmKey+"&resultType="+resultType;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URL url = null;
		try {
			url = new URL(apiUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader br = null;
    	try {
			br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	StringBuffer sb = new StringBuffer();
    	String tempStr = null;
    	
    	while(true){
    		try {
				tempStr = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if(tempStr == null) break;
    		sb.append(tempStr);								// 응답결과 JSON 저장
    	}
    	try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println(sb.toString());
    	
		return sb.toString();
	}
}
