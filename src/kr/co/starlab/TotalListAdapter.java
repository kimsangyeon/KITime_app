package kr.co.starlab;

import java.util.ArrayList;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

public class TotalListAdapter extends BaseAdapter {

	private ArrayList<EnrollList> tList = null; // 배열 List 객체
	private DBAdapter mDb = null; // DataBase
	private Student student;

	private Context context;

	LayoutInflater inflater; // Layoutinflater

	public TotalListAdapter(Context context, ArrayList<EnrollList> totalList) {

		this.context = context;
		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mDb = new DBAdapter(context); // DB 불러오기
		student = new Student();
		student = mDb.getStudentLogin();

		tList = totalList;

		// Thread 추가
		if (android.os.Build.VERSION.SDK_INT > 9) {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);

		}

	}

	class ViewHolder {
		public TextView nametxt; // 과목 이름
		public TextView codetxt; // 과목 코드
		public SeekBar seekbar; // 비율 표시 seek bar
		public TextView totaltxt; // 과목 코드
	}

	@Override
	public int getCount() {

		return tList.size();
	}

	@Override
	public Object getItem(int position) {
		return tList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// converview null일경우 객체 초기 생성
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.total_list_item, parent,
					false);

			holder = new ViewHolder();

			// holder.linearsearch = (LinearLayout) convertView
			// .findViewById(R.id.Linear_searchlistid);

			holder.nametxt = (TextView) convertView
					.findViewById(R.id.total_nametxt);

			holder.codetxt = (TextView) convertView
					.findViewById(R.id.total_codetxt);

			holder.seekbar = (SeekBar) convertView
					.findViewById(R.id.total_seekbar);
			holder.seekbar.setEnabled(false);

			holder.totaltxt = (TextView) convertView
					.findViewById(R.id.total_numtxt);

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
			holder.seekbar.setEnabled(false);
		}

		final EnrollList el = tList.get(position);
		double current = 0;

		if (el.getIndex() != 0) {
			current = (double) el.getMaxnum() / (double) el.getCurnum();
			/* Text 내용 저장 */
			holder.nametxt.setText("교과목명: " + el.getName());
			holder.codetxt.setText("  과목코드: " + el.getCode());
			Log.v("@", " getmax: " + el.getMaxnum());
			Log.v("@", " getcur: " + el.getCurnum());
			Log.v("@", " current: " + current);
			Log.v("@", " dd: " + 100 / current);
			Log.v("@", " aa: " + (int) (100 / current));
			if (100 / current < 100) {

				holder.seekbar.setProgress((int) (100 / current));
			} else {

				holder.seekbar.setProgress(100);
			}

			holder.seekbar.setVisibility(View.VISIBLE);
			holder.totaltxt.setText("신청인원: " + el.getCurnum() + " / "
					+ el.getMaxnum() + " (" + (int) (100 / current) + "%)");

		} else if (el.getIndex() == 0) {
			holder.nametxt.setText("");
			holder.codetxt.setText("");
			holder.totaltxt.setText("");
			holder.seekbar.setProgress(0);
			holder.seekbar.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

}
