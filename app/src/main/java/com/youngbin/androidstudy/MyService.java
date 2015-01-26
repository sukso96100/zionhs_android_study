package com.youngbin.androidstudy;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.youngbin.androidstudy.data.WeatherDataManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static android.support.v4.app.NotificationCompat.*;

public class MyService extends Service {
    ArrayList<String> stateArrayList;
    ArrayList<String> maxTempArrayList;
    ArrayList<String> minTempArrayList;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
           Log.d("MyService","Service Started");
           loadData(MyService.this);
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


                } catch (JSONException error) {
                    error.printStackTrace();
                }
                //네트워킹으로 얻은 데이터 기기에 저장
                WeatherDataManager manager = new WeatherDataManager(MyService.this);
                manager.dropOldAndSaveNew(State, Max, Min, 7);

                Calendar Cal = Calendar.getInstance();
                int today = Cal.get(Calendar.DAY_OF_WEEK);
                String Desc = stateArrayList.get(today)+" , "+"Max : "+
                        maxTempArrayList.get(today)+" , "+"Min : "+minTempArrayList.get(today);

                NotificationCompat.Builder builder = new Builder(MyService.this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Today's Weather")
                        .setContentText(Desc);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());

                stopSelf();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                stopSelf();
            }
        });




    }


}
