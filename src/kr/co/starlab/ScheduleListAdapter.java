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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleListAdapter extends BaseAdapter {

	private ArrayList<EnrollList> eList = null; // 배열 List 객체
	private Context context;
	LayoutInflater inflater; // Layoutinflater

	private DBAdapter mDb = null; // DataBase
	private Student student;

	private String jsonResult;
	private String enrollurl = "http://starlab.kumoh.ac.kr/~starlab/enroll_proc.php"; // 학과

	public ScheduleListAdapter(Context context, ArrayList<EnrollList> EnrollList) {

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
		public ImageView actionbtn; // 과목 추가 버튼
		// public LinearLayout linearsearch; // list 배경
	}

	@Override
	public int getCount() {

		return eList.size();
	}

	@Override
	public EnrollList getItem(int position) {
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
			convertView = inflater.inflate(R.layout.basket_schedulelist_item,
					parent, false);
			
			/* holder에 View 대입 */
			holder = new ViewHolder();

			holder.nametxt = (TextView) convertView
					.findViewById(R.id.basket_schedulenametxt);

			holder.listtxt1 = (TextView) convertView
					.findViewById(R.id.basket_schedulelisttxt1);
			holder.listtxt2 = (TextView) convertView
					.findViewById(R.id.basket_schedulelisttxt2);
			holder.listtxt3 = (TextView) convertView
					.findViewById(R.id.basket_schedulelisttxt3);

			holder.actionbtn = (ImageView) convertView
					.findViewById(R.id.basket_scheduleactionbtnid);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final EnrollList el = eList.get(position);

		/* Text 내용 저장 */
		holder.nametxt.setText("교과목명: " + el.getName());

		holder.listtxt1.setText("학년: " + el.getYear() + "    구분: "
				+ el.getType() + "      Code: " + el.getCode());

		holder.listtxt2.setText("학점: " + el.getGrage() + "    교수님: "
				+ el.getProf() + "    수강학과: " + el.getMajor());

		holder.listtxt3.setText("강의시간: " + el.getDate());

		/* 장바구니의 강의 시간표에 추가 event*/
		holder.actionbtn.setOnClickListener(new OnClickListener() {

			String[] day = null; // time day date
			String week = null; // 요일
			String time = null; // 시간
			String date = null;
			int check = 0;

			@Override
			public void onClick(View v) {

				day = el.getTime().split("/"); // 요일 자르기

				/* 요일별 시간 중복 검사 */
				for (int i = 0; i < day.length; i++) {
					// 과목 시간 겹칠 일 경우
					if (check == 1) {
						break;
					}

					for (int j = 0; j < day[i].length(); j++) {
						week = day[i].substring(0, 1); // 요일 자르기

						if (j > 0) {
							time = day[i].substring(j, 1 + j); // 시간 자르기

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

							// 중복 검사 받아오기
							check = ScheduleActivity.ScheduleCheck(
									el.getName(), el.getRoom(),
									Integer.parseInt(date));

							// 과목 시간 겹칠 경우
							if (check == 1) {
								break;
							}

						}

					}
				}

				if (check == 0) {
					/* 요일별 시간 넣기 */
					for (int i = 0; i < day.length; i++) {

						for (int j = 0; j < day[i].length(); j++) {
							week = day[i].substring(0, 1);

							String[] room = el.getRoom().split("/");

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

								ScheduleActivity.ScheduleInput(el.getName(),
										room[i],
										Integer.parseInt(el.getGrage()),
										Integer.parseInt(date), el);

							}
						}
					}
					
					// 인강일 경우
					if (el.getDate().length() == 0) {
						ScheduleActivity.ScheduleInputInternet(el.getName(),
								Integer.parseInt(el.getGrage()), el);
					}

					/* 서버 DB에 추가 하기 */
					/* 서버 DB에 추가 */
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters.add(new BasicNameValuePair("sid", student
							.getNum()));

					postParameters.add(new BasicNameValuePair("cid", Integer
							.toString(el.getIndex())));

					postParameters.add(new BasicNameValuePair("slot", "1"));

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
							Toast.makeText(context, "교과목 추가 실패",
									Toast.LENGTH_SHORT).show();

						} else if (res.equals("1")) {
							Toast.makeText(context,
									"교과목: " + el.getName() + " 추가",
									Toast.LENGTH_SHORT).show();
						}

					} catch (Exception e) {
						Toast.makeText(context, "Compare fail",
								Toast.LENGTH_SHORT).show();
					}
					ScheduleActivity.img_count++;
				}

			}
		});

		notifyDataSetChanged();

		return convertView;
	}
}
