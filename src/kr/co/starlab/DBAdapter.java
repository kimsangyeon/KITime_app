package kr.co.starlab;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQLite3 데이터베이스에 연결하기 위한 어댑터 클래스
public class DBAdapter extends SQLiteOpenHelper {
	private SQLiteDatabase db;

	public static final int ALL = -1;

	// DB이름
	private static final String DB_NAME = "Student.db";
	private static final int VERSION = 1;

	public DBAdapter(Context context) {
		super(context, DB_NAME, null, VERSION);
		db = this.getWritableDatabase();
	}

	@Override
	public synchronized void close() {
		db.close();
		super.close();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	// DB table 생성
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createQuery = "CREATE TABLE student "
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "   Login INTEGER ," + "   sNum VARCHAR(30)); ";
		db.execSQL(createQuery);
	}

	// 객체 반환
	public Student getStudentLogin() {
		Student ret = new Student();
		String query = "select * from student;";

		Cursor c = db.rawQuery(query, null);

		if (c.moveToFirst()) {
			
			final int idxId = c.getColumnIndex("id");
			final int idxLogin = c.getColumnIndex("Login");
			final int idxNum = c.getColumnIndex("sNum");

			Student s = new Student();
			
			s.setId(c.getInt(idxId));
			s.setLogin(c.getInt(idxLogin));
			s.setNum(c.getString(idxNum));

			ret = s;
		}

		c.close();

		return ret;
	}

	// DB 정보 저장
	public void insertStudent(Student student) {

		String insertQuery = ""
				+ "INSERT INTO student"
				+ "(Login, sNum)"
				+ "VALUES" + "( '"
				+ student.getLogin()
				+ "' , '"
				+ student.getNum()
			    + "');";

		db.execSQL(insertQuery);
	}

	// DB 정보 업데이트
	public void updateStudent(Student student) {

		String updateQuery = "" + " update student " + " set Login = '"
				+ Integer.toString(student.getLogin())+ "' , " + "sNum = '"
				+ student.getNum() + "';";

		db.execSQL(updateQuery);
	}

	// DB 정보 삭제
	public void deleteMorningCall(String sNum) {
		String updateQuery = "" + " delete from student "
				+ "where sNum = '" + sNum + "'.;";

		db.execSQL(updateQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		onCreate(db);
	}

}
