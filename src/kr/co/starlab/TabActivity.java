package kr.co.starlab;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

@SuppressWarnings("deprecation")
public class TabActivity extends android.app.TabActivity implements
		OnTabChangeListener {

	private DBAdapter mDb = null; // DataBase
	public Student student;

	private MenuInflater inflater;
	private Menu menu;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mDb = new DBAdapter(this); // DB 불러오기
		student = new Student();

		student = mDb.getStudentLogin();

		final TabHost tabHost = getTabHost();

		tabHost.setOnTabChangedListener(this);
		tabHost.setBackgroundColor(Color.WHITE);

		tabHost.addTab(tabHost
				.newTabSpec("Schedule")
				.setIndicator("시간표",
						getResources().getDrawable(R.drawable.searchtab))
				.setContent(
						new Intent(this, ScheduleActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		tabHost.addTab(tabHost
				.newTabSpec("Search")
				.setIndicator("검색",
						getResources().getDrawable(R.drawable.scheduletab))
				.setContent(new Intent(this, SearchActivity.class)));

		tabHost.addTab(tabHost
				.newTabSpec("Total")
				.setIndicator("통계",
						getResources().getDrawable(R.drawable.scheduletab))
				.setContent(
						new Intent(this, TotalActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		this.menu = menu;

		getMenuInflater().inflate(R.menu.schedule, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			student.setLogin(0);
			student.setNum("0");
			mDb.updateStudent(student);

			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);

			finish();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabChanged(String arg0) {

		if (arg0.equals("Schedule")) {

		} else if (arg0.equals("Search")) {

		}
	}

}
