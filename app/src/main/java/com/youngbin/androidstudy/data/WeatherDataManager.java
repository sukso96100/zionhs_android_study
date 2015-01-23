package com.youngbin.androidstudy.data;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by youngbin on 15. 1. 22.
 */
//데이터 관리 클래스
public class WeatherDataManager {
    Realm realm;
    String TAG = "WeatherDataManager";
    public WeatherDataManager(Context context){
        realm = Realm.getInstance(context);
    }

    //이전 캐시 데이터를 비우고 새로 캐시를 저장하는 함수
    public void dropOldAndSaveNew(
            String[] State, String[] Max, String[] Min, int number){

        Log.d(TAG, "Caching Data");
        //모든 데이터 쿼리하여 불러오기
        RealmResults<WeatherDataModel> result = queryAll();
        //데이터 처리 시작
        realm.beginTransaction();
        //모두 지우기
        result.clear();
        realm.commitTransaction();

        for(int i=0;i<number;i++){
            realm.beginTransaction();
            //새로 데이 객체 생성
            WeatherDataModel Data = realm.createObject(WeatherDataModel.class);
            //각 필드마다 데이터 설정
            Data.setState(State[i]);
            Data.setMax(Max[i]);
            Data.setMin(Min[i]);
            realm.commitTransaction();
        }
        Log.d(TAG, "Done Caching");
    }

    //날씨 상태 데이터 로드하는 함수
    public String[] loadStateArrayList(){
        //캐시된 데이터 모두 로드
        RealmResults<WeatherDataModel> result = queryAll();
        String[] List = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            WeatherDataModel w = result.get(i);
            List[i] = w.getState();
            // ... do something with the object ...
        }
        return List;
    }

    //최대기온 데이터 로드하는 함수
    public String[] loadMaxArrayList(){
        //캐시된 데이터 모두 로드
        RealmResults<WeatherDataModel> result = queryAll();
        String[] List = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            WeatherDataModel w = result.get(i);
            List[i] = w.getMax();
            // ... do something with the object ...
        }
        return List;
    }

    //최저기온 데이터 로드하는 함수 
    public String[] loadMinArrayList(){
        //캐시된 데이터 모두 로드 
        RealmResults<WeatherDataModel> result = queryAll();
        String[] List = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            WeatherDataModel w = result.get(i);
            List[i] = w.getMin();
            // ... do something with the object ...
        }
        return List;
    }

    //화면에 표시할 데이터 로드
    public String[] loadDataFromRealm(){
        Log.d(TAG, "Loading From Cache");
        RealmResults<WeatherDataModel> result = queryAll();
        String[] List = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            WeatherDataModel w = result.get(i);
            String Item = w.getState() + " : " + " MAX=" + w.getMax() + " MIN=" + w.getMin();
            List[i] = Item;
            // ... do something with the object ...
        }
        Log.d(TAG, "Done Loading From Cache");
        return List;
    }

    //모든 캐시 데이터 로드하는 함수
    private RealmResults<WeatherDataModel> queryAll(){
        RealmQuery<WeatherDataModel> query = realm.where(WeatherDataModel.class);
        RealmResults<WeatherDataModel> results = query.findAll();
        return results;
    }
}
