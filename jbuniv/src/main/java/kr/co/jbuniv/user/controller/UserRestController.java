package kr.co.jbuniv.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.jbuniv.user.domain.Users;
import kr.co.jbuniv.user.service.UserService;

@RestController
public class UserRestController {
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="userListAjax", method = RequestMethod.POST)
	public Map<String,Object> userListAjaxCtrl(@RequestParam(value="relationType")int relationType,
			@RequestParam(value="relation1")String relation1,
			@RequestParam(value="startNum", defaultValue="0")int startNum,
			@RequestParam(value="type", defaultValue="none")String type){
		System.out.println("AJAX 넘어온 값 확인 : "+relationType+", "+relation1+", 시작인덱스 : "+startNum); 
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
		System.out.println("유저수정 포스트요청");
		System.out.println("폼값확인 : "+user);
		int result = userService.modifyUserServ(user);
		System.out.println("입력성공 여부확인 : "+result);
		
		Map<String, String> map = new HashMap<String, String>();
		
		if(result == 0 ) map.put("result", "fail");
		else if(result > 0) map.put("result", "success");
		
		return map;
	}
	
	@RequestMapping(value="strangeListAjax", method = RequestMethod.POST)
	public Map<String,Object> strangeListAjaxCtrl(){
		List<Users> userList = userService.splitData();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", userList);
		
		return map;
	}
}
