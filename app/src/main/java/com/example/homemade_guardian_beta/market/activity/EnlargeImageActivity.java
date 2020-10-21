package com.example.homemade_guardian_beta.market.activity;

import android.os.Bundle;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.homemade_guardian_beta.Main.activity.BasicActivity;
import com.example.homemade_guardian_beta.R;
import com.example.homemade_guardian_beta.Main.common.ViewPagerAdapter;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class EnlargeImageActivity extends BasicActivity {
    private ArrayList<String> ImageList = new ArrayList<>();            //게시물의 이미지 리스트
    private ViewPager Viewpager;                    //이미지들을 보여주기 위한 ViewPager 선언
    private ConstraintLayout ViewPagerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge_imagelist);

        ImageList = (ArrayList<String>) getIntent().getSerializableExtra("marketImage");

        String ViewpagerState = "Disable";
        if(ImageList != null) {
            Viewpager = findViewById(R.id.ViewPager);
            Viewpager.setAdapter(new ViewPagerAdapter(this, ImageList, ViewpagerState));
            CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(Viewpager);
        }else{
            ViewPagerLayout = (ConstraintLayout) findViewById(R.id.ViewPagerLayout);
            ViewPagerLayout.setVisibility(View.GONE);
        }
    }
}
