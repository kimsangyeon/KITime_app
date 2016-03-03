package kr.co.starlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity {

	private DBAdapter mDb = null; // DataBase
	public Student student;

	private ArrayAdapter<CharSequence> adspin_grade; // �г� array
	private ArrayAdapter<CharSequence> adspin_option; // �ɼ� array
	private ArrayAdapter<String> adspin_major; // �а� array

	private Spinner gradespin; // �г� spinner
	private Spinner optionspin; // �˻� �ɼ� spinner
	private Spinner majorspin; // �а� spninner

	/* json ���� */
	private String jsonResult;
	private String majorurl = "http://starlab.kumoh.ac.kr/~starlab/majorlist_mobile.php"; // �а�
	private static String searchurl = "http://starlab.kumoh.ac.kr/~starlab/showCourse_mobile.php?";
	private static String enrollurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_mobile.php?sid=";
	private static ArrayList<String> majorList = new ArrayList<String>(); // url

	private SearchListAdapter sla;
	private EnrollListAdapter ela;

	private static ArrayList<Major> mla = new ArrayList<Major>();

	private int majornum;

	private ImageButton view1Btn, view2Btn;

	private ListView searchlistView; // �˻� list
	private ListView enrolllistView; // �˻� list

	private ImageButton searchbtn; // �˻� ��ư
	private ImageButton downbtn; // scroll ��ư

	private boolean flag_search;
	private boolean flag_basket;

	private int position = 0;

	private String type = "t=";
	private String major = "&m=";
	private String year = "&y=";

	private AlertDialog exitDialog = null; // ���� Dialog

	private View view1, view2;
	private Animation aniShow, aniHide;
	public static boolean open = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		accessDatabase(majorurl); // �а� parsing

		mDb = new DBAdapter(this); // DB �ҷ�����
		student = new Student();
		student = mDb.getStudentLogin();

		flag_search = false;
		flag_search = false;

		// adapter �ʱ�ȭ
		adspin_grade = ArrayAdapter.createFromResource(getBaseContext(),
				R.array.grade, android.R.layout.simple_spinner_item);

		// drop layout ����
		adspin_grade
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// �г� spinner �ʱ�ȭ
		gradespin = (Spinner) findViewById(R.id.gradespin_id);
		gradespin.setPrompt("�г��� �����ϼ���");

		gradespin.setAdapter(adspin_grade);
		gradespin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
				// Toast.makeText(MainActivity.this,
				// adspin.getItem(position) + "�� ���� �߽��ϴ�.", 1).show();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		/* �˻� �ɼ� adapter */

		// adapter �ʱ�ȭ
		adspin_option = ArrayAdapter.createFromResource(getBaseContext(),
				R.array.option, android.R.layout.simple_spinner_item);

		// drop layout ����
		adspin_option
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// �˻� �ɼ� spinner �ʱ�ȭ
		optionspin = (Spinner) findViewById(R.id.optionspin_id);
		optionspin.setPrompt("�˻� �ɼ��� �����ϼ���.");

		optionspin.setAdapter(adspin_option);
		optionspin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
				// Toast.makeText(MainActivity.this,
				// adspin.getItem(position) + "�� ���� �߽��ϴ�.", 1).show();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		/* �а� adapter */
		// adapter �ʱ�ȭ
		adspin_major = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item, majorList);

		// drop layout ����
		adspin_major
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// �а� spinner �ʱ�ȭ
		majorspin = (Spinner) findViewById(R.id.majorspin_id);
		majorspin.setPrompt("�˻� �а��� �����ϼ���.");

		majorspin.setAdapter(adspin_major);
		majorspin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

				if (position != 0) {
					majornum = mla.get(position - 1).getCode();

				}
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		/* �а� adapter */
		majorList.add("�а�                 ");
		// mla.add(m);
		// adapter �ʱ�ȭ
		adspin_major = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item, majorList);

		// drop layout ����
		adspin_major
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// �а� spinner �ʱ�ȭ
		majorspin = (Spinner) findViewById(R.id.majorspin_id);
		majorspin.setPrompt("�˻� �а��� �����ϼ���.");

		majorspin.setAdapter(adspin_major);
		majorspin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
				// Toast.makeText(MainActivity.this,
				// adspin.getItem(position) + "�� ���� �߽��ϴ�.", 1).show();
				if (position != 0) {
					majornum = mla.get(position - 1).getCode();
					Log.v("@@", "majornum :" + majornum);

				}
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		/* �˻� List */
		searchlistView = (ListView) findViewById(R.id.search_list_id);
		enrolllistView = (ListView) findViewById(R.id.basket_searchlist_id);

		searchlistView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

		/* down Button */
		downbtn = (ImageButton) findViewById(R.id.downbtn);
		downbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				searchlistView.smoothScrollToPosition(searchlistView
						.getFirstVisiblePosition() + 8);

				searchlistView.setSelection(searchlistView
						.getFirstVisiblePosition() + 8);
			}
		});

		/* �˻� ��ư */
		searchbtn = (ImageButton) findViewById(R.id.searchbtn_id);
		searchbtn.setOnClickListener(new OnClickListener() {

			String search1 = "���񱸺�";
			String search2 = "��ü";
			String search3 = "�а�                 ";

			@Override
			public void onClick(View v) {

				flag_search = true;

				// t= ? type ����
				switch (optionspin.getSelectedItem().toString()) {
				case "���񱸺�":
					break;
				case "��������":
					type = type.concat("1");
					break;
				case "MSC":
					type = type.concat("2");
					break;
				case "����":
					type = type.concat("3");
					break;
				case "����":
					type = type.concat("4");
					break;
				case "������":
					type = type.concat("5");
					break;
				}

				// m=? major ����
				if (search3.equals(majorspin.getSelectedItem().toString())) {
					major = "&m=";
				} else {
					major = major.concat(Integer.toString(majornum));
				}

				// y=? year ����
				switch (gradespin.getSelectedItem().toString()) {
				case "1�г�":
					year = year.concat("1");
					break;
				case "2�г�":
					year = year.concat("2");
					break;
				case "3�г�":
					year = year.concat("3");
					break;
				case "4�г�":
					year = year.concat("4");
					break;
				default:
					break;
				}

				searchurl = searchurl + type + major + year;

				Log.v("searchurl", "searchurl : " + searchurl);

				accessDatabase(searchurl);

				searchurl = "http://starlab.kumoh.ac.kr/~starlab/showCourse_mobile.php?";
				type = "t=";
				major = "&m=";
				year = "&y=";

				position = 0;
			}
		});

		/**
		 * shop Slide Menu
		 * */

		view1 = findViewById(R.id.searchview1);
		view2 = findViewById(R.id.searchview2);

		view2.setVisibility(View.GONE);
		aniShow = AnimationUtils.loadAnimation(this, R.anim.left_in);
		aniHide = AnimationUtils.loadAnimation(this, R.anim.left_out);

		/* ��ٱ��� ������ ��ư �̺�Ʈ */
		view2Btn = (ImageButton) findViewById(R.id.searchview2Btn);
		view2Btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "View2 Btn Clicked",
				// Toast.LENGTH_SHORT).show();
				view2.startAnimation(aniHide);
				view2.setVisibility(View.GONE);
				view1Btn.setClickable(true);
				downbtn.setVisibility(View.VISIBLE);
				downbtn.setClickable(true);
				searchbtn.setClickable(true);

				/* List View ��ũ�� �츮�� */
				searchlistView.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						int act = event.getAction();
						switch (act & MotionEvent.ACTION_MASK) {
						case MotionEvent.ACTION_DOWN:
							break;
						case MotionEvent.ACTION_MOVE:
							break;
						case MotionEvent.ACTION_UP:

							break;
						case MotionEvent.ACTION_POINTER_UP:

							break;
						case MotionEvent.ACTION_POINTER_DOWN:
							break;
						case MotionEvent.ACTION_CANCEL:
							break;
						default:
							break;
						}

						return false;
					}
				});

				open = false;
			}
		});

		/* ��ٱ��� �ø��� ��ư �̺�Ʈ */
		view1Btn = (ImageButton) findViewById(R.id.searchview1Btn);
		view1Btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (!open) {
					view2.setVisibility(View.VISIBLE);
					view2.startAnimation(aniShow);
					view1Btn.setClickable(false);
					downbtn.setVisibility(View.INVISIBLE);
					downbtn.setClickable(false);
					searchbtn.setClickable(false);

					enrollurl = enrollurl + student.getNum();

					accessDatabase(enrollurl);

					Log.v("@", "enrollurl: " + enrollurl);

					enrollurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_mobile.php?sid=";

					/* List View ��ũ�� ���̱� */
					searchlistView.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							int act = event.getAction();
							switch (act & MotionEvent.ACTION_MASK) {
							case MotionEvent.ACTION_DOWN:
								break;
							case MotionEvent.ACTION_MOVE:

								event.setAction(MotionEvent.ACTION_CANCEL);

								break;
							case MotionEvent.ACTION_UP:

								break;
							case MotionEvent.ACTION_POINTER_UP:

								break;
							case MotionEvent.ACTION_POINTER_DOWN:
								break;
							case MotionEvent.ACTION_CANCEL:
								break;
							default:
								break;
							}

							return false;
						}
					});

					open = true;
				} else {
					view2.startAnimation(aniHide);
					view2.setVisibility(View.GONE);
					view1Btn.setClickable(false);
					downbtn.setVisibility(View.INVISIBLE);
					downbtn.setClickable(false);
					searchbtn.setClickable(false);
					open = false;
				}
			}
		});

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
					downbtn.setVisibility(View.VISIBLE);
					downbtn.setClickable(true);
					searchbtn.setClickable(true);

					/* List View ��ũ�� �츮�� */
					searchlistView.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							int act = event.getAction();
							switch (act & MotionEvent.ACTION_MASK) {
							case MotionEvent.ACTION_DOWN:
								break;
							case MotionEvent.ACTION_MOVE:
								break;
							case MotionEvent.ACTION_UP:

								break;
							case MotionEvent.ACTION_POINTER_UP:

								break;
							case MotionEvent.ACTION_POINTER_DOWN:
								break;
							case MotionEvent.ACTION_CANCEL:
								break;
							default:
								break;
							}

							return false;
						}
					});

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
		JsonReadTask task1 = new JsonReadTask();
		JsonReadTaskSearch task2 = new JsonReadTaskSearch();
		JsonReadTaskBasket task3 = new JsonReadTaskBasket();

		if (url == majorurl) {
			task1.execute(new String[] { url });
		} else if (url == searchurl) {
			task2.execute(new String[] { url });
		} else if (url == enrollurl) {
			task3.execute(new String[] { url });
		}
	}

	private class JsonReadTask extends AsyncTask<String, Void, String> {
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
			majorParser();
		}
	}

	private class JsonReadTaskSearch extends AsyncTask<String, Void, String> {
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
			searchParser();
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

	/**
	 * search List (Search List �Ľ� ) saerch List �� �ѷ���
	 * 
	 * **/
	// -----------------------------------------------------
	public void searchParser() {

		ArrayList<SearchList> searchList = new ArrayList<SearchList>();

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("Course");

			for (int i = 0; i < jsonMainNode.length() + 1; i++) {

				if (jsonMainNode.length() == i) {
					
					SearchList sl = new SearchList(0, null, null,
							null, null, null, null, null, null, null,
							null, 0);

					searchList.add(sl);

					sl = searchList.get(i);
					
					
					
				} else {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
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
					int cMaxnum = jsonChildNode.optInt("cMaxNum"); // �����ο�

					SearchList sl = new SearchList(cIndex, cName, cType,
							cMajor, cYear, cGrade, cCode, cDate, cTime, cRoom,
							cProf, cMaxnum);

					searchList.add(sl);

					sl = searchList.get(i);
				}
			}

		} catch (JSONException e) {

			Toast.makeText(getApplicationContext(), "�ش� ���� ����",
					Toast.LENGTH_SHORT).show();
		}

		sla = new SearchListAdapter(getApplicationContext(), searchList);
		searchlistView.setAdapter(sla);
		sla.notifyDataSetChanged();
	}

	/**
	 * �а� (Major �Ľ� ) spinner�� �ѷ���
	 * 
	 * **/
	// -----------------------------------------------------
	public void majorParser() {
		// List<Map<String, String>> employeeList = new ArrayList<Map<String,
		// String>>();

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("Course");

			for (int i = 0; i < jsonMainNode.length(); i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				Major m = new Major(i + 1, jsonChildNode.optString("cMajor"),
						jsonChildNode.optInt("cMajorcode"));

				mla.add(m);

				String name = jsonChildNode.optString("cMajor");

				majorList.add(name);

			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_SHORT).show();
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

		ela = new EnrollListAdapter(getApplicationContext(), enrollList);
		enrolllistView.setAdapter(ela);
		ela.notifyDataSetChanged();
	}

}
