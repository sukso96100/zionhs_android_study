package com.youngbin.androidstudy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//메인 화면에 일기예보 표시에 사용할 커스텀 어뎁터
public class WeatherAdapter extends BaseAdapter{

    Activity ctx;
    ArrayList<String> Day;
    ArrayList<String> State;
    ArrayList<String> MaxTemp;
    ArrayList<String> MinTemp;

    //생성자(초기화 블럭) - 어뎁터 초기화 할때 호출하며, 필요한 값들을 받습니다.
    public WeatherAdapter(Activity Activity, ArrayList<String> day,
                          ArrayList<String> state, ArrayList<String> maxtemp,
                          ArrayList<String> mintemp){
        super(); //부모 클래스인 BaseAdapter 의 생성자를 호출합니다.
        this.ctx = Activity;
        this.Day = day;
        this.State = state;
        this.MaxTemp = maxtemp;
        this.MinTemp = mintemp;
    }

    //어뎁터가 가진 데이터 갯수를 반환하는 메서드
    @Override
    public int getCount() {
        return State.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 각 하목바다 표시할 View 들을 보관하는 ViewHolder 클래스
    private class ViewHolder{
        TextView txtDay;
        TextView txtState;
        TextView txtMaxTemp;
        TextView txtMinTemp;
    }

    //ListView 에 표시할 View를 생성하여 반환하는 메서드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    LayoutInflater inflator =  ctx.getLayoutInflater();
    if(convertView == null){
        convertView = inflator.inflate(R.layout.list_item, null);
        holder = new ViewHolder();
        holder.txtDay = (TextView)convertView.findViewById(R.id.day);
        holder.txtState = (TextView)convertView.findViewById(R.id.state);
        holder.txtMaxTemp = (TextView)convertView.findViewById(R.id.max);
        holder.txtMinTemp = (TextView)convertView.findViewById(R.id.min);
    }else{
        holder = (ViewHolder) convertView.getTag();
    }

    holder.txtDay.setText(Day.get(position));
    holder.txtState.setText(State.get(position));
    holder.txtMaxTemp.setText(MaxTemp.get(position));
    holder.txtMinTemp.setText(MinTemp.get(position));
    return convertView;
    }
}