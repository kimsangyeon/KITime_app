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
	private static final String TAG = "AppWidget";		//Widget �±�
	private Context context;

	private static final int WIDGET_UPDATE_INTERVAL = 30000;	// widget ������Ʈ ������
	private static PendingIntent mSender;		
	private static AlarmManager mManager;		// �˶� �޴���

	/* json ���� */
	private String jsonResult;

	private DBAdapter mDb = null; // DataBase
	public static Student student;

	private static String[][] schedule_array = new String[6][14];

	/* json ���� */

	// schedule slot url
	private static String scheduleurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_slot_mobile.php?sid=";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		mDb = new DBAdapter(context); // DB �ҷ�����

		student = new Student();	// �л� ��ü
		student = mDb.getStudentLogin();	// �л� ��ü ��񿡼� �޾ƿ���

		// scheduleurl�� �л���񿡼� �޾ƿ� �й� �Է�
		scheduleurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_slot_mobile.php?sid=";
		scheduleurl = scheduleurl + student.getNum() + "&slot=1";
		
		if (TextUtils.isEmpty(student.getNum())) {
			// ���� �ִ� ��� ó��
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
			//Toast.makeText(getApplicationContext(), "���ͳ� ���� ���¸� Ȯ�� ���ּ���", Toast.LENGTH_SHORT).show();
		}

		// ���� Ŭ������ ��ϵ� ��� ������ ����Ʈ�� ������
		appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(
				context, getClass()));
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}

	}

	
	/* ���� �ֱ��� ������Ʈ �Լ� */
	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK) - 1;	// ���� ��¥
		
		RemoteViews updateViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_schedule);

		int textcount = 0;

		// ������ ������ ���� �ѷ��ֱ� 
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
		// ���� ������Ʈ ����Ʈ�� �������� ��
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
		} // ���� ���� ����Ʈ�� �������� ��
		else if (action.equals("android.appwidget.action.APPWIDGET_DISABLED")) {
			Log.w(TAG, "android.appwidget.action.APPWIDGET_DISABLED");
			removePreviousAlarm();
		}
	}

	/**
	 * ����Ǿ��ִ� �˶��� ����մϴ�.
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
									.parseInt(time)] = " ����: " + el.getName()
									+ "\n ����: " + el.getDate() + "\n";
						}

					}
				}

			}

		} catch (JSONException e) {

			// Toast.makeText(getApplicationContext(), "�ش� ���� ����",
			// Toast.LENGTH_SHORT).show();
		}
	}
}
