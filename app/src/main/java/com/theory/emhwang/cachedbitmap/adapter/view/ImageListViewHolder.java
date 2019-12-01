package com.theory.emhwang.cachedbitmap.adapter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.theory.emhwang.cachedbitmap.R;
import com.theory.emhwang.cachedbitmap.listener.IUrlDownloadCallbak;
import com.theory.emhwang.cachedbitmap.manager.ImageMemoryCacheManager;
import com.theory.emhwang.cachedbitmap.manager.ImageResizeManager;
import com.theory.emhwang.cachedbitmap.manager.UrlDownloadManager;

import java.net.URL;

public class ImageListViewHolder extends FrameLayout {

    private Context mContext;

    private ImageView mIvView;

    private TextView mTvView;

    public ImageListViewHolder(final Context context) {
        this(context, null);
    }

    public ImageListViewHolder(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageListViewHolder(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    /**
     * 뷰 초기화하기
     */
    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_image_main, this);
        mIvView = findViewById(R.id.iv_view);
        mTvView = findViewById(R.id.tv_view);
    }

    /**
     * 해당 URL의 이미지 로드하기
     *
     * @param imageUrl : Image의 URL
     */
    public void loadImageOfUrl(final String imageUrl) {
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
    }

    /**
     * 이미지뷰에 비트맵 셋팅하기
     *
     * @param bitmap : Bitmap 객체
     */
    public void setBitmapToImageView(final Bitmap bitmap) {
        mIvView.setImageBitmap(bitmap);
    }

    /**
     * 텍스트뷰에 텍스트 셋팅하기
     *
     * @param text : 텍스트
     */
    public void setTextView(final String text) {
        mTvView.setText(text);
    }

}

