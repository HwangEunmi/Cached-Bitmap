package com.theory.emhwang.cachedbitmap.adapter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.theory.emhwang.cachedbitmap.R;
import com.theory.emhwang.cachedbitmap.listener.IUrlDownloadCallbak;
import com.theory.emhwang.cachedbitmap.manager.ImageMemoryCacheManager;
import com.theory.emhwang.cachedbitmap.manager.ImageResizeManager;
import com.theory.emhwang.cachedbitmap.manager.UrlDownloadManager;

import java.net.URL;

public class ImageRecyclerViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    private ImageView mIvView;

    private TextView mTvView;


    public ImageRecyclerViewHolder(final Context context, final View itemView) {
        super(itemView);
        this.mContext = context;
        initView(itemView);
    }

    /**
     * 뷰 초기화하기
     */
    private void initView(final View view) {
        mIvView = view.findViewById(R.id.iv_view);
        mTvView = view.findViewById(R.id.tv_view);
    }

    /**
     * 뷰 셋팅하기
     */
    public void setData(final String imageUrl) {
        final UrlDownloadManager urlDownloadManager = new UrlDownloadManager(mContext, true, imageUrl, true, new IUrlDownloadCallbak() {
            @Override
            public void onSuccess(final Object result, final int position) {
                final URL url = (URL) result;
                final ImageResizeManager manager = new ImageResizeManager(mContext, url, mIvView, new ImageResizeManager.IImageResizeResultListener() {
                    @Override
                    public void onImageResize(final Bitmap bitmap) {
                        setBitmapToImageView(bitmap);

                        // 해당 URL을 Memory Cache에 저장하기
                        ImageMemoryCacheManager.getInstance(mContext).addBitmapToMemoryCache(imageUrl, bitmap);
                    }
                });
                manager.executeOnExecutor();
            }

            @Override
            public void onFail(final String code, final String msg) {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
        urlDownloadManager.executeOnExecutor();

        mTvView.setText("텍스트");
    }

    /**
     * 이미지뷰에 비트맵 셋팅하기
     *
     * @param bitmap : Bitmap 객체
     */
    public void setBitmapToImageView(final Bitmap bitmap) {
        mIvView.setImageBitmap(bitmap);
    }

}