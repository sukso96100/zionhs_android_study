package com.youngbin.androidstudy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by youngbin on 15. 1. 2.
 */
public class WeatherFragment extends Fragment {
    ArrayList<String> WeatherData;
    ArrayAdapter<String> myAdapter;

    public WeatherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // 이 Fragment 가 Overflow Menu 를 가지고 있음을 알리기.
        setHasOptionsMenu(true);
        //문자열 배열로 ListView에 넣을 데이터 만들기. 이름은 myArray.
        String[] myArray = {"Sample Item 0", "Sample Item 1", "Sample Item 2", "Sample Item 3", "Sample Item 4"};
        List<String> myArrayList = new ArrayList<String>(Arrays.asList(myArray));
        //ArrayAdapter 초기화
        myAdapter = new ArrayAdapter<String>(
                getActivity(), //Context - Fragment 는 Context 를 가지지 않으므로 Activity 에서 얻어옴
                android.R.layout.simple_list_item_1, //각 항목별 Layout - 일단은 안드로이드 시스템 내장 리소스 얻어옴
                myArrayList); //ListView 에 표시될 데이터
        //ListView 찾기
        ListView LV = (ListView)rootView.findViewById(R.id.listView); //R.id.(ListView id 값 - Layout 파일에서 확인 가능)
        //Adapter 설정
        LV.setAdapter(myAdapter);


        updateWeather();

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ForecastItem = myAdapter.getItem(position);//항목에 해당되는 데이터 얻기
                //새로운 Intent 객체 만들기
                //getActivity() - Context 는 Activity 에서 얻습니다.
                //DetailFragment.class 대상 앱 컴포넌트 입니다.
                Intent DetailIntent = new Intent(getActivity(), DetailActivity.class);
                // 키값은 weather_data, 첨부된 데이터는 String 형태인 ForecastItem 로 하였습니다.
                DetailIntent.putExtra("weather_data", ForecastItem);
                startActivity(DetailIntent); // Activity 시작하기
            }
        });



        return rootView;
    }

    void updateWeather(){
        SharedPreferences Pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String CityId = Pref.getString("pref_city_id",
                getString(R.string.pref_city_id_default_value));
        String Unit = Pref.getString("pref_unit",
                getString(R.string.pref_unit_default_value));

        myAsyncTask mat = new myAsyncTask(); //myAsyncTask 객체 생성
        mat.execute(CityId,Unit); //myAsyncTask 실행하기
    }

    protected class myAsyncTask extends AsyncTask<String, Void, String[]> {
        String forecastJsonStr = null;

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null; //HttpUrlConnection
            BufferedReader reader = null;


            // 날시 데이터 URL 에 사용될 옵션
            String format = "json";
            String units =  params[1];
            int numDays = 7;
            try {
                //새 URL 객체
                //UriBuilder 를 이용해 URL 만들기
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "id";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d("URL", url.toString());
                //새 URLConnection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                //InputStream 을 사용해 데이터 읽어들이기
                InputStream inputStream = urlConnection.getInputStream();
                //StringBuffer 에 데이터 저장
                StringBuffer buffer = new StringBuffer(); // 새로운 StringBuffer 생성
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // 불러온 데이터가 비어있음.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString(); //로드한 데이터 문자열 변수에 저장.
            } catch (IOException e) {
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect(); //HttpURLConnection 연결 끊기
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            try {
                JSONObject JsonObj = new JSONObject(forecastJsonStr);
                Log.d("JSON", forecastJsonStr);
                JSONArray JsonArray = JsonObj.getJSONArray("list");
                String[] WeatherDataArray = new String[JsonArray.length()];
                for (int i = 0; i < JsonArray.length(); i++) {
                    String MaxTemp = null; // 최대기온 저장할 변수
                    String MinTemp = null; // 최저기온 저장할 변수
                    String WeatherMain = null; // 날시상태 저장할 변수
                    String Item; // 1일 날시정보 저장할 변수
                    JSONObject EachObj = JsonArray.getJSONObject(i); // i 번째 객체 얻기
                    JSONObject Temp = EachObj.getJSONObject("temp"); // i 번쨰 객체에서 "temp" 객체 얻기
                    MaxTemp = Temp.getString("max"); // "temp" 객체에서 최대기온인 "max" 얻기
                    MinTemp = Temp.getString("min"); // "temp" 객체에서 최저기온인 "min" 얻기

                    // i 번째 객체에서 "weather" Json Array 얻기
                    JSONArray WeatherArray = EachObj.getJSONArray("weather");
                    // "weather" Json Array 의 0번째 객체 얻기
                    JSONObject WeatherObj = WeatherArray.getJSONObject(0);
                    // 0번째 객체에서 날시 상태에 해당되는 "main" 얻기
                    WeatherMain = WeatherObj.getString("main");

                    //하나의 문자열로 저장
                    Item = WeatherMain + " : " + " MAX=" + MaxTemp + " MIN=" + MinTemp;
                    Log.d("Item",Item);
                    // WeatherDataArray 에 i 번째 항목으로 넣기
                    WeatherDataArray[i] = Item;


                }
                return WeatherDataArray; //데이터 반환
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] Data) { // 백그라운드 작업 후, UI Thread 에서 실행 됩니다.
            if (Data != null) {
                myAdapter.clear();// Adapter 가 가진 데이터 모두 지우기
                for (String dayForecastStr : Data) {
                    myAdapter.add(dayForecastStr);// 반복문 이용해 데이터 새로 넣기
                }
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // 정의한 Menu 리소스를 여기서 Inflate 합니다.
        inflater.inflate(R.menu.weatherfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴 항목 클릭을 여기서 처리합니다..
        int id = item.getItemId(); // 클릭된 항목 id 값 얻기

        //얻은 id 값에 따라 클릭 처리
        if (id == R.id.action_refresh) { //id값이 action_refresh 이면.
            // 네트워크 작업 실행
            updateWeather();
            return true;
        }else if(id == R.id.action_web){
            SharedPreferences Pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String CityId = Pref.getString("pref_city_id",
                    getString(R.string.pref_city_id_default_value));
            String URL = "http://openweathermap.org/city/" + CityId;
            Uri webpage = Uri.parse(URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}
