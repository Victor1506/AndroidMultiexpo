package ru.victor.androidmultiexpo.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by Виктор on 27.03.2016.
 */
public class OverlayImageSingleton {
    private static OverlayImageSingleton ourInstance = new OverlayImageSingleton();

    public static OverlayImageSingleton getInstance() {
        return ourInstance;
    }

    private OverlayImageSingleton() {
    }

    public Bitmap createNewOverlayImage(Bitmap firstImage, Bitmap secondImage, int firstRotateAngle,
                                        int secondRotateAngle, int firstImageAlpha, int secondImageAlpha) {
        Bitmap newBitmap = null;

        int w;
        if (firstImage.getWidth() <= secondImage.getWidth()) {
            w = firstImage.getWidth();
        } else {
            w = secondImage.getWidth();
        }

        int h;
        if (firstImage.getHeight() <= secondImage.getHeight()) {
            h = firstImage.getHeight();
        } else {
            h = secondImage.getHeight();
        }

        Bitmap newFirstImage = Bitmap.createScaledBitmap(rotateBitmap(firstImage,
                firstRotateAngle), w, h, false);
        Bitmap newSecondImage = Bitmap.createScaledBitmap(rotateBitmap(secondImage,
                secondRotateAngle), w, h, false);

        Bitmap.Config config = newFirstImage.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        newBitmap = Bitmap.createBitmap(w, h, config);
        Canvas newCanvas = new Canvas(newBitmap);

        Paint paint = new Paint();
        paint.setAlpha(secondImageAlpha);
        newCanvas.drawBitmap(newSecondImage, 0, 0, paint);

        paint.setAlpha(firstImageAlpha);
        newCanvas.drawBitmap(newFirstImage, 0, 0, paint);

        return newBitmap;
    }

    //поворот изображения на указанный градус
    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
