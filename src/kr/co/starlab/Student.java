package kr.co.starlab;

public class Student {

	private int id;
	private int login; // 로그인 정보
	private String sNum; // 학번
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	// 로긴정보 보내기
	public int getLogin() {
		return login;
	}

	// 로긴정보 저장
	public void setLogin(int login) {
		this.login = login;
	}

	// 학번 보내기
	public String getNum() {
		return sNum;
	}

	// 학번 저장
	public void setNum(String sNum) {
		this.sNum = sNum;
	}
}
