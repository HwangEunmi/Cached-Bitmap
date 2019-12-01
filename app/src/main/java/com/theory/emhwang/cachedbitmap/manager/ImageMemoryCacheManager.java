package com.theory.emhwang.cachedbitmap.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageMemoryCacheManager {

    private static Context mContext;


    // 메모리 캐시는 LruCache를 사용한다.
    // (디스크 캐시는 DiskLruCache를 사용)
    private LruCache<String, Bitmap> mMemoryCache;


    private static class ImageMemoryCacheManagerHolder {
        public static final ImageMemoryCacheManager MANAGER = new ImageMemoryCacheManager();
    }

    public static ImageMemoryCacheManager getInstance(final Context context) {
        mContext = context;
        return ImageMemoryCacheManagerHolder.MANAGER;
    }


    public ImageMemoryCacheManager() {
        // 메모리 캐시가 사용할 수 있는 가용 메모리를 설정한다.
        int memoryClass = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int maxSize = 1024 * 1024 * memoryClass / 8; // 앱이 사용할 수 있는 메모리의 1/8 을 사용한다.

        mMemoryCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(final String key, final Bitmap value) {
                return value.getByteCount();
            }
        };
    }


    /**
     * Bitmap을 Memory Cache에 저장한다.
     *
     * @param key    : 키값 (LruCache는 내부적으로 LinkedHashMap 를 사용)
     * @param bitmap : Bitmap 객체
     */
    public void addBitmapToMemoryCache(final String key, final Bitmap bitmap) {
        if ((getBitmapFromMemoryCache(key) != null) || bitmap == null) {
            return;
        }
        mMemoryCache.put(key, bitmap);
    }

    /**
     * 해당하는 Key값의 Bitmap을 리턴한다.
     *
     * @param key : 키값
     * @return
     */
    public Bitmap getBitmapFromMemoryCache(final String key) {
        return mMemoryCache.get(key);
    }

}
