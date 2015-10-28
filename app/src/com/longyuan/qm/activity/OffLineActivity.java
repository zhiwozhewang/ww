package com.longyuan.qm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.R;
import com.longyuan.qm.fragment.BookShelfFragment;
import com.longyuan.qm.fragment.MagazineShelfFragment;

/**
 * Created by Android on 2014/12/6.
 */
public class OffLineActivity extends BaseActivity {
    private Button head_LeftBtn, right_RightBtn, bookBtn, magazineBtn;
    private BookShelfFragment bookFragment;
    private MagazineShelfFragment magazineFragment;
    private int isChanged = 0;
    private int isMag = 1;
    private int isBook = 2;
    private int checkColor = 0, normalColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_activity_normal);
        checkColor = mContext.getResources().getColor(R.color.system_top_nav);
        normalColor = mContext.getResources().getColor(R.color.system_top);

        bookBtn = (Button) findViewById(R.id.change_book);
        magazineBtn = (Button) findViewById(R.id.change_magazine);
        head_LeftBtn = (Button) findViewById(R.id.head_offline_layout_showLeft);
        head_LeftBtn.setBackgroundResource(R.drawable.button_back_selector);
        right_RightBtn = (Button) findViewById(R.id.head_offline_layout_showRight);
//        right_RightBtn.setVisibility(View.INVISIBLE);
        if (!ConstantsAmount.LOGININTERNETSTATE) {
            head_LeftBtn.setVisibility(View.INVISIBLE);
            right_RightBtn.setBackgroundResource(R.drawable.uy_refresh);

            right_RightBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isInternet()) {
                        Toast.makeText(mContext, "正在验证登录信息...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, SplashActivity.class);
                        startActivity(intent);
                        finish();
                        HomeActivity.instance_home.finish();
                    } else {
                        Toast.makeText(mContext, ConstantsAmount.BAD_NETWORK_CONNECTION, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            right_RightBtn.setVisibility(View.INVISIBLE);
        }
        initPage();
        head_LeftBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        magazineBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                magazineBtn
                        .setBackgroundResource(R.drawable.offline_left_pitchup);
                magazineBtn.setTextColor(checkColor);
                bookBtn.setBackgroundResource(R.drawable.offline_right_unpitchup);
                bookBtn.setTextColor(normalColor);
                if (isChanged != isMag) {
                    isChanged = isMag;
                    magazineFragment = new MagazineShelfFragment();
                    FragmentTransaction beginTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    beginTransaction.replace(R.id.offline_bookShelf_relativelayout,
                            magazineFragment);
                    beginTransaction.commit();
                }
            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                magazineBtn
                        .setBackgroundResource(R.drawable.offline_left_unpitchup);
                magazineBtn.setTextColor(normalColor);
                bookBtn.setBackgroundResource(R.drawable.offline_right_pitchup);
                bookBtn.setTextColor(checkColor);

                if (isChanged != isBook) {
                    isChanged = isBook;
                    bookFragment = new BookShelfFragment();
                    FragmentTransaction beginTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    beginTransaction.replace(R.id.offline_bookShelf_relativelayout,
                            bookFragment);
                    beginTransaction.commit();
                }
            }
        });
        overridePendingTransition(R.anim.slide_in_from_right,
                R.anim.slide_out_form_right);
    }

    private void initPage() {
        bookFragment = new BookShelfFragment();
        FragmentTransaction beginTransaction = getSupportFragmentManager()
                .beginTransaction();
        beginTransaction.add(R.id.offline_bookShelf_relativelayout,
                bookFragment);
        beginTransaction.commit();
        isChanged = isBook;
    }
}
