package com.longyuan.qm.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.longyuan.qm.R;
import com.longyuan.qm.adapter.MyLoginViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PreloginActivity extends Activity {
    private ViewPager pager;
    // private List<Fragment> list = new ArrayList<Fragment>();
    private List<View> list = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preloginpage);
        SharedPreferences settings = getSharedPreferences("loginanim", 0);
        String flag = settings.getString("loginflag", "0");
        if (flag.equals("1")) {
            Intent intent = new Intent(PreloginActivity.this,
                    SplashActivity.class);
            startActivity(intent);
            finish();
        }

        pager = (ViewPager) findViewById(R.id.prelogin_viewPager);

        View pager1 = LayoutInflater.from(PreloginActivity.this).inflate(
                R.layout.prelogin_page1, null);
        View pager2 = LayoutInflater.from(PreloginActivity.this).inflate(
                R.layout.prelogin_page2, null);
        View pager3 = LayoutInflater.from(PreloginActivity.this).inflate(
                R.layout.prelogin_page3, null);

        list.add(pager1);
        list.add(pager2);
        list.add(pager3);

        MyLoginViewPagerAdapter adapter = new MyLoginViewPagerAdapter(
                PreloginActivity.this, list);
        pager.setAdapter(adapter);
        pager.setClickable(true);
        pager3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences settings = getSharedPreferences("loginanim",
                        0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("loginflag", "1");
                editor.commit();

                Intent intent = new Intent(PreloginActivity.this,
                        SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}