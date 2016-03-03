package kr.co.starlab;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchListAdapter extends BaseAdapter {

	private ArrayList<SearchList> sList = null; // �迭 List ��ü
	private DBAdapter mDb = null; // DataBase
	private Student student;

	private String jsonResult;
	private String enrollurl = "http://starlab.kumoh.ac.kr/~starlab/enroll_proc.php"; // ��ٱ���

	private Context context;

	private ArrayList<SearchList> basketList;
	private EnrollListAdapter bla;

	private ListView basketlistView;

	private Activity mActivity;

	LayoutInflater inflater; // Layoutinflater

	public SearchListAdapter(Context context, ArrayList<SearchList> SearchList) {

		this.context = context;
		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mDb = new DBAdapter(context); // DB �ҷ�����
		student = new Student();
		student = mDb.getStudentLogin();

		sList = SearchList;

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
		public ImageButton addbtn; // ���� �߰� ��ư
		// public LinearLayout linearsearch; // list ���
	}

	@Override
	public int getCount() {

		return sList.size();
	}

	@Override
	public Object getItem(int position) {
		return sList.get(position);
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
			convertView = inflater.inflate(R.layout.listitem, parent, false);

			/* holder�� View ���� */
			holder = new ViewHolder();

			holder.nametxt = (TextView) convertView.findViewById(R.id.nametxt);

			holder.listtxt1 = (TextView) convertView
					.findViewById(R.id.listtxt1);
			holder.listtxt2 = (TextView) convertView
					.findViewById(R.id.listtxt2);
			holder.listtxt3 = (TextView) convertView
					.findViewById(R.id.listtxt3);

			holder.addbtn = (ImageButton) convertView
					.findViewById(R.id.addbtnid);

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		final SearchList sl = sList.get(position);

		if (sl.getIndex() != 0) {
			/* Text ���� ���� */
			holder.nametxt.setText("�������: " + sl.getName());

			holder.listtxt1.setText("�г�: " + sl.getYear() + "    ����: "
					+ sl.getType() + "      Code: " + sl.getCode());

			holder.listtxt2.setText("����: " + sl.getGrage() + "    ������: "
					+ sl.getProf() + "    �����а�: " + sl.getMajor());

			holder.listtxt3.setText("���ǽð�: " + sl.getDate());
			holder.addbtn.setClickable(true);
			holder.addbtn.setVisibility(View.VISIBLE);
		}else if ( sl.getIndex() == 0){
			holder.nametxt.setText("");
			holder.listtxt1.setText("");
			holder.listtxt2.setText("");
			holder.listtxt3.setText("");
			holder.addbtn.setClickable(false);
			holder.addbtn.setVisibility(View.INVISIBLE);
		}
		
		/* ��ٱ��� �߰� ��ư event */
		holder.addbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// ��ٱ��ϰ� ������� return
				if (SearchActivity.open == true)
					return;

				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair("sid", student
						.getNum()));

				postParameters.add(new BasicNameValuePair("cid", Integer
						.toString(sl.getIndex())));

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
						if(sl.getIndex() != 0)
						Toast.makeText(context, "�ߺ� ����", Toast.LENGTH_SHORT)
								.show();

					} else if (res.equals("1")) {
						Toast.makeText(context, "��ٱ��� �߰�", Toast.LENGTH_SHORT)
								.show();
					}

				} catch (Exception e) {
					Toast.makeText(context, "Compare fail", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		return convertView;
	}

}
