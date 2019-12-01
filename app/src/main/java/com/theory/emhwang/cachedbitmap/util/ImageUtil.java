package com.theory.emhwang.cachedbitmap.util;

// https://itmining.tistory.com/17
// https://www.androidpub.com/2426245
// https://developer.android.com/topic/performance/graphics/load-bitmap#load-bitmap

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.widget.ImageView;

import java.io.InputStream;

// https://aroundck.tistory.com/59   -> Bitmap Resize 설명
public class ImageUtil {
    // [1] 큰 이미지를 (효율적으로) 화면에 보여주기
    // 고해상도의 이미지를 불러와 그대로 메모리에 넣고 화면에 보여주는 것은 OutOfMemory 발생 위험이 있다.
    // -> 원래 이미지의 해상도와 화면에 보이려는 ImageView의 해상도를 알아내어 품질을 떨어트리면 된다.
    // -> 즉, 이미지의 해상도를 ImageView의 크기만큼 해상도를 떨어트린다.

    /**
     * ImageView와 이미지의 비율을 계산하여
     * 뷰에 셋팅하기 좋은 적절한 이미지 사이즈로 리턴하기
     *
     * @param context : Context 객체
     * @param object  : 이미지 객체 (세가지 타입)
     * @param view    : ImageView 뷰 객체
     * @return
     */
    // https://jamssoft.tistory.com/57
    public static int calculateImageSize(final Context context, final InputStream object, final ImageView view) {
        // 해상도를 계산할 변수값
        int sampleSize = 1;

        // 1. ImageView의 높이/폭을 알아낸다.
        final Pair<Integer, Integer> vScale = getSizeOfImageView(view);
        int vHeight = vScale.first;
        int vWidth = vScale.second;

        // 2. 이미지의 높이/폭을 알아낸다.
        final Pair<Integer, Integer> iScale = getSizeOfImageObject(context, object);
        int iHeight = iScale.first;
        int iWidth = iScale.second;

        // 이미지의 크기 조절이 필요한 경우
        if (iHeight > vHeight || iWidth > vWidth) {
            final int halfHeight = iHeight / 2;
            final int halfWidth = iWidth / 2;

            // https://itmining.tistory.com/17
            // https://developer.android.com/topic/performance/graphics/load-bitmap#load-bitmap
            // TODO : 여기 이해 안됨
            // (2의 지수로 만드는 이유는 2의 지수가 디코딩 속도가 가장 빠르다고 함)
            // https://aroundck.tistory.com/59
            while (((halfHeight / sampleSize) >= vHeight)
                    && ((halfWidth / sampleSize) >= vWidth)) {
                // 뷰의 높이/폭 보다 이미지의 높이/폭 이 크게
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }

//    public static int calculateImageSize(final Context context, final ImageView view) {
//        // 해상도를 계산할 변수값
//        int sampleSize = 1;
//
//        // 1. ImageView의 높이/폭을 알아낸다.
//        final Pair<Integer, Integer> vScale = getSizeOfImageView(view);
//        int vHeight = vScale.first;
//        int vWidth = vScale.second;
//
//        // 2. 이미지의 높이/폭을 알아낸다.
//        final Pair<Integer, Integer> iScale = getSizeOfImageObject(context, object);
//        int iHeight = iScale.first;
//        int iWidth = iScale.second;
//
//        // 이미지의 크기 조절이 필요한 경우
//        if (iHeight > vHeight || iWidth > vWidth) {
//            final int halfHeight = iHeight / 2;
//            final int halfWidth = iWidth / 2;
//
//            // https://itmining.tistory.com/17
//            // https://developer.android.com/topic/performance/graphics/load-bitmap#load-bitmap
//            // TODO : 여기 이해 안됨
//            // (2의 지수로 만드는 이유는 2의 지수가 디코딩 속도가 가장 빠르다고 함)
//            // https://aroundck.tistory.com/59
//            while (((halfHeight / sampleSize) >= vHeight)
//                    && ((halfWidth / sampleSize) >= vWidth)) {
//                // 뷰의 높이/폭 보다 이미지의 높이/폭 이 크게
//                sampleSize *= 2;
//            }
//        }
//        return sampleSize;
//    }

//    public static int calculateImageSize(final Context context, final Object object, final ImageView view) {
//        // 해상도를 계산할 변수값
//        int sampleSize = 1;
//
//        // 1. ImageView의 높이/폭을 알아낸다.
//        final Pair<Integer, Integer> vScale = getSizeOfImageView(view);
//        int vHeight = vScale.first;
//        int vWidth = vScale.second;
//
//        // 2. 이미지의 높이/폭을 알아낸다.
//        final Pair<Integer, Integer> iScale = getSizeOfImageObject(context, object);
//        int iHeight = iScale.first;
//        int iWidth = iScale.second;
//
//        // 이미지의 크기 조절이 필요한 경우
//        if (iHeight > vHeight || iWidth > vWidth) {
//            final int halfHeight = iHeight / 2;
//            final int halfWidth = iWidth / 2;
//
//            // https://itmining.tistory.com/17
//            // https://developer.android.com/topic/performance/graphics/load-bitmap#load-bitmap
//            // TODO : 여기 이해 안됨
//            while (((halfHeight / sampleSize) >= vHeight)
//                    && ((halfWidth / sampleSize) >= vWidth)) {
//                // 뷰의 높이/폭 보다 이미지의 높이/폭 이 크게
//                sampleSize *= 2;
//            }
//        }
//        return sampleSize;
//    }


    /**
     * 해당 ImageView의 높이/폭을 알아내기
     *
     * @param view : ImageView 뷰 객체
     * @return
     */
    public static Pair<Integer, Integer> getSizeOfImageView(final ImageView view) {
        return Pair.create(view.getHeight(), view.getWidth());
    }


    /**
     * 해당 값의 이미지 높이/폭을 알아내기
     *
     * @param context : Context 객체
     * @param object  : 이미지 객체 (세가지 타입)
     * @return
     */
    public static Pair<Integer, Integer> getSizeOfImageObject(final Context context, final InputStream object) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        int height = 0;
        int width = 0;

        // object.mark(object.available());

        // 이미지를 메모리에 올려놓지 않고 해상도만 알아낼 수 있다.
        options.inJustDecodeBounds = true;

        getBitmapOfObject(object, options);
        height = options.outHeight; // 이미지 높이
        width = options.outWidth; // 이미지 폭

        return Pair.create(height, width);
    }

//    public static Pair<Integer, Integer> getSizeOfImageObject(final Context context, final Object object) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//
//        // 이미지를 메모리에 올려놓지 않고 해상도만 알아낼 수 있다.
//        options.inJustDecodeBounds = true;
//
//        getBitmapOfObject(context, object, options);
//        int height = options.outHeight; // 이미지 높이
//        int width = options.outWidth; // 이미지 폭
//
//        return Pair.create(height, width);
//    }


    /**
     * 해당 값의 Bitmap을 리턴한다.
     *
     * @param object  : 이미지 객체 (세가지 타입)
     *                1. (이미지가 셋팅되어있는) ImageView같은 View 의 id값 또는 Image의 id값 일 경우
     *                2. 파일 경로 값일 경우
     *                3. URL의 InputStream으로부터 Bitmap을 생성할 경우
     * @param options : BitmapFactory.Options 객체
     * @return
     */
    private static Bitmap getBitmapOfObject(final InputStream object, final BitmapFactory.Options options) {
        Bitmap bitmap = null;

        // TODO : 이미지가 셋팅되어있는 ImageView도 가능한지 확인하기 (1번일 경우)
        // 1. (이미지가 셋팅되어있는) ImageView같은 View 의 id값 또는 Image의 id값 일 경우
        bitmap = BitmapFactory.decodeStream(object, null, options);

        return bitmap;
    }

//    private static Bitmap getBitmapOfObject(final Context context, final Object object, final BitmapFactory.Options options) {
//        Bitmap bitmap = null;
//
//        // TODO : 이미지가 셋팅되어있는 ImageView도 가능한지 확인하기 (1번일 경우)
//        // 1. (이미지가 셋팅되어있는) ImageView같은 View 의 id값 또는 Image의 id값 일 경우
//        if (object instanceof Integer) {
//            bitmap = BitmapFactory.decodeResource(context.getResources(), (int) object, options);
//            // 2. 파일 경로 값일 경우
//        } else if (object instanceof String) {
//            bitmap = BitmapFactory.decodeFile((String) object, options);
//            // 3. InputStream으로부터 Bitmap을 생성할 경우
//        } else if (object instanceof InputStream) {
//            bitmap = BitmapFactory.decodeStream((InputStream) object, null, options);
//        }
//        return bitmap;
//    }


}
