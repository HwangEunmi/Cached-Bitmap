package com.theory.emhwang.cachedbitmap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theory.emhwang.cachedbitmap.R;
import com.theory.emhwang.cachedbitmap.adapter.view.ImageRecyclerViewHolder;
import com.theory.emhwang.cachedbitmap.manager.ImageMemoryCacheManager;

import java.util.ArrayList;
import java.util.List;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerViewHolder> {

    private Context mContext;

    /**
     * Memory Cache 이미지 매니저
     */
    private ImageMemoryCacheManager mImageMemoryCacheManager;

    private List<String> mList;

    public ImageRecyclerAdapter(final Context context) {
        this.mContext = context;
        mList = new ArrayList<>();
        mImageMemoryCacheManager = ImageMemoryCacheManager.getInstance(mContext);
    }

    /**
     * List 추가하기
     */
    public void addAll(final List<String> list) {
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * List 지우기
     */
    public void clearAll() {
        this.mList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageRecyclerViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.view_image_main, parent, false);
        return new ImageRecyclerViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageRecyclerViewHolder holder, final int position) {
        final String data = mList.get(position%3);
        // 1. Memory Cache에 저장되어 있는 데이터 있는지 확인 (Recycler)
        Bitmap bitmap = mImageMemoryCacheManager.getBitmapFromMemoryCache(data);
        if(bitmap != null) {
            Log.d("THEEND", "Recycler");
            holder.setBitmapToImageView(bitmap);
        }else {
            Log.d("THEEND", "New");
            // 1-2. 해당 URL를 Memory Cache에 저장하기
            holder.setData(data);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() * 10;
    }
}
