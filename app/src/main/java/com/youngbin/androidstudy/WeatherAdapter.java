package com.youngbin.androidstudy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 영자신문-1 on 2015-01-23.
 */
public class WeatherAdapter extends BaseAdapter{

    Activity ctx;
    ArrayList<String> Day;
    ArrayList<String> State;
    ArrayList<String> MaxTemp;
    ArrayList<String> MinTemp;

    public WeatherAdapter(Activity Activity, ArrayList<String> day,
                          ArrayList<String> state, ArrayList<String> maxtemp,
                          ArrayList<String> mintemp){
        super();
        this.ctx = Activity;
        this.Day = day;
        this.State = state;
        this.MaxTemp = maxtemp;
        this.MinTemp = mintemp;
    }
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

    private class ViewHolder{
        TextView txtDay;
        TextView txtState;
        TextView txtMaxTemp;
        TextView txtMinTemp;
    }

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