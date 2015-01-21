package com.youngbin.androidstudy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        String WeatherData;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            setHasOptionsMenu(true);

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            //날씨 정보 표시에 쓸 TextView 찾기
            TextView WeatherTxt = (TextView)rootView.findViewById(R.id.weather_data);
            //Activity 가 받은 Intent 얻어내어, 같이 Extra 로 온 데이터 얻기
            WeatherData = getActivity().getIntent().getStringExtra("weather_data");
            //TextView 내용을 얻은 데이터로 설정.
            WeatherTxt.setText(WeatherData);
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // 정의한 Menu 리소스를 여기서 Inflate 합니다.
            inflater.inflate(R.menu.menu_detail, menu);
            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // 메뉴 항목 클릭을 여기서 처리합니다..
            int id = item.getItemId(); // 클릭된 항목 id 값 얻기
            // Retrieve the share menu item

            return super.onOptionsItemSelected(item);
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,WeatherData);
            return shareIntent;
        }
    }
}
