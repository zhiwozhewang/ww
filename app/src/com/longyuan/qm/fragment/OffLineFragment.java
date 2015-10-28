package com.longyuan.qm.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.longyuan.qm.BaseFragment;
import com.longyuan.qm.R;
import com.longyuan.qm.activity.HomeActivity;

public class OffLineFragment extends BaseFragment {
    private Button head_LeftBtn, bookBtn, magazineBtn;
    private BookShelfFragment bookFragment;
    private MagazineShelfFragment magazineFragment;
    private int isChanged = 0;
    private int isMag = 1;
    private int isBook = 2;
    private int checkColor = 0, normalColor = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        checkColor = mContext.getResources().getColor(R.color.system_top_nav);
        normalColor = mContext.getResources().getColor(R.color.system_top);

        View mView = inflater.inflate(
                R.layout.offline_activity_normal, null);

        bookBtn = (Button) mView.findViewById(R.id.change_book);
        magazineBtn = (Button) mView.findViewById(R.id.change_magazine);
        head_LeftBtn = (Button) mView.findViewById(R.id.head_offline_layout_showLeft);
        initPage();
        head_LeftBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).showMenu();
            }
        });

        magazineBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                magazineBtn
                        .setBackgroundResource(R.drawable.offline_left_pitchup);
                magazineBtn.setTextColor(checkColor);
                bookBtn.setBackgroundResource(R.drawable.offline_right_unpitchup);
                bookBtn.setTextColor(normalColor);
                if (isChanged != isMag) {
                    Log.e("onclick:bookbtn", "" + isChanged);
                    isChanged = isMag;
                    magazineFragment = new MagazineShelfFragment();
                    FragmentTransaction beginTransaction = getFragmentManager()
                            .beginTransaction();
                    beginTransaction.replace(R.id.offline_bookShelf_relativelayout,
                            magazineFragment);
                    beginTransaction.commit();
                }
            }
        });

        bookBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                magazineBtn
                        .setBackgroundResource(R.drawable.offline_left_unpitchup);
                magazineBtn.setTextColor(normalColor);
                bookBtn.setBackgroundResource(R.drawable.offline_right_pitchup);
                bookBtn.setTextColor(checkColor);

                if (isChanged != isBook) {
                    Log.e("onclick:bookbtn", "" + isChanged);
                    isChanged = isBook;
                    bookFragment = new BookShelfFragment();
                    FragmentTransaction beginTransaction = getFragmentManager()
                            .beginTransaction();
                    beginTransaction.replace(R.id.offline_bookShelf_relativelayout,
                            bookFragment);
                    beginTransaction.commit();
                }
            }
        });
        return mView;
    }

    private void initPage() {
        bookFragment = new BookShelfFragment();
        FragmentTransaction beginTransaction = getFragmentManager()
                .beginTransaction();
        beginTransaction.add(R.id.offline_bookShelf_relativelayout,
                bookFragment);
        beginTransaction.commit();
        isChanged = isBook;
    }
}
