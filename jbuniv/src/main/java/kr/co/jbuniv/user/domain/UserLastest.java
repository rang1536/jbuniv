package kr.co.jbuniv.user.domain;

public class UserLastest {
	private int seqNo;
	private String name;
	private String relation1;
	private String graduated;
	private String etc3;
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelation1() {
		return relation1;
	}
	public void setRelation1(String relation1) {
		this.relation1 = relation1;
	}
	public String getGraduated() {
		return graduated;
	}
	public void setGraduated(String graduated) {
		this.graduated = graduated;
	}
	public String getEtc3() {
		return etc3;
	}
	public void setEtc3(String etc3) {
		this.etc3 = etc3;
	}
	@Override
	public String toString() {
		return "UserLastest [seqNo=" + seqNo + ", name=" + name + ", relation1=" + relation1 + ", graduated="
				+ graduated + ", etc3=" + etc3 + "]";
	}
	
	
}
