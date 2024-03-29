package com.theory.emhwang.cachedbitmap.manager;

import java.io.IOException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageResizeManager {

    /**
     * Context 객체
     */
    private Context mContext;

    /**
     * URL 객체 (Image)
     */
    private URL mUrl;

    /**
     * ImageView 객체
     */
    private ImageView mView;

    /**
     * Progress Dialog 객체
     */
    private ProgressDialog mDialog;

    /**
     * Image Resize 결과 리스너
     */
    private IImageResizeResultListener mListener;

    public ImageResizeManager(final Context context,
                              final URL url,
                              final ImageView view,
                              final IImageResizeResultListener listener) {
        this.mContext = context;
        this.mUrl = url;
        this.mView = view;
        this.mListener = listener;
        mDialog = new ProgressDialog(mContext, 0);
    }

    /**
     * Manager 실행하기
     */
    public void executeOnExecutor() {
        final ImageResizeOperation operation = new ImageResizeOperation();
        operation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl);
    }

    /**
     * Image Resize AsyncTask
     */
    private class ImageResizeOperation extends AsyncTask<URL, URL, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mDialog != null && !((Activity)mContext).isFinishing()
                && !((Activity)mContext).isDestroyed()
                && !mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected Bitmap doInBackground(final URL... urls) {
            return runImageResize(urls[0]);
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mDialog != null && !((Activity)mContext).isFinishing()
                && !((Activity)mContext).isDestroyed()
                && mDialog.isShowing()) {
                mDialog.cancel();
            }
            Log.d("THEEND", "Return bitmap: " + bitmap);
            if (mListener != null) {
                mListener.onImageResize(bitmap);
            }
        }

        /**
         * Image Resize 시작하기
         * <p>
         * (Picasso, Glide 등 라이브러리는 자동으로 리사이즈를 해준다. 참고 URL : https://gun0912.tistory.com/19)
         */
        private Bitmap runImageResize(final URL url) {
            // Image를 ImageView 크기에 맞춰서 리사이즈 시킨다.
            Bitmap bitmap = null;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            int afterSize = 1;

            try {
                // 이미지를 메모리에 올려놓지 않고 해상도만 알아낼 수 있다.
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(url.openStream(), null, options);

                int height = options.outHeight; // 이미지 높이
                int width = options.outWidth; // 이미지 폭

                // 이미지의 크기 조절이 필요한 경우
                // Ex. inSampleSize가 2인 경우, 원본의 너비/높이 1/2 사이즈 Bitmap을 리턴한다.
                if (height > mView.getHeight() || width > mView.getWidth()) {
                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    if (mView.getHeight() == 0 || mView.getWidth() == 0) {
                        return null;
                    }
                    while (((halfHeight / afterSize) >= mView.getHeight())
                           && ((halfWidth / afterSize) >= mView.getWidth())) {
                        // (2의 지수로 만드는 이유는 2의 지수가 디코딩 속도가 가장 빠르다고 함)
                        afterSize *= 2;
                    }
                }

                // 예를들어 height가 50, mView.getHeight()가 10인 경우
                // halfHeight는 25,
                // 25 / 1 즉, 25가 getHeight보다 크므로 *= 2
                // afterSize는 2

                // 25/2 즉, 12가 getHeight보다 크므로 *= 2
                // afterSize는 4

                // 즉 원본의 1/4  근사치로 줄여야 함

                options.inJustDecodeBounds = false;
                options.inSampleSize = afterSize;

                bitmap = BitmapFactory.decodeStream(url.openStream(), null, options);

            } catch (final IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

    }

    /**
     * Image Resize 결과 리스너
     */
    public interface IImageResizeResultListener {

        public void onImageResize(final Bitmap bitmap);
    }

}
// [1] 큰 이미지를 (효율적으로) 화면에 보여주기
// 고해상도의 이미지를 불러와 그대로 메모리에 넣고 화면에 보여주는 것은 OutOfMemory 발생 위험이 있다.
// -> 원래 이미지의 해상도와 화면에 보이려는 ImageView의 해상도를 알아내어 품질을 떨어트리면 된다.
// -> 즉, 이미지의 해상도를 ImageView의 크기만큼 해상도를 떨어트린다.
