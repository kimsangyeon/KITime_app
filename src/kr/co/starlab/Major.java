package kr.co.starlab;

// ÇÐ°ú °´Ã¼
public class Major {
	int index;
	String name;
	int code;
	
	public Major(int index, String name, int code){
		this.index = index;
		this.name = name;
		this.code = code;
	}

	String getName() {
		return this.name;
	}

	void setName(String name) {
		this.name = name;
	}

	int getCode() {
		return this.code;
		
	}

	void setCode(int code) {
		this.code = code;
	}

}
