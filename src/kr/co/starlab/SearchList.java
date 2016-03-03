package kr.co.starlab;

public class SearchList {

	private int cIndex; // id
	private String cName; // 과목명
	private String cType; // 교과목 종류
	private String cMajor; // 학과
	private String cYear; // 학년
	private String cGrade; // 학점
	private String cCode; // 교과목코드
	private String cDate; // 날짜
	private String cTime; // 강의시간
	private String cRoom; // 강의실
	private String cProf; // 교수
	private int cMaxnum; // 제한인원

	// 객체 초기화
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

	// 상세내용 보내기
	public int getIndex() {
		return cIndex;
	}

	// 상세내용 저장
	public void setIndex(int cIndex) {
		this.cIndex = cIndex;
	}

	// 상세내용 보내기
	public String getName() {
		return cName;
	}

	// 상세내용 저장
	public void setName(String cName) {
		this.cName = cName;
	}

	// 상세내용 보내기
	public String getType() {
		return cType;
	}

	// 상세내용 저장
	public void setType(String cType) {
		this.cType = cType;
	}

	// 상세내용 보내기
	public String getMajor() {
		return cMajor;
	}

	// 상세내용 저장
	public void setMajor(String cMajor) {
		this.cMajor = cMajor;
	}

	// 상세내용 보내기
	public String getYear() {
		return cYear;
	}

	// 상세내용 저장
	public void setYear(String cYear) {
		this.cYear = cYear;
	}

	// 상세내용 보내기
	public String getGrage() {
		return cGrade;
	}

	// 상세내용 저장
	public void setGrade(String cGrade) {
		this.cGrade = cGrade;
	}

	// 상세내용 보내기
	public String getCode() {
		return cCode;
	}

	// 상세내용 저장
	public void setCode(String cCode) {
		this.cCode = cCode;
	}

	// 상세내용 보내기
		public String getDate() {
			return cDate;
		}

		// 상세내용 저장
		public void setDate(String cDate) {
			this.cDate = cDate;
		}

	
	// 상세내용 보내기
	public String getTime() {
		return cTime;
	}

	// 상세내용 저장
	public void setTime(String cTime) {
		this.cTime = cTime;
	}

	// 상세내용 보내기
	public String getRoom() {
		return cRoom;
	}

	// 상세내용 저장
	public void setRoom(String cRoom) {
		this.cRoom = cRoom;
	}

	// 상세내용 보내기
	public String getProf() {
		return cProf;
	}

	// 상세내용 저장
	public void setProf(String cProf) {
		this.cProf = cProf;
	}

	// 상세내용 보내기
	public int getMaxnum() {
		return cMaxnum;
	}

	// 상세내용 저장
	public void setMaxnum(int cMaxnum) {
		this.cMaxnum = cMaxnum;
	}

}
