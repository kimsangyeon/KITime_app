package kr.co.starlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleActivity extends Activity {

	private DBAdapter mDb = null; // DataBase
	public static Student student; // 학생 객체

	/* 시간표 관련 변수 */
	public static Button scheduleBtns[] = new Button[615]; // 강의 배열 button
	public static Button scheduleBtn; // 강의 표시
	public static TextView totalnumtxt; // 학점 표시 text button
	public static ImageButton gradebtn;
	public static int totalnum; // 학점 total
	public static int img_count; // 이미지 drawable count

	/* 장바구니 변수 */
	private View view1, view2; // 장바구니 view
	private ImageButton view1Btn, view2Btn; // 장바구니 올리기 내리기 버튼
	private Animation aniShow, aniHide; // 장바우기 animation
	private boolean open = false; // 장바구니 open 여부

	/* json 변수 */
	private String jsonResult;
	private static String enrollurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_mobile.php?sid=";
	private String scheduleurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_slot_mobile.php?sid=";

	private ScheduleListAdapter ela;
	private static GradeListAdapter gla;

	private ListView enrolllistView; // 검색 list
	private static ListView gradelistView; // 검색 list

	public static Context context;

	private AlertDialog mDialog = null; // 삭제 Dialog
	private AlertDialog exitDialog = null; // 종료 Dialog
	private AlertDialog gradeDialog = null; // 종료 Dialog

	private static ArrayList<EnrollList> eList;
	private static EnrollList allEl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		// Thread 추가
		if (android.os.Build.VERSION.SDK_INT > 9) {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);

		}

		context = this;
		totalnum = 0;
		img_count = 0;

		mDb = new DBAdapter(this); // DB 불러오기

		student = new Student();
		student = mDb.getStudentLogin();

		eList = new ArrayList<EnrollList>();
		gla = new GradeListAdapter(context, eList);

		// schedule 버튼 초기화
		// 주간 수업
		for (int i = 0; i < 5; i++) {
			scheduleBtns[11 + i * 10] = (Button) findViewById(R.id.scheduleid_11
					+ i);
			scheduleBtns[11 + i * 10].setTag(R.id.tag_first, "false");

		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[12 + i * 10] = (Button) findViewById(R.id.scheduleid_12
					+ i);
			scheduleBtns[12 + i * 10].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[13 + i * 10] = (Button) findViewById(R.id.scheduleid_13
					+ i);
			scheduleBtns[13 + i * 10].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[14 + i * 10] = (Button) findViewById(R.id.scheduleid_14
					+ i);
			scheduleBtns[14 + i * 10].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[15 + i * 10] = (Button) findViewById(R.id.scheduleid_15
					+ i);
			scheduleBtns[15 + i * 10].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[16 + i * 10] = (Button) findViewById(R.id.scheduleid_16
					+ i);
			scheduleBtns[16 + i * 10].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[17 + i * 10] = (Button) findViewById(R.id.scheduleid_17
					+ i);
			scheduleBtns[17 + i * 10].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[18 + i * 10] = (Button) findViewById(R.id.scheduleid_18
					+ i);
			scheduleBtns[18 + i * 10].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[19 + i * 10] = (Button) findViewById(R.id.scheduleid_19
					+ i);
			scheduleBtns[19 + i * 10].setTag(R.id.tag_first, "false");
		}
		// 야간 수업
		for (int i = 0; i < 5; i++) {
			scheduleBtns[110 + i * 100] = (Button) findViewById(R.id.scheduleid_110
					+ i);
			scheduleBtns[110 + i * 100].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[111 + i * 100] = (Button) findViewById(R.id.scheduleid_111
					+ i);
			scheduleBtns[111 + i * 100].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[112 + i * 100] = (Button) findViewById(R.id.scheduleid_112
					+ i);
			scheduleBtns[112 + i * 100].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[113 + i * 100] = (Button) findViewById(R.id.scheduleid_113
					+ i);
			scheduleBtns[113 + i * 100].setTag(R.id.tag_first, "false");
		}
		for (int i = 0; i < 5; i++) {
			scheduleBtns[114 + i * 100] = (Button) findViewById(R.id.scheduleid_114
					+ i);
			scheduleBtns[114 + i * 100].setTag(R.id.tag_first, "false");
		}

		for (int i = 0; i < 5; i++) {
			scheduleBtns[115 + i * 100] = (Button) findViewById(R.id.scheduleid_115
					+ i);
			scheduleBtns[115 + i * 100].setTag(R.id.tag_first, "false");
		}

		totalnumtxt = (TextView) findViewById(R.id.totalnumtxt);
		gradebtn = (ImageButton) findViewById(R.id.gradebtn_id);
		// //////////////////////////////////////////////////
		accessDatabase(scheduleurl);

		// //////////////////////////////////////////////////
		enrolllistView = (ListView) findViewById(R.id.basket_schedulelist_id);

		view1 = findViewById(R.id.scheduleview1);
		view2 = findViewById(R.id.scheduleview2);

		view2.setVisibility(View.GONE);
		aniShow = AnimationUtils.loadAnimation(this, R.anim.left_in);
		aniHide = AnimationUtils.loadAnimation(this, R.anim.left_out);

		/* 장바구니 내리는 버튼 이벤트 */
		view2Btn = (ImageButton) findViewById(R.id.scheduleview2Btn);
		view2Btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "View2 Btn Clicked",
				// Toast.LENGTH_SHORT).show();
				view2.startAnimation(aniHide);
				view2.setVisibility(View.GONE);
				view1Btn.setClickable(true);
				open = false;
			}
		});

		/* 장바구니 올리는 버튼 이벤트 */
		view1Btn = (ImageButton) findViewById(R.id.scheduleview1Btn);
		view1Btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!open) {
					view2.setVisibility(View.VISIBLE);
					view2.startAnimation(aniShow);
					view1Btn.setClickable(false);
					open = true;

					enrollurl = enrollurl + student.getNum();

					accessDatabase(enrollurl);

					enrollurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_mobile.php?sid=";

				} else {
					view2.startAnimation(aniHide);
					view2.setVisibility(View.GONE);
					view1Btn.setClickable(false);
					open = false;
				}
			}
		});

	}
	
	/* 추가된 강의 목록 보기 event*/
	public void onClickBtn(View v) {
		scheduleBtn = (Button) findViewById(v.getId());

		if (scheduleBtn.getTag(R.id.tag_first).equals("false")) {
			return;
		}

		mDialog = createInflaterDialog();
		mDialog.show();
	}

	/* 시간표 강의 검사 */
	public static int ScheduleCheck(String name, String room, int date) {

		int check = 0;

		// 시간이 중복 될때
		if (scheduleBtns[date].getTag(R.id.tag_first).equals("true")) {

			Toast.makeText(context, "시간이 중복됩니다", Toast.LENGTH_SHORT).show();
			check = 1;

			return check;

		}

		return check;
	}

	/* 시간표에 강의 추가 */
	public static void ScheduleInput(String name, String room, int grade,
			int date, EnrollList el) {

		boolean check = false;

		for (int i = 0; i < eList.size(); i++) {
			if (eList.get(i) == el) {
				check = true;
				break;
			}
		}

		if (check == false) {
			eList.add(el);
		}

		scheduleBtns[date].setText(name + " " + room);
		scheduleBtns[date].setBackgroundResource(R.drawable.scheduleinput_1
				+ img_count);
		scheduleBtns[date].setTag(R.id.tag_first, "true");
		scheduleBtns[date].setTag(R.id.tag_second, el);

		totalnum += grade / grade;
		totalnumtxt.setText("학점:" + totalnum);

	}

	/* 시간표에 인강추가 */
	public static void ScheduleInputInternet(String name, int grade,
			EnrollList el) {

		boolean check = false;

		for (int i = 0; i < eList.size(); i++) {
			if (eList.get(i) == el) {
				check = true;
				break;
			}
		}

		if (check == false) {
			eList.add(el);
		}
		totalnum += grade;
		totalnumtxt.setText("학점:" + totalnum);

	}

	public void onClickGradeBtn(View v) {
		gradeDialog = createInflaterDialog_grade();
		gradeDialog.show();
	}

	/* grade 삭제 Dialog */

	// grade 알람 삭제 다이어로그
	private AlertDialog createInflaterDialog_grade() {
		final View innerView = getLayoutInflater().inflate(
				R.layout.dialog_layout_grade, null);

		gradelistView = (ListView) innerView.findViewById(R.id.grade_list_id);

		gradelistView.setAdapter(gla);
		gla.notifyDataSetChanged();

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("강의 목록");
		ab.setView(innerView);

		ab.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});

		return ab.create();
	}

	/* 삭제 Dialog */

	// 알람 삭제 다이어로그
	private AlertDialog createInflaterDialog() {
		final View innerView = getLayoutInflater().inflate(
				R.layout.dialog_layout_del, null);
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("강의 제거");
		ab.setView(innerView);

		ab.setNegativeButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				EnrollList el = (EnrollList) scheduleBtn
						.getTag(R.id.tag_second);

				String[] day = null; // time day date
				String week = null; // 요일
				String time = null; // 시간
				String date = null;

				day = el.getTime().split("/"); // 요일 자르기

				/* 요일별 시간 넣기 */
				for (int i = 0; i < day.length; i++) {

					for (int j = 0; j < day[i].length(); j++) {
						week = day[i].substring(0, 1);

						if (j > 0) {
							time = day[i].substring(j, 1 + j);

							// 야간수업 숫자로 변경
							switch (time) {
							case "A":
								time = "10";
								break;
							case "B":
								time = "11";
								break;
							case "C":
								time = "12";
								break;
							case "D":
								time = "13";
								break;
							case "E":
								time = "14";
								break;
							case "F":
								time = "15";
								break;
							default:
								break;
							}

							date = week + time;

							scheduleBtns[Integer.parseInt(date)].setText("");
							scheduleBtns[Integer.parseInt(date)]
									.setBackgroundResource(R.drawable.schedulebasic);
							scheduleBtns[Integer.parseInt(date)].setTag(
									R.id.tag_first, "false");
							scheduleBtns[Integer.parseInt(date)].setTag(
									R.id.tag_second, null);
						}
					}
				}
				eList.remove(el);
				img_count--;

				/* 서버 DB에 삭제 하기 */
				/* 서버 DB에 삭제 */
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair("sid", student
						.getNum()));

				postParameters.add(new BasicNameValuePair("cid", Integer
						.toString(el.getIndex())));

				postParameters.add(new BasicNameValuePair("slot", "4"));

				String response = null;

				try {
					Log.v("post", "post : " + postParameters.toString()); // sid

					response = CustomHttpClient
							.executeHttpPost(
									"http://starlab.kumoh.ac.kr/~starlab/enroll_slot_proc.php",
									postParameters);

					String res = response.toString();

					res = res.substring(res.length() - 2, res.length() - 1);
					
					if (res.equals("0")) {
						Toast.makeText(context, "교과목 삭제 실패", Toast.LENGTH_SHORT)
								.show();

					} else if (res.equals("1")) {
						Toast.makeText(context, "교과목: " + el.getName() + " 삭제",
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					Toast.makeText(context, "Compare fail", Toast.LENGTH_SHORT)
							.show();
				}

				totalnum -= Integer.parseInt(el.getGrage());
				totalnumtxt.setText("학점:" + totalnum);

			}
		});

		ab.setPositiveButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});

		return ab.create();
	}

	/* Back 버튼 Event */
	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				// 여기에 뒤로 버튼을 눌렀을때 해야할 행동을 지정한다
				if (open == true) {
					view2.startAnimation(aniHide);
					view2.setVisibility(View.GONE);
					view1Btn.setClickable(true);
					open = false;
					return true;
				} else {
					exitDialog = exitInflaterDialog();
					exitDialog.show();
				}
			}
		}
		return super.onKeyDown(KeyCode, event);
	}

	// 알람 종료 다이어로그
	private AlertDialog exitInflaterDialog() {
		final View innerView = getLayoutInflater().inflate(
				R.layout.dialog_layout_exit, null);
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("KITime 종료");
		ab.setView(innerView);

		ab.setNegativeButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		ab.setPositiveButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});

		return ab.create();
	}

	/* JSONE OPJECT */
	/**
	 * json search url parser
	 **/

	public void accessDatabase(String url) {
		JsonReadTaskBasket task1 = new JsonReadTaskBasket();
		JsonReadTaskSchedule task2 = new JsonReadTaskSchedule();

		if (url == enrollurl) {
			task1.execute(new String[] { url });
		} else if (url == scheduleurl) {
			url = url + student.getNum() + "&slot=1";
			task2.execute(new String[] { url });
		}
	}

	private class JsonReadTaskBasket extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			try {
				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				return jsonResult;
			} catch (HttpHostConnectException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				// e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			enrollParser();
		}
	}

	private class JsonReadTaskSchedule extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			try {
				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				return jsonResult;
			} catch (HttpHostConnectException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				// e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			scheduleParser();
		}
	}

	/**
	 * basket List (Basket List 파싱 ) basket List 에 뿌려줌
	 * 
	 * **/
	// -----------------------------------------------------
	public void enrollParser() {

		ArrayList<EnrollList> enrollList = new ArrayList<EnrollList>();

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("Enroll");

			for (int i = 0; i < jsonMainNode.length(); i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				int eIndex = jsonChildNode.optInt("eIndex");
				int sIndex = jsonChildNode.optInt("sIndex");
				int cIndex = jsonChildNode.optInt("cIndex");
				String cName = jsonChildNode.optString("cName"); // 과목명
				String cType = jsonChildNode.optString("cType"); // 교과목 종류
				String cMajor = jsonChildNode.optString("cMajor"); // 학과
				String cYear = jsonChildNode.optString("cYear"); // 학년
				String cGrade = jsonChildNode.optString("cGrade"); // 학점
				String cCode = jsonChildNode.optString("cCode"); // 교과목코드
				String cDate = jsonChildNode.optString("cDate"); // 강의시간
				String cTime = jsonChildNode.optString("cTime"); // 강의시간
				String cRoom = jsonChildNode.optString("cRoom"); // 강의실
				String cProf = jsonChildNode.optString("cProf"); // 교수
				int cCurnum = jsonChildNode.optInt("cCurNum"); // 제한인원
				int cMaxnum = jsonChildNode.optInt("cMaxNum"); // 제한인원

				EnrollList el = new EnrollList(eIndex, sIndex, cIndex, cName,
						cType, cMajor, cYear, cGrade, cCode, cDate, cTime,
						cRoom, cProf, cCurnum, cMaxnum);

				enrollList.add(el);

				el = enrollList.get(i);

			}

		} catch (JSONException e) {

		}

		ela = new ScheduleListAdapter(getApplicationContext(), enrollList);
		enrolllistView.setAdapter(ela);
		ela.notifyDataSetChanged();
	}

	/**
	 * schedule 시간표 ( slot 1 파싱 ) 시간표에 뿌려줌
	 * 
	 * **/
	// -----------------------------------------------------
	public void scheduleParser() {

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("Enroll");

			for (int i = 0; i < jsonMainNode.length(); i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				int eIndex = jsonChildNode.optInt("eIndex");
				int sIndex = jsonChildNode.optInt("sIndex");
				int cIndex = jsonChildNode.optInt("cIndex");
				String cName = jsonChildNode.optString("cName"); // 과목명
				String cType = jsonChildNode.optString("cType"); // 교과목 종류
				String cMajor = jsonChildNode.optString("cMajor"); // 학과
				String cYear = jsonChildNode.optString("cYear"); // 학년
				String cGrade = jsonChildNode.optString("cGrade"); // 학점
				String cCode = jsonChildNode.optString("cCode"); // 교과목코드
				String cDate = jsonChildNode.optString("cDate"); // 강의시간
				String cTime = jsonChildNode.optString("cTime"); // 강의시간
				String cRoom = jsonChildNode.optString("cRoom"); // 강의실
				String cProf = jsonChildNode.optString("cProf"); // 교수
				int cCurnum = jsonChildNode.optInt("cCurNum"); // 제한인원
				int cMaxnum = jsonChildNode.optInt("cMaxNum"); // 제한인원

				EnrollList el = new EnrollList(eIndex, sIndex, cIndex, cName,
						cType, cMajor, cYear, cGrade, cCode, cDate, cTime,
						cRoom, cProf, cCurnum, cMaxnum);

				allEl = el;
				eList.add(allEl);

				// //////////////////
				String[] day = null; // time day date
				String week = null; // 요일
				String time = null; // 시간
				String date = null;

				day = el.getTime().split("/"); // 요일 자르기

				/* 요일별 시간 넣기 */
				for (int i2 = 0; i2 < day.length; i2++) {

					for (int j = 0; j < day[i2].length(); j++) {
						week = day[i2].substring(0, 1);

						if (j > 0) {
							time = day[i2].substring(j, 1 + j);

							switch (time) {
							case "A":
								time = "10";
								break;
							case "B":
								time = "11";
								break;
							case "C":
								time = "12";
								break;
							case "D":
								time = "13";
								break;
							case "E":
								time = "14";
								break;
							case "F":
								time = "15";
								break;
							default:
								break;
							}

							date = week + time;

							String[] room = el.getRoom().split("/");

							ScheduleInput(el.getName(), room[i2],
									Integer.parseInt(el.getGrage()),
									Integer.parseInt(date), el);

						}
					}
				}

				// 인강일 경우
				if (el.getDate().length() == 0) {
					ScheduleInputInternet(el.getName(),
							Integer.parseInt(el.getGrage()), el);
				}

				img_count++;
				// ////////////////////////////
			}

		} catch (JSONException e) {

		}
	}

}
