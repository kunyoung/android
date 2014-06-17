package com.example.timecard;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.example.timecard.ui.TimeCardAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TimeCardActivity extends Activity {

	private AQuery mAq;
	private GridView gridView;
	private List<String> seqNo = new ArrayList<String>();
	private SharedPreferences spf;
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_card);
		mAq = new AQuery(this);
		pd = new ProgressDialog(TimeCardActivity.this);
		spf = getSharedPreferences(MainActivity.LOGIN_PREFERENCES, 0);
		
		gridView = (GridView) findViewById(R.id.gridView1);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
		
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				
				String emp_no = spf.getString("empNo", "");
				String seq_no = seqNo.get(position);

				paramsMap.put("emp_no", emp_no);
				paramsMap.put("seq_no", seq_no);
				
				
				pd.setTitle("조회중");
				pd.setMessage("wait...");
				
				mAq.progress(pd).ajax("http://121.128.254.192:8081/mTimeCard/timeCardInfo.do", paramsMap, JSONArray.class, new AjaxCallback<JSONArray>(){
					@Override
					public void callback(String url, JSONArray json, AjaxStatus status) {
						List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
						System.out.println(json.toString());
						for(int i=0; i<json.length();i++){
							Map<String, Object> map = new HashMap<String, Object>();
							try {
								JSONObject jobj = (JSONObject) json.get(i);
								map.put("SEQNO", jobj.get("SEQNO"));
								map.put("EMPNO", jobj.get("EMPNO"));
								map.put("CONTENT", jobj.get("CONTENT"));
								map.put("WORKTIME", jobj.get("WORKTIME"));
								map.put("BUSSINESS", jobj.get("BUSSINESS"));
								map.put("CODE", jobj.get("CODE"));
								map.put("REGDATE", jobj.get("REGDATE"));
								list.add(map);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						View view = (LinearLayout) View.inflate(TimeCardActivity.this, R.layout.timecarddetail, null);
						
						((TextView) view.findViewById(R.id.calDt)).setText(list.get(0).get("REGDATE").toString());
						((TextView) view.findViewById(R.id.workType)).setText(list.get(0).get("CODE").toString());
						((TextView) view.findViewById(R.id.business)).setText(list.get(0).get("BUSSINESS").toString());
						((TextView) view.findViewById(R.id.workTime)).setText(list.get(0).get("WORKTIME").toString());
						((TextView) view.findViewById(R.id.content)).setText(list.get(0).get("CONTENT").toString());
						
						new AlertDialog.Builder(TimeCardActivity.this).setTitle("타임카드정보").setIcon(R.drawable.logo)
						.setView(view).setPositiveButton("확인", new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}
						).setNegativeButton("취소", new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}).show();
						
					}
				});
				
				
				return true;
			}
		});

		
		pd.setTitle("로그인");
		pd.setMessage("wait...");

		SharedPreferences spf = getSharedPreferences(MainActivity.LOGIN_PREFERENCES, 0);
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("emp_no", spf.getString("empNo", "empty"));
		
		mAq.progress(pd).ajax("http://121.128.254.192:8081/mTimeCard/timeCardUserInfo.do", paramsMap, JSONArray.class, new AjaxCallback<JSONArray>(){

			@Override
			public void callback(String url, JSONArray json, AjaxStatus status) {
				if(json != null){
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					
					Calendar cal = Calendar.getInstance();
					cal.set(2013, 11, 1);
					
					int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
					
					for(int i=1 ; i<dayOfWeek; i++){
						list.add(null);
						seqNo.add("");
					}
					
					for(int i=0; i<json.length();i++){
						Map<String, Object> map = new HashMap<String, Object>();
						try {
							JSONObject jobj = (JSONObject) json.get(i);
							map.put("CALDT", jobj.get("CALDT"));
							map.put("WORKTIME", jobj.get("WORKTIME").equals(null) ? "" : jobj.get("WORKTIME"));
							map.put("WORKCODE", jobj.get("WORKCODE"));
							map.put("CONTENT", jobj.get("CONTENT"));
							map.put("CALDT", jobj.get("CALDT"));
							list.add(map);
							seqNo.add(jobj.get("SEQNO").toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
					//TimeCardAdapter adapter = new TimeCardAdapter(TimeCardActivity.this, list);
					//gridView.setAdapter(adapter);
					
					/*Toast toast = Toast.makeText(TimeCardActivity.this, "아이디/비밀번호를 확인해주세요.", Toast.LENGTH_SHORT);
					toast.show();*/
				}else{
					Toast toast = Toast.makeText(TimeCardActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_card, menu);
		return true;
	}
	

}




