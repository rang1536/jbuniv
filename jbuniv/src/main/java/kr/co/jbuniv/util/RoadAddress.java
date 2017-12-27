package kr.co.jbuniv.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.jbuniv.user.domain.Users;
import kr.co.jbuniv.user.service.UserService;

public class RoadAddress {
	
	@Autowired
	UserService userService;
	
	public void changeAddress(String currentPage,
			String countPerPage,
			String resultType,
			String confmKey,
			String keyword,
			int startNum,
			int endNum) throws Exception {
		System.out.println("값확인 : "+startNum+", "+endNum);
		
		
		//대상조회
		List<Users> list = userService.readUserAddServ(startNum, endNum);
		
		for(int i=0; i<list.size(); i++) {
			keyword = list.get(i).getHomeAddress().trim();
			
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
	    	
	    	System.out.println(i+" 번째 도로명주소확인 : "+data.getJSONObject("results").getJSONArray("juso").getJSONObject(0).get("roadAddr"));
		}
		
	}
}
