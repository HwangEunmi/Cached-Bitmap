package com.theory.emhwang.cachedbitmap.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.theory.emhwang.cachedbitmap.listener.IUrlDownloadCallbak;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class UrlDownloadManager {

    private final Context mContext;

    private final String uri;

    private final boolean isImage;

    private final int position;

    private final IUrlDownloadCallbak listener;

    /**
     * 프로그레스 다이얼로그
     */
    private ProgressDialog progressDialog;

    public UrlDownloadManager(final Context context,
                              final boolean isProgress,
                              final String uri,
                              final boolean isImage,
                              final IUrlDownloadCallbak listener) {
        super();
        mContext = context;
        this.uri = uri;
        this.isImage = isImage;
        position = 0;
        this.listener = listener;
        if (isProgress) {
            progressDialog = new ProgressDialog(mContext, 0);
        }
    }

    public UrlDownloadManager(final Context context,
                              final boolean isProgress,
                              final String uri,
                              final boolean isImage,
                              final int position,
                              final IUrlDownloadCallbak listener) {
        super();
        mContext = context;
        this.uri = uri;
        this.isImage = isImage;
        this.position = position;
        this.listener = listener;
        if (isProgress) {
            progressDialog = new ProgressDialog(mContext, 0);
        }
    }

    public void executeOnExecutor() {
        final RequestOperation ro = new RequestOperation();
        ro.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class RequestOperation extends AsyncTask<String, String, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null && !((Activity)mContext).isFinishing()
                && !((Activity)mContext).isDestroyed()
                && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected Object doInBackground(final String... params) {
            return urlDownload(uri, isImage);
        }

        @Override
        protected void onPostExecute(final Object object) {
            super.onPostExecute(object);
            if (progressDialog != null && !((Activity)mContext).isFinishing()
                && !((Activity)mContext).isDestroyed()
                && progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            if (object != null) {
                if (listener != null) {
                    listener.onSuccess(object, position);
                }
            } else {
                if (listener != null) {
                    listener.onFail("", "");
                }
            }
        }

        /**
         * url로 된 이미지를 Bitmap으로 return
         *
         * @param filePath 파일 경로
         * @return Bitmap
         */
        private Object urlDownload(final String filePath, final boolean isImage) {
            // url로 된 이미지를 다운로드하여 return 한다.
            InputStream is = null;
            try {
                final URL url = new URL(filePath);
                final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                if (200 == conn.getResponseCode()) { // Success
                    is = conn.getInputStream();
                } else {
                    return null;
                }

                if (isImage) {
                    // Bitmap말고 InputStream 넘기기
                    // 이미지 리사이즈 처리를 하고 뷰에 셋팅하기 위해
                    return url;
                }
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}
