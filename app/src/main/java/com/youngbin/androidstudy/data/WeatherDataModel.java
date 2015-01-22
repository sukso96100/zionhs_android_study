package com.youngbin.androidstudy.data;

import io.realm.RealmObject;

/**
 * Created by youngbin on 15. 1. 22.
 */
//데이터 모델 클래스(데이터 구조를 정의하는 클래스)
public class WeatherDataModel extends RealmObject {
    private String State;
    private String Max;
    private String Min;

    public String getState() {
        return State;
    }

    public void setState(String state) {
        this.State = state;
    }

    public String getMax() {
        return Max;
    }

    public void setMax(String max) {
        this.Max = max;
    }

    public String getMin() {
        return Min;
    }

    public void setMin(String min) {
        this.Min = min;
    }
}
