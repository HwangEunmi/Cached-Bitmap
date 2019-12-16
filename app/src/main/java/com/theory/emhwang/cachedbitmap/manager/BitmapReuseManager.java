package com.theory.emhwang.cachedbitmap.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.URL;

public class BitmapReuseManager {

    private static Context mContext;

    private static class BitmapReuseManagerHolder {

        public static final BitmapReuseManager MANAGER = new BitmapReuseManager();
    }

    public static BitmapReuseManager getInstance(final Context context) {
        mContext = context;
        return BitmapReuseManagerHolder.MANAGER;
    }

    /**
     * 사용한 Bitmap을 재활용해서 사용하기 (이미 View에 셋팅했던)
     * 
     * @param useBitmap : 사용한 Bitmap (재활용할)
     * @param url : 새로 불러올 URL (새로운 Bitmap이 될)
     */
    public Bitmap reuseBitmap(final Bitmap useBitmap, final URL url) {
        Bitmap newBitmap = null;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(url.openStream(), null, options);
            if (canUseForInBitmap(useBitmap, options)) {
                // 재활용이 가능한 경우
                options.inBitmap = useBitmap;
            }
            options.inJustDecodeBounds = false;
            newBitmap = BitmapFactory.decodeStream(url.openStream(), null, options);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return newBitmap;
    }

    /**
     * 크기를 비교하여 Bitmap을 재활용할 수 있는지에 대한 여부 판단하기
     * 
     * @param useBitmap : 사용한 Bitmap (재활용할)
     * @param options : 새로운 Bitmap의 Options
     * @return
     */
    private boolean canUseForInBitmap(final Bitmap useBitmap, final BitmapFactory.Options options) {
        // API 4.4 이상부터 가능
        int width = options.outWidth / options.inSampleSize; // 이미지 폭
        int height = options.outHeight / options.inSampleSize; // 이미지 높이
        int byteCount = width * height * getBytesPerPixel(useBitmap.getConfig());

        try {
            return byteCount <= useBitmap.getAllocationByteCount();
        } catch (final NullPointerException e) {
            return byteCount <= useBitmap.getHeight() * useBitmap.getRowBytes();
        }
    }

    // TODO : 이것에 대해서 살펴보기
    private int getBytesPerPixel(Bitmap.Config config) {
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        int bytesPerPixel = 0;
        switch (config) {
            case ALPHA_8:
                bytesPerPixel = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                bytesPerPixel = 2;
                break;
            case ARGB_8888:
            default:
                bytesPerPixel = 4;
                break;
        }
        return bytesPerPixel;
    }

}
// [2] 사용한 Bitmap을 재활용해서 성능저하 피하기
// 요즘 모바일 앱을보면 이미지를 정말 많이 사용하는데, 이는 GC때문에 자주 오랫동안 끊기게 되는 원인이 된다.
// 이렇게 임시로 배정되는 Bitmap 때문에 생기는 성능저하를 피하는데 좋은것을 살펴본다.
//
// Heap에서 가장 큰 메모리블럭은 거의 항상 Bitmap이다. 그에따라 종종 새로 배정되는 Bitmap이 들어갈 공간을 찾는 문제가 생긴다.
// 그럼 그 공간을 확보하기 위해 GC 이벤트가 계속해서 발생하게 될 것이다.
// 이런식으로 이미지 로딩중에 발생하는 GC 이벤트 때문에 앱 성능이 빠르게 저하되는 것이다.
//
// 이를 해결하는 방법은 다음과 같다. 즉, Object Pool의 개념을 Bitmap에도 적용시키면 된다.
// 메모리 급변을 일으키는 데이터 타입은 Object Pool로 해결한다.
// 그러니까 객체 사용을 완료한 후, 메모리 힙에 반납하기보다 사용할 수 있는 객체 리스트(Object Pool)에 참조를 남겨놓는 것이다.
// 이로써 다시 같은 타입의 새 객체가 필요해지면 메모리 힙에 새로 공간을 요구하는것보다, Object Pool에 이미 존재하는 객체를 불러온다.
// 이것을 Bitmap에도 적용시키는것이다.
//
// 안드로이드 이미지 관련 API는 이미지 데이터가 들어오면 디폴트로 메모리 힙에 Bitmap을 생성하도록 되어있다.
// 하지만 이것도 방법이 있다.
// Bitmap을 완전히 새로 만들기보다 디코더한테 이미 있는 메모리로 Bitmap을 로드하라고 지정해주는 것이다.
//
// Bitmap 옵션 객체의 inBitmap 속성을 사용하면 된다.
// 이미 있는 Bitmap에 inBitmap 속성을 지정하면 디코드나 로드를 할 때, 들어오는 픽셀 데이터를 이미 있는 Bitmap에 저장한다. (메모리 힙에서 새로 객체를 배정받지 않고)
//
// 이로 인해 사용자가 사진 10000개쯤을 아무리 열심히 스크롤해서 봐도 따로 Bitmap 10000개를 생성할 필요가 없게 되는 것이다.
// 그냥 동시에 볼 수 있는 최대 한도의 Bitmap을 배정하고 화면에 보여줄때는 이것들을 재활용하면 된다.
//
// 물론 이 테크닉도 사용상 주의할 점은 있다. 우선 재활용되는 Bitmap은 기존 Bitmap의 물리적 크기에 따른 제약이 있다.
// 무조건 기존 Bitmap이 로드하려는 Bitmap보다 크기가 같거나 더 커야 한다. (안그러면 오류가 발생한다.)
//
// 또 한가지가 있다. 자주 쓰는 사이즈와 포맷의 Bitmap을 Object Pool에 등록시켜 뒀다가 앱 내내 활용하면 메모리 변동도 적고, 메모리 파편화도 적고 좋다.
//
// 사실 이런 문제가 예전부터 있었기 때문에 Glide 라이브러리에는 이미 이런 Bitmap 재활용 담당 코드가 있고, 안드로이드에선 Bitmap 사용을 훨씬 편하게 해주는 기능들이 많다.
//  물론 이러한 이론적인것들은 다 알아야 하지만, 실제 개발을 할땐 최대한 라이브러리를 사용하는 것이 좋다.
// 참고 URL : https://blog.mindorks.com/how-to-use-bitmap-pool-in-android-56c71a55533c
