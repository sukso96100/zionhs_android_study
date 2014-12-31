package com.youngbin.androidstudy;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //문자열 배열로 ListView에 넣을 데이터 만들기. 이름은 myArray.
            String[] myArray = {"Sample Item 0", "Sample Item 1", "Sample Item 2", "Sample Item 3", "Sample Item 4"};
            //ArrayAdapter 초기화
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(
                    getActivity(), //Context - Fragment 는 Context 를 가지지 않으므로 Activity 에서 얻어옴
                    android.R.layout.simple_list_item_1, //각 항목별 Layout - 일단은 안드로이드 시스템 내장 리소스 얻어옴
                    myArray); //ListView 에 표시될 데이터
            //ListView 찾기
            ListView LV = (ListView)rootView.findViewById(R.id.listView); //R.id.(ListView id 값 - Layout 파일에서 확인 가능)
            //Adapter 설정
            LV.setAdapter(myAdapter);
            return rootView;
        }
    }
}
