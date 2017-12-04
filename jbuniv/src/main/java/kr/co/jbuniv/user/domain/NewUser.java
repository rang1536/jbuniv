package kr.co.jbuniv.user.domain;

public class NewUser {
	private int seqNo;
	private String studentId;
	private String name;
	private String nation;
	private String company;
	private String department;
	private String position;
	private String relation1;
	private String relation2;
	private String admission;
	private String graduated;
	private String zipCode;
	private String homeAddress;
	private String email;
	private String hp;
	private String mainPhone;
	private String homepage;
	private String homeSangseAdd;
	private String homePhone;
	
	public String getHomeSangseAdd() {
		return homeSangseAdd;
	}
	public void setHomeSangseAdd(String homeSangseAdd) {
		this.homeSangseAdd = homeSangseAdd;
	}
	public String getHomePhone() {
		return homePhone;
	}
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getRelation1() {
		return relation1;
	}
	public void setRelation1(String relation1) {
		this.relation1 = relation1;
	}
	public String getRelation2() {
		return relation2;
	}
	public void setRelation2(String relation2) {
		this.relation2 = relation2;
	}
	public String getAdmission() {
		return admission;
	}
	public void setAdmission(String admission) {
		this.admission = admission;
	}
	public String getGraduated() {
		return graduated;
	}
	public void setGraduated(String graduated) {
		this.graduated = graduated;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getHomeAddress() {
		return homeAddress;
	}
	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHp() {
		return hp;
	}
	public void setHp(String hp) {
		this.hp = hp;
	}
	public String getMainPhone() {
		return mainPhone;
	}
	public void setMainPhone(String mainPhone) {
		this.mainPhone = mainPhone;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	@Override
	public String toString() {
		return "NewUser [seqNo=" + seqNo + ", studentId=" + studentId + ", name=" + name + ", nation=" + nation
				+ ", company=" + company + ", department=" + department + ", position=" + position + ", relation1="
				+ relation1 + ", relation2=" + relation2 + ", admission=" + admission + ", graduated=" + graduated
				+ ", zipCode=" + zipCode + ", homeAddress=" + homeAddress + ", email=" + email + ", hp=" + hp
				+ ", mainPhone=" + mainPhone + ", homepage=" + homepage + "]";
	}

}
