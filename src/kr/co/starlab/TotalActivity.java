package kr.co.starlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class TotalActivity extends Activity {

	/* FACEBOOK ���� */
	/*****************************************/
	private static final List<String> PERMISSIONS = Arrays.asList(
			"publish_stream", "user_website", "user_status", "user_about_me");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	private static final String TAG = "TotalActivity";

	private final String PENDING_ACTION_BUNDLE_KEY = "kr.co.starlab:pendingAction";
	private LoginButton loginButton;
	private PendingAction pendingAction = PendingAction.NONE;
	private ViewGroup controlsContainer;
	private GraphUser user;
	private GraphPlace place;
	private List<GraphUser> tags;
	private boolean canPresentShareDialog;
	private boolean canPresentShareDialogWithPhotos;

	/******************************************/

	private DBAdapter mDb = null; // DataBase
	public static Student student;

	private ListView totallistView; // �˻� list
	private TotalListAdapter tla;

	/* json ���� */
	private String jsonResult;
	private static String totalurl;

	private ImageButton shareButton = null;

	private AlertDialog exitDialog = null; // ���� Dialog

	/* FACEBOOK open source code */
	/******************************************/
	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);

		}
	};

	/* onCreate */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDb = new DBAdapter(this); // DB �ҷ�����

		student = new Student();
		student = mDb.getStudentLogin();

		totalurl = "http://starlab.kumoh.ac.kr/~starlab/showEnroll_slot_mobile.php?sid=";
		totalurl = totalurl + student.getNum();

		accessDatabase(totalurl);

		// ///////////FACEBOOK////////////////
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);

			pendingPublishReauthorization = savedInstanceState.getBoolean(
					PENDING_PUBLISH_KEY, false);

		}

		setContentView(R.layout.widget_schedule);

		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(R.layout.activity_total, null);
		this.addContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		totallistView = (ListView) findViewById(R.id.total_list_id);

		loginButton = (LoginButton) findViewById(R.id.authButton);
		loginButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						TotalActivity.this.user = user;
					}
				});

		// session state call back event
		loginButton.setSessionStatusCallback(new Session.StatusCallback() {
			@SuppressWarnings("deprecation")
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {
					shareButton.setVisibility(View.VISIBLE);
					Request.executeMeRequestAsync(session,
							new Request.GraphUserCallback() {
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {
										// �α��� ���� (user�� ������ �������.)
										// Log.i(TAG,"User ID "+ user.getId());
										// Log.i(TAG,"Email "+
										// user.asMap().get("email"));
									}
								}
							});
				} else {
					shareButton.setVisibility(View.INVISIBLE);
				}
			}
		});

		// Can we present the share dialog for regular links?
		canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
		// Can we present the share dialog for photos?
		canPresentShareDialogWithPhotos = FacebookDialog.canPresentShareDialog(
				this, FacebookDialog.ShareDialogFeature.PHOTOS);

		shareButton = (ImageButton) findViewById(R.id.shareButton);
		shareButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*
				 * if (!isLogined()) { Toast.makeText(getApplicationContext(),
				 * "�α����ϼ���.", Toast.LENGTH_SHORT).show(); return; }
				 */
				publishFeedDialog();

			}
		});

		if (isLogined())
			shareButton.setVisibility(View.VISIBLE);
		else
			shareButton.setVisibility(View.INVISIBLE);

	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			shareButton.setVisibility(View.VISIBLE);
			if (pendingPublishReauthorization
					&& state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				pendingPublishReauthorization = false;
				publishFeedDialog();
			}

		} else if (state.isClosed()) {
			shareButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		AppEventsLogger.activateApp(this);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {
					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {
						Log.e("Activity",
								String.format("Error: %s", error.toString()));
					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
						Log.i("Activity", "Success!");
					}
				});
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@SuppressWarnings("unused")
	private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
		return new FacebookDialog.ShareDialogBuilder(this)
				.setName("Hello Facebook")
				.setDescription(
						"The 'Hello Facebook' sample application showcases simple Facebook integration")
				.setLink("http://developers.facebook.com/android");
	}

	// �α��� ���� Ȯ��
	boolean isLogined() {
		Session session = Session.getActiveSession();
		if (session == null)
			return false;

		if (!session.isOpened())
			return false;

		return true;
	}

	/* Back ��ư Event */
	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				// ���⿡ �ڷ� ��ư�� �������� �ؾ��� �ൿ�� �����Ѵ�
				exitDialog = exitInflaterDialog();
				exitDialog.show();
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

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", "KITime");
		params.putString("caption", "�ݿ��������б� �л����� ���� ���������û.");
		params.putString("description",
				"KITime �ݿ����� ���� ������û ���α׷� : Application, Web ����  [made by: starlab].");
		params.putString("link", "http://starlab.kumoh.ac.kr/~starlab/");
		params.putString("picture",
				"https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this,
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(getApplicationContext(),
										"Kitime�� facebook�� ���� �Ǿ����ϴ�.",
										Toast.LENGTH_LONG).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(getApplicationContext(),
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							// Generic, ex: network error
							Toast.makeText(getApplicationContext(),
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();
	}

	@SuppressWarnings("unused")
	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	/******************************************/

	// Comparator �� �����.
	private final static Comparator<EnrollList> myComparator = new Comparator<EnrollList>() {

		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(EnrollList object1, EnrollList object2) {

			if (object1.getMaxnum() != 0 || object2.getMaxnum() != 0) {

				if (object1.getIndex() != 0 && object2.getIndex() != 0) {

					return (100 / (object1.getMaxnum() / object1.getCurnum()) > 100 / (object2
							.getMaxnum() / object2.getCurnum()) ? -1 : 1);
				}
			}
			return (object1.getMaxnum() > object2.getMaxnum() ? -1 : 1);
		}
	};

	/* JSONE OPJECT */
	/**
	 * json search url parser
	 **/

	public void accessDatabase(String url) {
		JsonReadTaskTotal task1 = new JsonReadTaskTotal();
		url = url + "&slot=1";
		task1.execute(new String[] { url });

	}

	private class JsonReadTaskTotal extends AsyncTask<String, Void, String> {
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
			totalParser();
		}
	}

	/**
	 * total ��� list�� �ѷ��ش�
	 * 
	 * **/
	// -----------------------------------------------------
	public void totalParser() {

		ArrayList<EnrollList> eList = new ArrayList<EnrollList>();

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("Enroll");

			for (int i = 0; i < jsonMainNode.length() + 1; i++) {

				if (jsonMainNode.length() == i) {

					EnrollList el = new EnrollList(0, 0, 0, null, null, null,
							null, null, null, null, null, null, null, 0, 0);

					eList.add(el);

					// el = eList.get(i);

				} else {
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

					if (cMaxnum != 0) {

						EnrollList el = new EnrollList(eIndex, sIndex, cIndex,
								cName, cType, cMajor, cYear, cGrade, cCode,
								cDate, cTime, cRoom, cProf, cCurnum, cMaxnum);

						eList.add(el);
					}

				}
			}

		} catch (JSONException e) {

		}

		Collections.sort(eList, myComparator);

		tla = new TotalListAdapter(getApplicationContext(), eList);
		totallistView.setAdapter(tla);
		tla.notifyDataSetChanged();
	}

}
