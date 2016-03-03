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
	public static Student student; // �л� ��ü

	/* �ð�ǥ ���� ���� */
	public static Button scheduleBtns[] = new Button[615]; // ���� �迭 button
	public static Button scheduleBtn; // ���� ǥ��
	public static TextView totalnumtxt; // ���� ǥ�� text button
	public static ImageButton gradebtn;
	public static int totalnum; // ���� total
	public static int img_count; // �̹��� drawable count

	/* ��ٱ��� ���� */
	private View view1, view2; // ��ٱ��� view
	private ImageButton view1Btn, view2Btn; // ��ٱ��� �ø��� ������ ��ư
	private Animation aniShow, aniHide; // ��ٿ�� animation
	private boolean open = false; // ��ٱ��� open ����

	/* json ���� */
	private String jsonResult;
	private static String enrollurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_mobile.php?sid=";
	private String scheduleurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_slot_mobile.php?sid=";

	private ScheduleListAdapter ela;
	private static GradeListAdapter gla;

	private ListView enrolllistView; // �˻� list
	private static ListView gradelistView; // �˻� list

	public static Context context;

	private AlertDialog mDialog = null; // ���� Dialog
	private AlertDialog exitDialog = null; // ���� Dialog
	private AlertDialog gradeDialog = null; // ���� Dialog

	private static ArrayList<EnrollList> eList;
	private static EnrollList allEl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		// Thread �߰�
		if (android.os.Build.VERSION.SDK_INT > 9) {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);

		}

		context = this;
		totalnum = 0;
		img_count = 0;

		mDb = new DBAdapter(this); // DB �ҷ�����

		student = new Student();
		student = mDb.getStudentLogin();

		eList = new ArrayList<EnrollList>();
		gla = new GradeListAdapter(context, eList);

		// schedule ��ư �ʱ�ȭ
		// �ְ� ����
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
		// �߰� ����
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

		/* ��ٱ��� ������ ��ư �̺�Ʈ */
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

		/* ��ٱ��� �ø��� ��ư �̺�Ʈ */
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
	
	/* �߰��� ���� ��� ���� event*/
	public void onClickBtn(View v) {
		scheduleBtn = (Button) findViewById(v.getId());

		if (scheduleBtn.getTag(R.id.tag_first).equals("false")) {
			return;
		}

		mDialog = createInflaterDialog();
		mDialog.show();
	}

	/* �ð�ǥ ���� �˻� */
	public static int ScheduleCheck(String name, String room, int date) {

		int check = 0;

		// �ð��� �ߺ� �ɶ�
		if (scheduleBtns[date].getTag(R.id.tag_first).equals("true")) {

			Toast.makeText(context, "�ð��� �ߺ��˴ϴ�", Toast.LENGTH_SHORT).show();
			check = 1;

			return check;

		}

		return check;
	}

	/* �ð�ǥ�� ���� �߰� */
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
		totalnumtxt.setText("����:" + totalnum);

	}

	/* �ð�ǥ�� �ΰ��߰� */
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
		totalnumtxt.setText("����:" + totalnum);

	}

	public void onClickGradeBtn(View v) {
		gradeDialog = createInflaterDialog_grade();
		gradeDialog.show();
	}

	/* grade ���� Dialog */

	// grade �˶� ���� ���̾�α�
	private AlertDialog createInflaterDialog_grade() {
		final View innerView = getLayoutInflater().inflate(
				R.layout.dialog_layout_grade, null);

		gradelistView = (ListView) innerView.findViewById(R.id.grade_list_id);

		gradelistView.setAdapter(gla);
		gla.notifyDataSetChanged();

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("���� ���");
		ab.setView(innerView);

		ab.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});

		return ab.create();
	}

	/* ���� Dialog */

	// �˶� ���� ���̾�α�
	private AlertDialog createInflaterDialog() {
		final View innerView = getLayoutInflater().inflate(
				R.layout.dialog_layout_del, null);
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("���� ����");
		ab.setView(innerView);

		ab.setNegativeButton("Ȯ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				EnrollList el = (EnrollList) scheduleBtn
						.getTag(R.id.tag_second);

				String[] day = null; // time day date
				String week = null; // ����
				String time = null; // �ð�
				String date = null;

				day = el.getTime().split("/"); // ���� �ڸ���

				/* ���Ϻ� �ð� �ֱ� */
				for (int i = 0; i < day.length; i++) {

					for (int j = 0; j < day[i].length(); j++) {
						week = day[i].substring(0, 1);

						if (j > 0) {
							time = day[i].substring(j, 1 + j);

							// �߰����� ���ڷ� ����
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

				/* ���� DB�� ���� �ϱ� */
				/* ���� DB�� ���� */
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
						Toast.makeText(context, "������ ���� ����", Toast.LENGTH_SHORT)
								.show();

					} else if (res.equals("1")) {
						Toast.makeText(context, "������: " + el.getName() + " ����",
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					Toast.makeText(context, "Compare fail", Toast.LENGTH_SHORT)
							.show();
				}

				totalnum -= Integer.parseInt(el.getGrage());
				totalnumtxt.setText("����:" + totalnum);

			}
		});

		ab.setPositiveButton("���", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});

		return ab.create();
	}

	/* Back ��ư Event */
	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				// ���⿡ �ڷ� ��ư�� �������� �ؾ��� �ൿ�� �����Ѵ�
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

	// �˶� ���� ���̾�α�
	private AlertDialog exitInflaterDialog() {
		final View innerView = getLayoutInflater().inflate(
				R.layout.dialog_layout_exit, null);
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("KITime ����");
		ab.setView(innerView);

		ab.setNegativeButton("Ȯ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		ab.setPositiveButton("���", new DialogInterface.OnClickListener() {
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
	 * basket List (Basket List �Ľ� ) basket List �� �ѷ���
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
				String cName = jsonChildNode.optString("cName"); // �����
				String cType = jsonChildNode.optString("cType"); // ������ ����
				String cMajor = jsonChildNode.optString("cMajor"); // �а�
				String cYear = jsonChildNode.optString("cYear"); // �г�
				String cGrade = jsonChildNode.optString("cGrade"); // ����
				String cCode = jsonChildNode.optString("cCode"); // �������ڵ�
				String cDate = jsonChildNode.optString("cDate"); // ���ǽð�
				String cTime = jsonChildNode.optString("cTime"); // ���ǽð�
				String cRoom = jsonChildNode.optString("cRoom"); // ���ǽ�
				String cProf = jsonChildNode.optString("cProf"); // ����
				int cCurnum = jsonChildNode.optInt("cCurNum"); // �����ο�
				int cMaxnum = jsonChildNode.optInt("cMaxNum"); // �����ο�

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
	 * schedule �ð�ǥ ( slot 1 �Ľ� ) �ð�ǥ�� �ѷ���
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
				String cName = jsonChildNode.optString("cName"); // �����
				String cType = jsonChildNode.optString("cType"); // ������ ����
				String cMajor = jsonChildNode.optString("cMajor"); // �а�
				String cYear = jsonChildNode.optString("cYear"); // �г�
				String cGrade = jsonChildNode.optString("cGrade"); // ����
				String cCode = jsonChildNode.optString("cCode"); // �������ڵ�
				String cDate = jsonChildNode.optString("cDate"); // ���ǽð�
				String cTime = jsonChildNode.optString("cTime"); // ���ǽð�
				String cRoom = jsonChildNode.optString("cRoom"); // ���ǽ�
				String cProf = jsonChildNode.optString("cProf"); // ����
				int cCurnum = jsonChildNode.optInt("cCurNum"); // �����ο�
				int cMaxnum = jsonChildNode.optInt("cMaxNum"); // �����ο�

				EnrollList el = new EnrollList(eIndex, sIndex, cIndex, cName,
						cType, cMajor, cYear, cGrade, cCode, cDate, cTime,
						cRoom, cProf, cCurnum, cMaxnum);

				allEl = el;
				eList.add(allEl);

				// //////////////////
				String[] day = null; // time day date
				String week = null; // ����
				String time = null; // �ð�
				String date = null;

				day = el.getTime().split("/"); // ���� �ڸ���

				/* ���Ϻ� �ð� �ֱ� */
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

				// �ΰ��� ���
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
