package kr.co.starlab;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

public class LogoActivity extends Activity implements Runnable {

	private Handler mHandler = null;
	private DBAdapter mDb = null; // DataBase

	public Student student;

	// private LinearLayout mLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);

		mDb = new DBAdapter(this); // DB �ҷ�����
		student = new Student();

		student = mDb.getStudentLogin();

		if (TextUtils.isEmpty(student.getNum())) {
			// ���� �ִ� ��� ó��
			student.setLogin(0);
			student.setNum(null);

			mDb.insertStudent(student);

		}

		ConnectivityManager connect = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		if (

		connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
				|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
						.getState() == NetworkInfo.State.CONNECTED) {

			mHandler = new Handler();
			mHandler.postDelayed(this, 1500);

		} else {
			Toast.makeText(getApplicationContext(), "���ͳ� ���� ���¸� Ȯ�� ���ּ���", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void run() {

		if (student.getLogin() == 1) {
			Intent i = new Intent(this, TabActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.fade, R.anim.hold);
		} else {
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.fade, R.anim.hold);
		}

		finish();
	}

}
