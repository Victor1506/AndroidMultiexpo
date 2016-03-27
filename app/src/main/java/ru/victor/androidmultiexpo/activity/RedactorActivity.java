package ru.victor.androidmultiexpo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.edmodo.cropper.CropImageView;

import ru.victor.androidmultiexpo.R;
import ru.victor.androidmultiexpo.helper.Constants;
import ru.victor.androidmultiexpo.helper.FileSaveHelper;
import ru.victor.androidmultiexpo.helper.OverlayImageSingleton;
import ui.view.EditPhotoBar;


public class RedactorActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, EditPhotoBar.WorkModeListener {

    private ImageView mBigRightImageView;
    private ImageView mBigLeftImageView;
    private CropImageView mCropperImage;
    private Bitmap mNewOverlayImageBitmap;
    private Bitmap mLeftImageBitmap;
    private Bitmap mRightImageBitmap;
    private SeekBar mImageProgressSeekBar;
    private int mFocusedImage = 1;
    private int mLeftMiniImageProgressAlpha = 255;
    private int mRightMiniImageProgressAlpha = 255;
    private int mCropperImageNumber = 0;
    private int mLeftBigImageRotateAngle;
    private int mRightBigImageRotateAngle;
    private Bitmap mLeftCropperImageBitmap = null;
    private Bitmap mRightCropperImageBitmap = null;
    private LinearLayout mLeftMiniImageLinearLayout;
    private LinearLayout mRightMiniImageLinearLayout;
    private EditPhotoBar mEditPhotoBar;
    private OverlayImageSingleton mImageSingleton;

    private ImageView mMiniLeftImageView;
    private ImageView mMiniRightImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redactor);
        GettingTwoImagesActivity.showProgress("обработка изображений", this);

        mImageProgressSeekBar = (SeekBar) findViewById(R.id.imageAlphaSeekBar);
        mImageProgressSeekBar.setOnSeekBarChangeListener(this);

        mMiniLeftImageView = (ImageView) findViewById(R.id.MiniFrameFirst);
        mMiniRightImageView = (ImageView) findViewById(R.id.MiniFrameSecond);
        mMiniLeftImageView.setOnClickListener(this);
        mMiniRightImageView.setOnClickListener(this);

        mLeftMiniImageLinearLayout = (LinearLayout) findViewById(R.id.leftMiniImageLinearLayout);
        mRightMiniImageLinearLayout = (LinearLayout) findViewById(R.id.rightMiniImageLinearLayout);

        mBigRightImageView = (ImageView) findViewById(R.id.bigRightImageView);
        mBigLeftImageView = (ImageView) findViewById(R.id.bigLeftImageView);

        mCropperImage = (CropImageView) findViewById(R.id.cropperImage);

        mImageSingleton = OverlayImageSingleton.getInstance();

        //добавление панели переключения режимов роботы с изображениями
        RelativeLayout containerEditPhotoBar = (RelativeLayout) findViewById(R.id.containerEditPhotoBar);
        mEditPhotoBar = new EditPhotoBar(this);
        mEditPhotoBar.setCustomObjectListener(this);
        containerEditPhotoBar.addView(mEditPhotoBar);

        setFocusOnLayout(true);
    }

    //в потоке получаем большые изображения
    private void getBitmapFromURL(int number) {
        final Intent intent = getIntent();

        switch (number) {
            case 1:
                Glide.with(this)
                        .load(intent.getStringExtra(Constants.FIRST_IMAGE_URI))
                        .asBitmap()
                        .override(Constants.IMAGE_MEDIUM_WIGHT,Constants.IMAGE_MEDIUM_HIGHT)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mLeftImageBitmap = resource;
                                mMiniLeftImageView.setImageBitmap(mLeftImageBitmap);
                                mBigLeftImageView.setImageBitmap(mLeftImageBitmap);
                                mBigLeftImageView.setImageAlpha(mLeftMiniImageProgressAlpha);
                                mBigLeftImageView.setRotation(mLeftBigImageRotateAngle);
                                mLeftCropperImageBitmap = mLeftImageBitmap;
                            }
                        });
                break;
            case 2:
                Glide.with(this)
                        .load(intent.getStringExtra(Constants.SECOND_IMAGE_URI))
                        .asBitmap()
                        .override(Constants.IMAGE_MEDIUM_WIGHT,Constants.IMAGE_MEDIUM_HIGHT)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mRightImageBitmap = resource;
                                mMiniRightImageView.setImageBitmap(mRightImageBitmap);
                                mBigRightImageView.setImageBitmap(mRightImageBitmap);
                                mBigRightImageView.setImageAlpha(mRightMiniImageProgressAlpha);
                                mBigRightImageView.setRotation(mRightBigImageRotateAngle);
                                mRightCropperImageBitmap = mRightImageBitmap;
                                GettingTwoImagesActivity.hideProgress();
                            }
                        });
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getBitmapFromURL(1);
        getBitmapFromURL(2);
        mCropperImageNumber = 0;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mEditPhotoBar.getCurrentMode() == Constants.ROTATE_MODE) {
            seekBar.setMax(4);
            switch (mFocusedImage) {
                case Constants.MINI_LEFT_IMAGE_FOCUS_NUMBER:
                    mLeftBigImageRotateAngle = progress * 90;
                    mBigLeftImageView.setRotation(mLeftBigImageRotateAngle);
                    break;
                case Constants.MINI_RIGHT_IMAGE_FOCUS_NUMBER:
                    mRightBigImageRotateAngle = progress * 90;
                    mBigRightImageView.setRotation(mRightBigImageRotateAngle);
                    break;
            }
        } else if (mEditPhotoBar.getCurrentMode() == Constants.TRANSPARENCY_MODE) {
            seekBar.setMax(255);
            switch (mFocusedImage) {
                case Constants.MINI_LEFT_IMAGE_FOCUS_NUMBER:
                    mLeftMiniImageProgressAlpha = progress;
                    mBigLeftImageView.setImageAlpha(mLeftMiniImageProgressAlpha);
                    break;
                case Constants.MINI_RIGHT_IMAGE_FOCUS_NUMBER:
                    mRightMiniImageProgressAlpha = progress;
                    mBigRightImageView.setImageAlpha(mRightMiniImageProgressAlpha);
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {
        setCropperImage();
        switch (v.getId()) {
            case R.id.MiniFrameFirst:
                mFocusedImage = Constants.MINI_LEFT_IMAGE_FOCUS_NUMBER;

                if (mEditPhotoBar.getCurrentMode() == Constants.CROPPER_MODE) {
                    mCropperImage.setImageBitmap(mLeftImageBitmap);
                    mCropperImageNumber = 1;
                }
                break;
            case R.id.MiniFrameSecond:
                mFocusedImage = Constants.MINI_RIGHT_IMAGE_FOCUS_NUMBER;
                if (mEditPhotoBar.getCurrentMode() == Constants.CROPPER_MODE) {
                    mCropperImage.setImageBitmap(mRightImageBitmap);
                    /*mCropperImage.setImageBitmap(mCreatingNewImage.getImageForCropper(mRightImageBitmap,
                            mRightBigImageRotateAngle, mRightMiniImageProgressAlpha));*/
                    mCropperImageNumber = 2;
                }
                break;
        }
        setFocusOnLayout(true);
    }

    private void setCropperImage() {
        switch (mCropperImageNumber) {
            case 1:
                mLeftCropperImageBitmap = mCropperImage.getCroppedImage();
                break;
            case 2:
                mRightCropperImageBitmap = mCropperImage.getCroppedImage();
                break;
        }
    }

    private Bitmap createOverlayImageBitmap() {

        if (mLeftCropperImageBitmap == null) {
            mLeftCropperImageBitmap = mLeftImageBitmap;
        }
        if (mRightCropperImageBitmap == null) {
            mRightCropperImageBitmap = mRightImageBitmap;
        }

        if (mCropperImageNumber != 0) {
            return mImageSingleton.createNewOverlayImage(mLeftCropperImageBitmap,
                    mRightCropperImageBitmap, mLeftBigImageRotateAngle, mRightBigImageRotateAngle,
                    mLeftMiniImageProgressAlpha, mRightMiniImageProgressAlpha);
        } else {
            return mImageSingleton.createNewOverlayImage(mLeftImageBitmap,
                    mRightImageBitmap, mLeftBigImageRotateAngle, mRightBigImageRotateAngle,
                    mLeftMiniImageProgressAlpha, mRightMiniImageProgressAlpha);
        }

    }

    public void overlayImageCropper() {
        Bitmap tempCropperBitmap = mImageSingleton.createNewOverlayImage(mLeftCropperImageBitmap,
                mRightCropperImageBitmap, mLeftBigImageRotateAngle, mRightBigImageRotateAngle,
                mLeftMiniImageProgressAlpha, mRightMiniImageProgressAlpha);
        mCropperImage.setImageBitmap(tempCropperBitmap);

    }

    public void setFocusOnLayout(Boolean isSetFocus) {
        mLeftMiniImageLinearLayout.setBackgroundColor(Constants.FOCUS_COLOR_GRAY);
        mRightMiniImageLinearLayout.setBackgroundColor(Constants.FOCUS_COLOR_GRAY);

        if (isSetFocus) {
            switch (mFocusedImage) {
                case Constants.MINI_LEFT_IMAGE_FOCUS_NUMBER:
                    mLeftMiniImageLinearLayout.setBackgroundColor(Constants.FOCUS_COLOR_WHITE);
                    break;
                case Constants.MINI_RIGHT_IMAGE_FOCUS_NUMBER:
                    mRightMiniImageLinearLayout.setBackgroundColor(Constants.FOCUS_COLOR_WHITE);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.share_menu, menu);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item_share:
                saveImage();
                break;
            case R.id.menu_item_save:
                FileSaveHelper fileSaveHelper = new FileSaveHelper(RedactorActivity.this);

                if (mEditPhotoBar.getCurrentMode() == Constants.CROPPER_MODE) {
                    mNewOverlayImageBitmap = mCropperImage.getCroppedImage();
                } else {
                    //получение обрезаного или полного изображения
                    mNewOverlayImageBitmap = createOverlayImageBitmap();
                }
                // сохранение полученного изображения на SD
                fileSaveHelper.saveImageOnSD(mNewOverlayImageBitmap);
                break;
        }
        return true;
    }

    private void saveImage() {
        FileSaveHelper fileSaveHelper = new FileSaveHelper(RedactorActivity.this);

        if (mEditPhotoBar.getCurrentMode() == Constants.CROPPER_MODE) {
            mNewOverlayImageBitmap = mCropperImage.getCroppedImage();
        } else {
            //получение обрезаного или полного изображения
            mNewOverlayImageBitmap = createOverlayImageBitmap();
        }

        // сохранение полученного изображения на SD
        fileSaveHelper.saveImageOnSD(mNewOverlayImageBitmap);
        // сохранение полученного изображения в Instagram
        fileSaveHelper.createInstagramIntent();
    }

    @Override
    public void onSelectedMode(int currentMode) {
        setCropperImage();
        mBigLeftImageView.setImageBitmap(mLeftCropperImageBitmap);
        mBigRightImageView.setImageBitmap(mRightCropperImageBitmap);
        switch (currentMode) {
            case Constants.ROTATE_MODE:
                rotateImage();
                break;
            case Constants.TRANSPARENCY_MODE:
                transparencyImage();
                break;
            case Constants.CROPPER_MODE:
                cropperImage();
                break;
        }
    }

    private void rotateImage() {
        setFocusOnLayout(true);
        mCropperImage.setVisibility(View.INVISIBLE);
        mImageProgressSeekBar.setVisibility(View.VISIBLE);
        mBigLeftImageView.setVisibility(View.VISIBLE);
        mBigRightImageView.setVisibility(View.VISIBLE);
    }

    private void transparencyImage() {
        setFocusOnLayout(true);
        mCropperImage.setVisibility(View.INVISIBLE);
        mImageProgressSeekBar.setVisibility(View.VISIBLE);
        mBigLeftImageView.setVisibility(View.VISIBLE);
        mBigRightImageView.setVisibility(View.VISIBLE);
    }

    private void cropperImage() {
        setFocusOnLayout(false);
        mBigLeftImageView.setVisibility(View.INVISIBLE);
        mBigRightImageView.setVisibility(View.INVISIBLE);
        mImageProgressSeekBar.setVisibility(View.INVISIBLE);
        mCropperImage.setVisibility(View.VISIBLE);

        //обрезка наложеного изображения
        overlayImageCropper();
    }
}
