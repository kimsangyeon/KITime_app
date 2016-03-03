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

	private ArrayList<EnrollList> eList = null; // �迭 List ��ü
	private Context context;
	LayoutInflater inflater; // Layoutinflater

	private DBAdapter mDb = null; // DataBase
	private Student student;

	private String jsonResult;
	private String enrollurl = "http://starlab.kumoh.ac.kr/~starlab/enroll_proc.php"; // �а�

	public ScheduleListAdapter(Context context, ArrayList<EnrollList> EnrollList) {

		this.context = context;
		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		eList = EnrollList;

		mDb = new DBAdapter(context); // DB �ҷ�����
		student = new Student();
		student = mDb.getStudentLogin();

		// Thread �߰�
		if (android.os.Build.VERSION.SDK_INT > 9) {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);

		}
	}

	class ViewHolder {
		public TextView nametxt; // ���� �̸�
		public TextView listtxt1; // list ���� 1
		public TextView listtxt2; // list ���� 2
		public TextView listtxt3; // list ���� 3
		public ImageView actionbtn; // ���� �߰� ��ư
		// public LinearLayout linearsearch; // list ���
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
		// converview null�ϰ�� ��ü �ʱ� ����
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.basket_schedulelist_item,
					parent, false);
			
			/* holder�� View ���� */
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

		/* Text ���� ���� */
		holder.nametxt.setText("�������: " + el.getName());

		holder.listtxt1.setText("�г�: " + el.getYear() + "    ����: "
				+ el.getType() + "      Code: " + el.getCode());

		holder.listtxt2.setText("����: " + el.getGrage() + "    ������: "
				+ el.getProf() + "    �����а�: " + el.getMajor());

		holder.listtxt3.setText("���ǽð�: " + el.getDate());

		/* ��ٱ����� ���� �ð�ǥ�� �߰� event*/
		holder.actionbtn.setOnClickListener(new OnClickListener() {

			String[] day = null; // time day date
			String week = null; // ����
			String time = null; // �ð�
			String date = null;
			int check = 0;

			@Override
			public void onClick(View v) {

				day = el.getTime().split("/"); // ���� �ڸ���

				/* ���Ϻ� �ð� �ߺ� �˻� */
				for (int i = 0; i < day.length; i++) {
					// ���� �ð� ��ĥ �� ���
					if (check == 1) {
						break;
					}

					for (int j = 0; j < day[i].length(); j++) {
						week = day[i].substring(0, 1); // ���� �ڸ���

						if (j > 0) {
							time = day[i].substring(j, 1 + j); // �ð� �ڸ���

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

							// �ߺ� �˻� �޾ƿ���
							check = ScheduleActivity.ScheduleCheck(
									el.getName(), el.getRoom(),
									Integer.parseInt(date));

							// ���� �ð� ��ĥ ���
							if (check == 1) {
								break;
							}

						}

					}
				}

				if (check == 0) {
					/* ���Ϻ� �ð� �ֱ� */
					for (int i = 0; i < day.length; i++) {

						for (int j = 0; j < day[i].length(); j++) {
							week = day[i].substring(0, 1);

							String[] room = el.getRoom().split("/");

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

								ScheduleActivity.ScheduleInput(el.getName(),
										room[i],
										Integer.parseInt(el.getGrage()),
										Integer.parseInt(date), el);

							}
						}
					}
					
					// �ΰ��� ���
					if (el.getDate().length() == 0) {
						ScheduleActivity.ScheduleInputInternet(el.getName(),
								Integer.parseInt(el.getGrage()), el);
					}

					/* ���� DB�� �߰� �ϱ� */
					/* ���� DB�� �߰� */
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
							Toast.makeText(context, "������ �߰� ����",
									Toast.LENGTH_SHORT).show();

						} else if (res.equals("1")) {
							Toast.makeText(context,
									"������: " + el.getName() + " �߰�",
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
