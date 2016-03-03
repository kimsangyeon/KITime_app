package kr.co.starlab;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EnrollListAdapter extends BaseAdapter {

	private ArrayList<EnrollList> eList = null; // 배열 List 객체
	private Context context;
	LayoutInflater inflater; // Layoutinflater

	private DBAdapter mDb = null; // DataBase
	private Student student;

	private String jsonResult;
	private String enrollurl = "http://starlab.kumoh.ac.kr/~starlab/enroll_proc.php"; // 학과

	public EnrollListAdapter(Context context, ArrayList<EnrollList> EnrollList) {

		this.context = context;
		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		eList = EnrollList;

		mDb = new DBAdapter(context); // DB 불러오기
		student = new Student();
		student = mDb.getStudentLogin();

		// Thread 추가
		if (android.os.Build.VERSION.SDK_INT > 9) {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);

		}
	}

	class ViewHolder {
		public TextView nametxt; // 과목 이름
		public TextView listtxt1; // list 내용 1
		public TextView listtxt2; // list 내용 2
		public TextView listtxt3; // list 내용 3
		public ImageButton actionbtn; // 과목 추가 버튼
		// public LinearLayout linearsearch; // list 배경
	}

	@Override
	public int getCount() {

		return eList.size();
	}

	@Override
	public Object getItem(int position) {
		return eList.get(position);
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
			convertView = inflater.inflate(R.layout.basket_list_item, parent,
					false);

			/* holder에 View 대입 */
			holder = new ViewHolder();

			holder.nametxt = (TextView) convertView
					.findViewById(R.id.basket_nametxt);

			holder.listtxt1 = (TextView) convertView
					.findViewById(R.id.basket_listtxt1);
			holder.listtxt2 = (TextView) convertView
					.findViewById(R.id.basket_listtxt2);
			holder.listtxt3 = (TextView) convertView
					.findViewById(R.id.basket_listtxt3);

			holder.actionbtn = (ImageButton) convertView
					.findViewById(R.id.basket_actionbtnid);

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		final EnrollList el = eList.get(position);
		final int pos = position;
		/* Text 내용 저장 */
		holder.nametxt.setText("교과목명: " + el.getName());

		holder.listtxt1.setText("학년: " + el.getYear() + "    구분: "
				+ el.getType() + "      Code: " + el.getCode());

		holder.listtxt2.setText("학점: " + el.getGrage() + "    교수님: "
				+ el.getProf() + "    수강학과: " + el.getMajor());

		holder.listtxt3.setText("강의시간: " + el.getDate());

		// 장바구니에서 삭제 버튼 event
		holder.actionbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

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
									"http://starlab.kumoh.ac.kr/~starlab/enroll_proc.php",
									postParameters);

					String res = response.toString();

					res = res.substring(res.length() - 2, res.length() - 1);

					if (res.equals("0")) {
						Toast.makeText(context, "장바구니에서 제거 실패", Toast.LENGTH_SHORT)
								.show();

					} else if (res.equals("1")) {
						Toast.makeText(context, "장바구니에서 제거", Toast.LENGTH_SHORT)
								.show();
					}

				} catch (Exception e) {
					Toast.makeText(context, "Compare fail", Toast.LENGTH_SHORT)
							.show();
				}
				eList.remove(pos);
				notifyDataSetChanged();
			}
		});

		return convertView;
	}
}
