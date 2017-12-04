package kr.co.jbuniv.user.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.jbuniv.user.domain.NewUser;
import kr.co.jbuniv.user.domain.TbUser;
import kr.co.jbuniv.user.domain.Users;

@Repository
public class UserDao {
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	//회원입력
	public int insertUser(Users user) {
		return sqlSession.insert("UserDao.insertUser", user);
	}
	
	//단과,대학원등 카테고리별 목록조회(100건)
	public List<Users> selectUsersByRelType(Map<String, Object> params){
		return sqlSession.selectList("UserDao.selectUsersByRelType", params);
	}
	
	//단과,대학원등 카테고리별 목록조회(전체)
	public List<Users> selectUsersByRelTypeAll(Map<String, Object> params){
		return sqlSession.selectList("UserDao.selectUsersByRelTypeAll", params);
	}
		
	//시퀀스번호로 유저조회
	public Users selectUserBySeqNo(int seqNo) {
		return sqlSession.selectOne("UserDao.selectUserBySeqNo", seqNo);
	}
	
	//정보수정
	public int updateUser(Users user) {
		return sqlSession.update("UserDao.updateUser", user);
	}

	// DB총카운트(어깨동무 -> 정제전체카운트)
	public int selectCountAll1() {
		return sqlSession.selectOne("UserDao.selectCountAll1");
	}
	
	// DB총카운트(발전지원부)
	public int selectCountAll2() {
		return sqlSession.selectOne("UserDao.selectCountAll2");
	}
	
	// 핸드폰번호 형식 일원화 > 폰번호 조회(1000건단위 퍼짐방지)
	public List<Users> selectHpForRefine(){
		return sqlSession.selectList("UserDao.selectHpForRefine");
	}
	
	// 핸드폰번호 형식 일원화 > 폰번호 조회(1000건단위 퍼짐방지)
	public List<Users> selectHpForRefine2(){
		return sqlSession.selectList("UserDao.selectHpRefine2");
	}
	// 핸드폰번호 형식 일원화 > 세팅된 값 업데이트
	public int updateHp(Users user) {
		return sqlSession.update("UserDao.updateHp", user);
	}
	
	// 핸드폰번호필드값 널 인 행들 조회
	public List<Users> selectHpIsNull(){
		return sqlSession.selectList("UserDao.selectHpIsNull");
	}
	
	// DB정제 > 발전지원부 범위데이터 조회
	public List<TbUser> selectDataSelected(Map<String,Integer> params){
		return sqlSession.selectList("UserDao.selectDataSelected", params);
	}
	
	// DB정제 > 발전지원부 데이터 기분 동명인 검색(어깨동무)
	public List<Users> selectUserByNameNRel(TbUser tbUser){
		return sqlSession.selectList("UserDao.selectUserByNameNRel", tbUser);
	}
	
	// 이상데이터 조회
	public Users selectUserStrange(int seqNo) {
		return sqlSession.selectOne("UserDao.selectUserStrange",seqNo);
	}
}
