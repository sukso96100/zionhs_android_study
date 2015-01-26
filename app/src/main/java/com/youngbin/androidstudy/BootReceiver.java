package com.youngbin.androidstudy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //기기 부팅이 완료되었음을 알리는 방송을 수신하면, 아래 코드가 실행됩니다.
        Log.d("BootReceiver","Registering Alarm Tasks");
        Intent ServiceIntent = new Intent(context, MyService.class);
        //PendingIntent 는 Intent 를 다른 외부 앱이 실행 할 수 있도록 해줍니다.
        PendingIntent PI = PendingIntent.getService(context, 0, ServiceIntent, 0);

        //AlarmManager 를 이용해 특정 시간에 작업이 수행되도록 합니다.
        AlarmManager AM = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        try {
            AM.cancel(PI);
        }catch (Exception e){}
        AM.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, PI);
    }
}
