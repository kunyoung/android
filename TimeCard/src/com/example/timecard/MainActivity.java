package com.example.timecard;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.example.timecard.util.ParamVO;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	//로그인 버튼
	private Button loginBtn;
	private AQuery mAq;
	private SharedPreferences spf;
	
	public static final String LOGIN_PREFERENCES = "LOGIN";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAq= new AQuery(this);
        
        loginBtn = (Button) findViewById(R.id.loginBtn);
        
        spf = getSharedPreferences(LOGIN_PREFERENCES, 0);
        
        mAq.id(R.id.idText).text(spf.getString("empNo", ""));
        mAq.id(R.id.pwdText).text(spf.getString("pwd", ""));
        
        //로그인 버튼 클릭 시 이벤트 설정
        //TODO 퓨쳐 서버와 연동하여 로그인 처리 한다.
        loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				paramsMap.put("emp_no", mAq.id(R.id.idText).getText());
				paramsMap.put("password", mAq.id(R.id.pwdText).getText());
				
				ProgressDialog pd = new ProgressDialog(MainActivity.this);
				pd.setTitle("로그인");
				pd.setMessage("wait...");
				
				mAq.progress(pd).ajax(ParamVO.URL+"/mTimeCard/login.do", paramsMap, JSONObject.class, new AjaxCallback<JSONObject>(){

					@Override
					public void callback(String url, JSONObject json, AjaxStatus status) {
						try {
							if(json != null){
								if(json.get("errorCode").toString().equals("0")){
									JSONObject userInfo = (JSONObject) json.get("userInfo");
									spf = getSharedPreferences(LOGIN_PREFERENCES, 0);
									SharedPreferences.Editor editor = spf.edit();
									editor.putString("empNo", userInfo.getString("empNo"));
									editor.putString("pwd", userInfo.getString("pwd"));
									editor.commit();
									//로그인 후 타임카드 화면으로 이 동
									Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
									startActivity(intent);
									
								}else{
									Toast toast = Toast.makeText(MainActivity.this, "아이디/비밀번호를 확인해주세요.", Toast.LENGTH_SHORT);
									toast.show();
								}
							}else{
								Toast toast = Toast.makeText(MainActivity.this, "오류가 발생하였습니다"+status.getCode(), Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
   
    
}
