package com.theory.emhwang.cachedbitmap.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.theory.emhwang.cachedbitmap.R;
import com.theory.emhwang.cachedbitmap.adapter.ImageListAdapter;
import com.theory.emhwang.cachedbitmap.adapter.ImageRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

// https://javacan.tistory.com/entry/android-image-cache-implementation
// https://developer.android.com/topic/performance/graphics/cache-bitmap
// https://meylady.tistory.com/49
// https://ccdev.tistory.com/33   -> LruCache의 제한 갯수/사이즈
// http://blog.naver.com/PostView.nhn?blogId=huewu&logNo=110142842611  -> 좋은 설명
// https://ccdev.tistory.com/2  -> Bitmap 다루기 1
// TODO : https://www.androidpub.com/2426245    -> Bitmap 다루기 2 (좋은 설명)

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private Context mContext;

    /**
     * RadioGroup 객체
     */
    private RadioGroup mRgGroup;

    /**
     * ListView 객체
     */
    private ListView mLvView;

    private ImageListAdapter mLvAdapter;

    /**
     * RecyclerView 객체
     */
    private RecyclerView mRvView;

    private ImageRecyclerAdapter mRvAdapter;

    private List<String> mResponse;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        initView();

        setDataList();
        mLvAdapter = new ImageListAdapter(mContext);
        mLvView.setAdapter(mLvAdapter);

        mRvAdapter = new ImageRecyclerAdapter(mContext);
        mRvView.setLayoutManager(new LinearLayoutManager(mContext));
        mRvView.setAdapter(mRvAdapter);

        loadData(true);
    }


    @Override
    public void onCheckedChanged(final RadioGroup group, final int checkedId) {
        switch (checkedId) {
            // ListView 로 호출하기
            case R.id.rb_lv:
                loadData(true);
                break;
            // RecyclerView 로 호출하기
            case R.id.rb_rv:
                loadData(false);
                break;
            default:
                break;
        }
    }

    /**
     * 뷰 초기화하기
     */
    private void initView() {
        mLvView = findViewById(R.id.lv_view);
        mRvView = findViewById(R.id.rv_view);

        mRgGroup = findViewById(R.id.rg_view);
        mRgGroup.setOnCheckedChangeListener(this);
    }

    /**
     * 데이터 리스트 추가하기
     */
    private void setDataList() {
        mResponse = new ArrayList<>();
        mResponse.add("https://pds.joins.com/news/component/htmlphoto_mmdata/201902/14/4cfe5c49-facc-4f98-8955-23772078dfc2.jpg");
        mResponse.add("https://img.insight.co.kr/static/2018/03/09/700/ni507b50q74oqy7b47xb.jpg");
        mResponse.add("http://image.kyobobook.co.kr/newimages/giftshop_new/goods/400/1580/hot1570711756884.jpg");
        mResponse.add("https://developer.android.com/assets/images/android_logo.png");
    }

    /**
     * 데이터 불러오기
     * (간단한 이미지 URL로 테스트)
     */
    private void loadData(final boolean isListView) {
        if (isListView) {
            setListView(mResponse);
        } else {
            setRecyclerView(mResponse);
        }
    }

    /**
     * ListView에 데이터 셋팅하기
     */
    private void setListView(final List<String> response) {
        mLvView.setVisibility(View.VISIBLE);
        mRvView.setVisibility(View.GONE);

        mLvAdapter.clearAll();
        mLvAdapter.addAll(response);
    }

    /**
     * RecyclerView에 데이터 셋팅하기
     */
    private void setRecyclerView(final List<String> response) {
        mRvView.setVisibility(View.VISIBLE);
        mLvView.setVisibility(View.GONE);

        mRvAdapter.clearAll();
        mRvAdapter.addAll(response);
    }

}
// Picasso vs Glide : https://gun0912.tistory.com/19
