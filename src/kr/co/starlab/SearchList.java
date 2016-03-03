package kr.co.starlab;

public class SearchList {

	private int cIndex; // id
	private String cName; // �����
	private String cType; // ������ ����
	private String cMajor; // �а�
	private String cYear; // �г�
	private String cGrade; // ����
	private String cCode; // �������ڵ�
	private String cDate; // ��¥
	private String cTime; // ���ǽð�
	private String cRoom; // ���ǽ�
	private String cProf; // ����
	private int cMaxnum; // �����ο�

	// ��ü �ʱ�ȭ
	public SearchList(int cIndex, String cName, String cType, String cMajor,
			String cYear, String cGrade, String cCode, String cDate, String cTime,
			String cRoom, String cProf, int cMaxnum) {

		this.cIndex = cIndex;
		this.cName = cName;
		this.cType = cType;
		this.cMajor = cMajor;
		this.cYear = cYear;
		this.cGrade = cGrade;
		this.cCode = cCode;
		this.cDate = cDate;
		this.cTime = cTime;
		this.cRoom = cRoom;
		this.cProf = cProf;
		this.cMaxnum = cMaxnum;

	}

	// �󼼳��� ������
	public int getIndex() {
		return cIndex;
	}

	// �󼼳��� ����
	public void setIndex(int cIndex) {
		this.cIndex = cIndex;
	}

	// �󼼳��� ������
	public String getName() {
		return cName;
	}

	// �󼼳��� ����
	public void setName(String cName) {
		this.cName = cName;
	}

	// �󼼳��� ������
	public String getType() {
		return cType;
	}

	// �󼼳��� ����
	public void setType(String cType) {
		this.cType = cType;
	}

	// �󼼳��� ������
	public String getMajor() {
		return cMajor;
	}

	// �󼼳��� ����
	public void setMajor(String cMajor) {
		this.cMajor = cMajor;
	}

	// �󼼳��� ������
	public String getYear() {
		return cYear;
	}

	// �󼼳��� ����
	public void setYear(String cYear) {
		this.cYear = cYear;
	}

	// �󼼳��� ������
	public String getGrage() {
		return cGrade;
	}

	// �󼼳��� ����
	public void setGrade(String cGrade) {
		this.cGrade = cGrade;
	}

	// �󼼳��� ������
	public String getCode() {
		return cCode;
	}

	// �󼼳��� ����
	public void setCode(String cCode) {
		this.cCode = cCode;
	}

	// �󼼳��� ������
		public String getDate() {
			return cDate;
		}

		// �󼼳��� ����
		public void setDate(String cDate) {
			this.cDate = cDate;
		}

	
	// �󼼳��� ������
	public String getTime() {
		return cTime;
	}

	// �󼼳��� ����
	public void setTime(String cTime) {
		this.cTime = cTime;
	}

	// �󼼳��� ������
	public String getRoom() {
		return cRoom;
	}

	// �󼼳��� ����
	public void setRoom(String cRoom) {
		this.cRoom = cRoom;
	}

	// �󼼳��� ������
	public String getProf() {
		return cProf;
	}

	// �󼼳��� ����
	public void setProf(String cProf) {
		this.cProf = cProf;
	}

	// �󼼳��� ������
	public int getMaxnum() {
		return cMaxnum;
	}

	// �󼼳��� ����
	public void setMaxnum(int cMaxnum) {
		this.cMaxnum = cMaxnum;
	}

}
