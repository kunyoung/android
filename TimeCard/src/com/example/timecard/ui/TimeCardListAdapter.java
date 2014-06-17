package com.example.timecard.ui;

import java.util.List;
import java.util.Map;

import com.example.timecard.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimeCardListAdapter extends BaseAdapter {

	private Context mContext;
	List<Map<String, Object>> list;
	
	public TimeCardListAdapter(Context context, List<Map<String, Object>> list){
		this.mContext = context;
		this.list = list;
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
		Viewlistholder holder;
		
		if (convertView == null) {
			//viewHolder에 저장
			convertView = View.inflate(mContext,R.layout.layout_worklist, null); //리소스를 잡아먹음
			holder = new Viewlistholder();
			//holder.seq = (TextView) convertView.findViewById(R.id.seq);
			holder.code = (TextView) convertView.findViewById(R.id.code);
			holder.bussiness = (TextView) convertView.findViewById(R.id.bussiness);
			convertView.setTag(holder);
		} else {
			//null이 아니면 기존 viewholder에서 저장된 값을 가져옴.
			holder = (Viewlistholder) convertView.getTag();
		}
		
		//holder.seq.setText(String.valueOf(position+1));
		holder.code.setText(String.valueOf(list.get(position).get("CODE"))+"("+list.get(position).get("WORKTIME") +"분)");
		holder.bussiness.setText(String.valueOf(list.get(position).get("BUSSINESS")));
		
		return convertView;
	}

}

class Viewlistholder {
	TextView seq;
	TextView code;
	TextView bussiness;
	
}
