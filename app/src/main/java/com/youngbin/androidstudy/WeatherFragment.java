package com.youngbin.androidstudy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.DeadObjectException;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.youngbin.androidstudy.data.WeatherDataManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
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
    WeatherAdapter myWeatherAdapter;
    ArrayList<String> dayArrayList;
    ArrayList<String> stateArrayList;
    ArrayList<String> maxTempArrayList;
    ArrayList<String> minTempArrayList;
    ListView LV;

    public WeatherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // 이 Fragment 가 Overflow Menu 를 가지고 있음을 알리기.
        setHasOptionsMenu(true);
        //문자열 배열로 ListView에 넣을 데이터 만들기. 이름은 myArray.
        String[] dayArray = {"Sunday", "Monday", "Tuesday","Wednesday", "Thursday", "Friday", "Saturday"};
        dayArrayList = new ArrayList<String>(Arrays.asList(dayArray));

        String[] stateArray = {"Clear", "Sunny", "Rain", "Cloud", "Snow", "Clear", "Rain"};
        stateArrayList = new ArrayList<String>(Arrays.asList(stateArray));

        String[] maxTempArray = {"15","15","15","15","15","15","15"};
        maxTempArrayList = new ArrayList<String>(Arrays.asList(maxTempArray));

        String[] minTempArray = {"15","15","15","15","15","15","15"};
        minTempArrayList = new ArrayList<String>(Arrays.asList(minTempArray));

        myWeatherAdapter = new WeatherAdapter(getActivity(), dayArrayList, stateArrayList,
                maxTempArrayList, minTempArrayList);

        //ListView 찾기
        LV = (ListView)rootView.findViewById(R.id.listView); //R.id.(ListView id 값 - Layout 파일에서 확인 가능)
        //Adapter 설정
        LV.setAdapter(myWeatherAdapter);
        loadData(getActivity());

        

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent DetailIntent = new Intent(getActivity(), DetailActivity.class);
                DetailIntent.putExtra("day", dayArrayList.get(position));
                DetailIntent.putExtra("state", stateArrayList.get(position));
                DetailIntent.putExtra("max", maxTempArrayList.get(position));
                DetailIntent.putExtra("min", minTempArrayList.get(position));
                startActivity(DetailIntent);
            }
        });



        return rootView;
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
            loadData(getActivity());
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

    public void loadData(final Context context){

        SharedPreferences Pref = PreferenceManager.getDefaultSharedPreferences(context);
        String CityId = Pref.getString("pref_city_id",
                context.getString(R.string.pref_city_id_default_value));
        String Unit = Pref.getString("pref_unit",
                context.getString(R.string.pref_unit_default_value));

        final String FORECAST_BASE_URL =
                "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String QUERY_PARAM = "id";
        final String FORMAT_PARAM = "mode";
        final String UNITS_PARAM = "units";
        final String DAYS_PARAM = "cnt";


        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, CityId)
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(UNITS_PARAM, Unit)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(7))
                .build();

        AsyncHttpClient Client = new AsyncHttpClient();
        Client.get(builtUri.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ConvertedResponse = null;
                try {
                    ConvertedResponse = new String(responseBody, "UTF-8");
                    Log.d("JsonResponse", ConvertedResponse);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();}

                    String[] State = new String[7];
                    String[] Max= new String[7];
                    String[] Min= new String[7];

                    try {
                        JSONObject JsonObj = new JSONObject(ConvertedResponse);
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
                            State[i] = WeatherMain;
                            Max[i] = MaxTemp;
                            Min[i] = MinTemp;


                        }

                        stateArrayList = new ArrayList<String>(Arrays.asList(State));
                        maxTempArrayList = new ArrayList<String>(Arrays.asList(Max));
                        minTempArrayList = new ArrayList<String>(Arrays.asList(Min));

                        myWeatherAdapter = new WeatherAdapter(getActivity(), dayArrayList, stateArrayList,
                                maxTempArrayList, minTempArrayList);
                        LV.setAdapter(myWeatherAdapter);
                    } catch (JSONException error) {
                        error.printStackTrace();
                    }
                //네트워킹으로 얻은 데이터 기기에 저장
                WeatherDataManager manager = new WeatherDataManager(getActivity());
                manager.dropOldAndSaveNew(State, Max, Min, 7);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    //기기에 저장된 데이터 로드
                WeatherDataManager manager = new WeatherDataManager(context);
                String[] StateCache = manager.loadStateArrayList();
                String[] MaxCache = manager.loadMaxArrayList();
                String[] MinCache = manager.loadMinArrayList();
                stateArrayList = new ArrayList<String>(Arrays.asList(StateCache));
                maxTempArrayList = new ArrayList<String>(Arrays.asList(MaxCache));
                minTempArrayList = new ArrayList<String>(Arrays.asList(MinCache));

                myWeatherAdapter = new WeatherAdapter(getActivity(), dayArrayList, stateArrayList,
                        maxTempArrayList, minTempArrayList);
                LV.setAdapter(myWeatherAdapter);


            }
        });




    }
}
