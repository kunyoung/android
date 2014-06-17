package com.example.timecard.ui;



import java.util.List;
import java.util.Map;

import com.example.timecard.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimeCardAdapter extends BaseAdapter{

	private Context mContext;
	private List<Map<String, Object>> list;
	private int selectCal;
	
	public TimeCardAdapter(Context context, List<Map<String, Object>> list, int selectCal){
		this.mContext = context;
		this.list = list;
		this.selectCal = selectCal;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		
		if (convertView == null) {
			//viewHolder에 저장
			convertView = View.inflate(mContext,R.layout.layout_calendar, null); //리소스를 잡아먹음
			holder = new ViewHolder();
			holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
			holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
			convertView.setTag(holder);
		} else {
			//null이 아니면 기존 viewholder에서 저장된 값을 가져옴.
			holder = (ViewHolder) convertView.getTag();
		}
		
		Map<String, Object> map = list.get(position);
		
		if(map != null){
			holder.textView1.setText(String.valueOf(map.get("CALDT")).substring(6, 8));
			
			String workTime = String.valueOf(map.get("WORKTIME"));
			
			holder.textView2.setText(workTime);
	
			if(workTime == null || workTime.equals("")){
				holder.textView2.setVisibility(View.INVISIBLE);
			}
			
			if(position%7 == 0)  holder.textView1.setTextColor(Color.RED);
			if((position+1)%7 == 0)  holder.textView1.setTextColor(Color.BLUE);
			
			if(selectCal == position)
				convertView.setBackground(mContext.getResources().getDrawable(R.drawable.list_selector_background_disabled));
		}else{
			holder.textView1.setVisibility(View.INVISIBLE);
			holder.textView2.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

}

class ViewHolder {
	TextView textView1;
	TextView textView2;
	
	
	
	
	
	
}