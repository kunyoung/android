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
import com.example.timecard.ui.TimeCardListAdapter;
import com.example.timecard.util.ParamVO;
import com.example.timecard.util.util;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CalendarActivity extends Activity {

	private AQuery mAq;
	//달력 그리드뷰
	private GridView calGridView;
	private ProgressDialog pd;
	//그리드 인덱스에 맞는 날짜를 담기위한 컬렉션
	private List<String> seqNo;
	private Map<String, Object> paramMap;
	private ListView workList;
	private Calendar cal;
	private Button preDayBtn;
	private Button nextDayBtn;
	private int dayOfWeek;
	
	private List<String> arrWorkTypeID=new ArrayList<String>();
	private List<String> arrWorkTypeSpinner=new ArrayList<String>();
	private List<String> arrBussinessTypeID=new ArrayList<String>();
	private List<String> arrBussinessTypeSpinner=new ArrayList<String>();
	private Spinner workType;
	private Spinner busineessType;
	
	private SharedPreferences spf;
	private String empNo;
	
	private String regDt;
	private View regView;
	
	private String selectSeqNo;
	
	private String bussinessCode;
	
	private int selectGridIndex = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		spf = getSharedPreferences(MainActivity.LOGIN_PREFERENCES, 0);
		
		empNo = spf.getString("empNo", "");
		
		cal = Calendar.getInstance();
		
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		
		mAq = new AQuery(this);
		pd = new ProgressDialog(this);
		pd.setTitle("정보조회");
		pd.setMessage("Loading..");
		
		
		calGridView = (GridView) findViewById(R.id.calGridView);
		workList = (ListView) findViewById(R.id.workList);
		preDayBtn = (Button) findViewById(R.id.preDayBtn);
		nextDayBtn = (Button) findViewById(R.id.nextDayBtn);
		
		mAq.id(R.id.calDate).text(""+cal.get(Calendar.YEAR)+"년 "+(cal.get(Calendar.MONTH)+1)+"월");
		
		showCalendar();
		
		if(arrWorkTypeSpinner.size() == 0) selectWorkTypeList();
		
		
		workList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				paramMap = new HashMap<String, Object>();
				paramMap.put("seq_no", seqNo.get(position));
				
				mAq.ajax(ParamVO.URL+"/mTimeCard/selectTimeCardInfo.do", paramMap, JSONObject.class, new AjaxCallback<JSONObject>(){
					@Override
					public void callback(String url, JSONObject json, AjaxStatus status) {
						if(json != null){
							try {
								String content = json.getString("CONTENT");
								String workTime = json.getString("WORKTIME");
								bussinessCode = json.getString("BUSSINESSCODE");
								selectSeqNo = json.getString("SEQNO");
								String workcode = json.getString("WORKCODE");
								
								System.out.println(json.toString());
								
								
								regView = View.inflate(CalendarActivity.this, R.layout.layout_timecardreg, null);
								
								EditText workTimeEt = (EditText) regView.findViewById(R.id.workTimeEditText);
								EditText contentEt = (EditText) regView.findViewById(R.id.contentEditText);
								
								workTimeEt.setText(workTime);
								contentEt.setText(content);
								
								workType = (Spinner) regView.findViewById(R.id.workType);
								busineessType = (Spinner) regView.findViewById(R.id.businessType);
								
								ArrayAdapter<String> workAdapter = new ArrayAdapter<String>(CalendarActivity.this, android.R.layout.simple_spinner_item, arrWorkTypeSpinner);
								workAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								workType.setAdapter(workAdapter);
								

								for(int i=0; i<arrWorkTypeID.size(); i++){
									if(arrWorkTypeID.get(i).equals(workcode)){
										workType.setSelection(i);
										break;
									}
								}
								
								workType.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
										selectBusinessTypeList(arrWorkTypeID.get(position), bussinessCode);
									}

									@Override
									public void onNothingSelected(AdapterView<?> parent) {
										// TODO Auto-generated method stub
									}
								});
								
								showTimeAlertDialogForm();
								
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							Toast toast = Toast.makeText(CalendarActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
							toast.show();
						}
					}
				});
				
				return true;
			}
		});
		
		
		preDayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)-1, 1);
				selectGridIndex = 0;
				showCalendar();
			}
		});
		
		nextDayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 1);
				selectGridIndex = 0;
				showCalendar();
			}
		});
		
		
		calGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				selectGridIndex = position;

				regDt = String.valueOf(cal.get(Calendar.YEAR))+util.lpad(String.valueOf(cal.get(Calendar.MONTH)+1), 2, "0")+ util.lpad(String.valueOf((position+1-(dayOfWeek-1))), 2, "0");
				
				Log.d("regDt", regDt);
				
				regView = View.inflate(CalendarActivity.this, R.layout.layout_timecardreg, null);
				
				workType = (Spinner) regView.findViewById(R.id.workType);
				busineessType = (Spinner) regView.findViewById(R.id.businessType);
				
				ArrayAdapter<String> workAdapter = new ArrayAdapter<String>(CalendarActivity.this, android.R.layout.simple_spinner_item, arrWorkTypeSpinner);
				workAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				workType.setAdapter(workAdapter);
				
				workType.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						selectBusinessTypeList(arrWorkTypeID.get(position), "");
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
					}
				});
				
				
				new AlertDialog.Builder(CalendarActivity.this).setTitle("타임카드등록").setView(regView).setPositiveButton("등록", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String workCode = arrWorkTypeID.get(workType.getSelectedItemPosition());
						String bussinessCode = arrBussinessTypeID.get(busineessType.getSelectedItemPosition());
						
						EditText workTimeEt = (EditText) regView.findViewById(R.id.workTimeEditText);
						EditText contentEt = (EditText) regView.findViewById(R.id.contentEditText);
						
						String workTime =  workTimeEt.getText().toString();
						String content = contentEt.getText().toString();
						
						if(util.isNull(workTime) || util.isNull(content)){
							Toast.makeText(CalendarActivity.this, "업무시간/작업내용을 적어주세요!", Toast.LENGTH_SHORT).show();
						}else{
							paramMap = new HashMap<String, Object>();
							paramMap.put("emp_no", empNo);
							paramMap.put("business_code", bussinessCode);
							paramMap.put("content", content);
							paramMap.put("work_time", workTime);
							paramMap.put("reg_date", regDt);
							paramMap.put("work_code", workCode);
							
							mAq.progress(pd).ajax(ParamVO.URL+"/mTimeCard/insertTimeCardInfo.do", paramMap, JSONObject.class, new AjaxCallback<JSONObject>(){

								@Override
								public void callback(String url, JSONObject json, AjaxStatus status) {
									if(json != null){
										String stat = "";
										try {
											stat =  (String) json.get("status");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										if(stat.equals("1")){
											stat = "타임카드 정보를 저장하였습니다.";
										}else{
											stat = "타임카드 정보를 저장에 실패 하였습니다.";
										}
										
										Toast.makeText(CalendarActivity.this, stat, Toast.LENGTH_SHORT).show();
										
										showCalendar();
										
									}else{
										Toast toast = Toast.makeText(CalendarActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
										toast.show();
									}
								}
							});
							
							
						}
						
					}
				}
				).setNegativeButton("취소", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
				
				return true;
			}
		} );
		
		
		calGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectGridIndex = position;
				
				for(int i=0;i<parent.getChildCount(); i++){
					View childView = parent.getChildAt(i);
					if(i== position)
						childView.setBackground(getResources().getDrawable(R.drawable.list_selector_background_disabled));
					else
						childView.setBackground(getResources().getDrawable(R.drawable.list_selector_background_pressed_light));
				}

				
				showWorkList(position);

			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendar, menu);
		return true;
	}
	
	public void showCalendar(){
		paramMap = new HashMap<String, Object>();
		paramMap.put("emp_no", empNo);
		paramMap.put("reg_date", String.valueOf(cal.get(Calendar.YEAR))+(cal.get(Calendar.MONTH)+1));
		mAq.progress(pd).ajax(ParamVO.URL+"/mTimeCard/timeCardUserInfo.do", paramMap, JSONArray.class, new AjaxCallback<JSONArray>(){

			@Override
			public void callback(String url, JSONArray json, AjaxStatus status) {
				if(json != null){
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					
					dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
					
					for(int i=1 ; i<dayOfWeek; i++){
						list.add(null);
					}
					
					for(int i=0; i<json.length();i++){
						paramMap = new HashMap<String, Object>();
						try {
							JSONObject jobj = (JSONObject) json.get(i);
							paramMap.put("CALDT", jobj.get("CALDT"));
							paramMap.put("WORKTIME", jobj.get("WORKTIME").equals(null) ? "" : jobj.get("WORKTIME"));
							list.add(paramMap);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					mAq.id(R.id.calDate).text(""+cal.get(Calendar.YEAR)+"년 "+(cal.get(Calendar.MONTH)+1)+"월");
					
					TimeCardAdapter adapter = new TimeCardAdapter(CalendarActivity.this, list, selectGridIndex == 0 ? dayOfWeek-1 : selectGridIndex);
					calGridView.setAdapter(adapter);
					
					/*Toast toast = Toast.makeText(TimeCardActivity.this, "아이디/비밀번호를 확인해주세요.", Toast.LENGTH_SHORT);
					toast.show();*/
				}else{
					Toast toast = Toast.makeText(CalendarActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		showWorkList(selectGridIndex == 0 ? dayOfWeek-1 : selectGridIndex);
	}
	
	public void showWorkList(int position){
		
		seqNo = new ArrayList<String>();
		paramMap = new HashMap<String, Object>();
		paramMap.put("emp_no", empNo);
		paramMap.put("reg_date", String.valueOf(cal.get(Calendar.YEAR))+util.lpad(String.valueOf(cal.get(Calendar.MONTH)+1), 2, "0")+ util.lpad(String.valueOf((position+1-(dayOfWeek-1))), 2, "0"));
		
		mAq.progress(pd).ajax(ParamVO.URL+"/mTimeCard/timeCardInfo.do", paramMap, JSONArray.class, new AjaxCallback<JSONArray>(){

			@Override
			public void callback(String url, JSONArray json, AjaxStatus status) {
				if(json != null){
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					
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
							seqNo.add(String.valueOf(jobj.get("SEQNO")));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(list.size() > 0){
						TimeCardListAdapter adapter = new TimeCardListAdapter(CalendarActivity.this, list);
						workList.setAdapter(adapter);
					}else{
						workList.setAdapter(null);
					}
					
				}else{
					Toast toast = Toast.makeText(CalendarActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
	public void selectWorkTypeList(){
		
		mAq.progress(pd).ajax(ParamVO.URL+"/mTimeCard/timeCardWorkTypeList.do", null, JSONArray.class, new AjaxCallback<JSONArray>(){

			@Override
			public void callback(String url, JSONArray json, AjaxStatus status) {
				
				if(json != null){
					
					arrWorkTypeSpinner = new ArrayList<String>();
					arrWorkTypeID = new ArrayList<String>();
					
					for(int i=0; i<json.length();i++){
						try {
							JSONObject jobj = (JSONObject) json.get(i);
							arrWorkTypeSpinner.add((String) jobj.get("CODE_NM"));
							arrWorkTypeID.add((String) jobj.get("CODE"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else{
					Toast toast = Toast.makeText(CalendarActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
	public void selectBusinessTypeList(String workCode, String bcode){
		
		paramMap = new HashMap<String, Object>();
		paramMap.put("work_code", workCode);
		bussinessCode = bcode;
		
		mAq.progress(pd).ajax(ParamVO.URL+"/mTimeCard/timeCardBusinessTypeList.do", paramMap, JSONArray.class, new AjaxCallback<JSONArray>(){

			@Override
			public void callback(String url, JSONArray json, AjaxStatus status) {
				if(json != null){
					
					arrBussinessTypeSpinner = new ArrayList<String>();
					arrBussinessTypeID = new ArrayList<String>();
					
					for(int i=0; i<json.length();i++){
						try {
							JSONObject jobj = (JSONObject) json.get(i);
							arrBussinessTypeSpinner.add(String.valueOf(jobj.get("BUSINESSNAME")));
							arrBussinessTypeID.add(String.valueOf(jobj.get("BUSINESSCODE")));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					ArrayAdapter<String> bussinessAdapter = new ArrayAdapter<String>(CalendarActivity.this, android.R.layout.simple_spinner_item, arrBussinessTypeSpinner);
					bussinessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					busineessType.setAdapter(bussinessAdapter);		
					
					for(int i=0; i<arrBussinessTypeID.size(); i++){
						if(arrBussinessTypeID.get(i).equals(bussinessCode)){
							busineessType.setSelection(i);
							break;
						}
					}
					
				}else{
					Toast toast = Toast.makeText(CalendarActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
	/**
	 * 타임카드 정보 수정/등록 alertDialogForm
	 * @param seqNo
	 * @param regView
	 */
	public void showTimeAlertDialogForm(){
		new AlertDialog.Builder(CalendarActivity.this).setTitle("타임카드 수정").setView(regView).setPositiveButton("수정", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String workCode = arrWorkTypeID.get(workType.getSelectedItemPosition());
				String bussinessCode = arrBussinessTypeID.get(busineessType.getSelectedItemPosition());
				
				EditText workTimeEt = (EditText) regView.findViewById(R.id.workTimeEditText);
				EditText contentEt = (EditText) regView.findViewById(R.id.contentEditText);
				
				String workTime =  workTimeEt.getText().toString();
				String content = contentEt.getText().toString();
				
				if(util.isNull(workTime) || util.isNull(content)){
					Toast.makeText(CalendarActivity.this, "업무시간/작업내용을 적어주세요!", Toast.LENGTH_SHORT).show();
				}else{
					paramMap = new HashMap<String, Object>();
					paramMap.put("seq_no", selectSeqNo);
					paramMap.put("business_code", bussinessCode);
					paramMap.put("content", content);
					paramMap.put("work_time", workTime);
					paramMap.put("work_code", workCode);
					
					mAq.progress(pd).ajax(ParamVO.URL+"/mTimeCard/updateTimeCardInfo.do", paramMap, JSONObject.class, new AjaxCallback<JSONObject>(){

						@Override
						public void callback(String url, JSONObject json, AjaxStatus status) {
							if(json != null){
								String stat = "";
								try {
									stat =  (String) json.get("status");
								} catch (JSONException e) {
									e.printStackTrace();
								}
								
								if(stat.equals("1")){
									stat = "타임카드 정보를 수정하였습니다.";
								}else{
									stat = "타임카드 정보를 수정에 실패 하였습니다.";
								}
								
								Toast.makeText(CalendarActivity.this, stat, Toast.LENGTH_SHORT).show();
								
								showCalendar();
								
							}else{
								Toast toast = Toast.makeText(CalendarActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
								toast.show();
							}
						}
					});
					
					
				}
				
			}
		}
		).setNegativeButton("취소", new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).show();
	}
	
	

}
