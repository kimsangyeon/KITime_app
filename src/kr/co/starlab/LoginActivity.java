package kr.co.starlab;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private DBAdapter mDb = null; // DataBase


	private EditText un;
	private EditText pw;
	private TextView error;

	private Student student;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mDb = new DBAdapter(this); // DB 불러오기
		student = new Student();

		student = mDb.getStudentLogin();	// 학생 객체 디비에서 받아오기

		un = (EditText) findViewById(R.id.et_un);
		pw = (EditText) findViewById(R.id.et_pw);
		error = (TextView) findViewById(R.id.tv_error);

		if (android.os.Build.VERSION.SDK_INT > 9) {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);

		}

	}

	/* 로그인 버튼 event*/
	public void onClickBtn(View v) {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		
		// ID
		postParameters
				.add(new BasicNameValuePair("id", un.getText().toString()));

		// PW
		postParameters.add(new BasicNameValuePair("pwd", pw.getText()
				.toString()));

		String response = null;

		Intent i = new Intent(getApplicationContext(), TabActivity.class);

		try {
			response = CustomHttpClient
					.executeHttpPost(
							"http://starlab.kumoh.ac.kr/~starlab/login_proc_mobile.php",
							postParameters);
			String res = response.toString();

			res = res.substring(res.length() - 2, res.length() - 1);

			if (res.equals("0")) {
				error.setText("Correct Username or Password");

				student.setLogin(1);
				student.setNum(un.getText().toString());

				mDb.updateStudent(student);

				startActivity(i);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				finish();

			} else if (res.equals("1"))
				error.setText("Sorry!! Incorrect Username or Password");
			else
				error.setText("compare fail!");

		} catch (Exception e) {
			error.setText(e.toString());
		}
	}

}
