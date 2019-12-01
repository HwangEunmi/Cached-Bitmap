package com.theory.emhwang.cachedbitmap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.theory.emhwang.cachedbitmap.adapter.view.ImageListViewHolder;
import com.theory.emhwang.cachedbitmap.manager.ImageMemoryCacheManager;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends BaseAdapter {

    private Context mContext;

    /**
     * Memory Cache 이미지 매니저
     */
    private ImageMemoryCacheManager mImageMemoryCacheManager;

    private List<String> mList;


    public ImageListAdapter(final Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
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

    @Override
    public int getCount() {
        return mList.size() * 10;
    }

    @Override
    public Object getItem(final int position) {
        return mList.get(position % 3);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final String data = mList.get(position%3);
        View rowView = convertView;
        ImageListViewHolder viewHolder = null;
        if (rowView == null) {
            viewHolder = new ImageListViewHolder(mContext);
            rowView = viewHolder;
        } else {
            viewHolder = (ImageListViewHolder) rowView;
        }

        // 1. Memory Cache에 저장되어있는 데이터 있는지 확인 (Recycler)
        Bitmap bitmap = mImageMemoryCacheManager.getBitmapFromMemoryCache(data);
        if (bitmap != null) {
            Log.d("THEEND", "Recycler");
            viewHolder.setBitmapToImageView(bitmap);
        } else {
            Log.d("THEEND", "New");
            // 1-2. 해당 URL을 Memory Cache에 저장하기
            viewHolder.loadImageOfUrl(data);
        }
        viewHolder.setTextView("텍스트");

        return rowView;
    }

}
