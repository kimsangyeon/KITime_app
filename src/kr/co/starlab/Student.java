package kr.co.starlab;

public class Student {

	private int id;
	private int login; // �α��� ����
	private String sNum; // �й�
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	// �α����� ������
	public int getLogin() {
		return login;
	}

	// �α����� ����
	public void setLogin(int login) {
		this.login = login;
	}

	// �й� ������
	public String getNum() {
		return sNum;
	}

	// �й� ����
	public void setNum(String sNum) {
		this.sNum = sNum;
	}
}
