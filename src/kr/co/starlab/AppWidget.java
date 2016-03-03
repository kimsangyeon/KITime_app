package kr.co.starlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AppWidget extends AppWidgetProvider {
	private static final String TAG = "AppWidget";		//Widget 태그
	private Context context;

	private static final int WIDGET_UPDATE_INTERVAL = 30000;	// widget 업데이트 딜레이
	private static PendingIntent mSender;		
	private static AlarmManager mManager;		// 알람 메니저

	/* json 변수 */
	private String jsonResult;

	private DBAdapter mDb = null; // DataBase
	public static Student student;

	private static String[][] schedule_array = new String[6][14];

	/* json 변수 */

	// schedule slot url
	private static String scheduleurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_slot_mobile.php?sid=";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		mDb = new DBAdapter(context); // DB 불러오기

		student = new Student();	// 학생 객체
		student = mDb.getStudentLogin();	// 학생 객체 디비에서 받아오기

		// scheduleurl에 학생디비에서 받아온 학번 입력
		scheduleurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_slot_mobile.php?sid=";
		scheduleurl = scheduleurl + student.getNum() + "&slot=1";
		
		if (TextUtils.isEmpty(student.getNum())) {
			// 값이 있는 경우 처리
			student.setLogin(0);
			student.setNum(null);

			mDb.insertStudent(student);

		}

		ConnectivityManager connect = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

		if (
		connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
				|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
						.getState() == NetworkInfo.State.CONNECTED) {
			accessDatabase(scheduleurl);
		} else {
			//Toast.makeText(getApplicationContext(), "인터넷 연결 상태를 확인 해주세요", Toast.LENGTH_SHORT).show();
		}

		// 현재 클래스로 등록된 모든 위젯의 리스트를 가져옴
		appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(
				context, getClass()));
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}

	}

	
	/* 위젯 주기적 업데이트 함수 */
	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK) - 1;	// 현재 날짜
		
		RemoteViews updateViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_schedule);

		int textcount = 0;

		// 위젯에 오늘의 강의 뿌려주기 
		for (int i = 0; i < schedule_array[dayofweek].length; i++) {
			if (schedule_array[dayofweek][i] != null) {
				updateViews.setTextViewText(R.id.current_course_txt_1
						+ textcount, schedule_array[dayofweek][i]);
				textcount++;
			}

		}
		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	}

	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String action = intent.getAction();
		// 위젯 업데이트 인텐트를 수신했을 때
		if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
			Log.w(TAG, "android.appwidget.action.APPWIDGET_UPDATE");
			removePreviousAlarm();
			long firstTime = System.currentTimeMillis();
			mSender = PendingIntent.getBroadcast(context, 0, intent, 0);
			mManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			mManager.set(AlarmManager.RTC, firstTime, mSender);

			firstTime = System.currentTimeMillis() + WIDGET_UPDATE_INTERVAL;
			mManager.set(AlarmManager.RTC, firstTime, mSender);
		} // 위젯 제거 인텐트를 수신했을 때
		else if (action.equals("android.appwidget.action.APPWIDGET_DISABLED")) {
			Log.w(TAG, "android.appwidget.action.APPWIDGET_DISABLED");
			removePreviousAlarm();
		}
	}

	/**
	 * 예약되어있는 알람을 취소합니다.
	 */
	public void removePreviousAlarm() {
		if (mManager != null && mSender != null) {
			mSender.cancel();
			mManager.cancel(mSender);
		}
	}

	private PendingIntent buildActivityIntent(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		return pi;
	}

	private RemoteViews buildViews(Context context) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_schedule);

		return views;
	}

	public void accessDatabase(String url) {
		JsonReadTaskSchedule task2 = new JsonReadTaskSchedule();

		task2.execute(new String[] { url });

	}

	/* JSONE OPJECT */
	/**
	 * json search url parser
	 **/

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
				Toast.makeText(context, "Error..." + e.toString(),
						Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			scheduleParser();
		}
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

						if (j == day[i2].length() - 1) {
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
							default:
								break;
							}

							schedule_array[Integer.parseInt(week)][Integer
									.parseInt(time)] = " 강의: " + el.getName()
									+ "\n 요일: " + el.getDate() + "\n";
						}

					}
				}

			}

		} catch (JSONException e) {

			// Toast.makeText(getApplicationContext(), "해당 과목 없음",
			// Toast.LENGTH_SHORT).show();
		}
	}
}
