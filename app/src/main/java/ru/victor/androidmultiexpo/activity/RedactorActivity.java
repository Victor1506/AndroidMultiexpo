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
    private OverlayImageSingleton mOverlayImageSingleton;
    private ImageView mMiniLeftImageView;
    private ImageView mMiniRightImageView;
    private FileSaveHelper fileSaveHelper;

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

        //инициализация singleton для создания наложенных изображений
        mOverlayImageSingleton = OverlayImageSingleton.getInstance();

        //helper для сохранения изображений на SD или в Instagram
        fileSaveHelper = new FileSaveHelper(this);

        //добавление панели переключения режимов роботы с изображениями
        RelativeLayout containerEditPhotoBar = (RelativeLayout) findViewById(R.id.containerEditPhotoBar);
        mEditPhotoBar = new EditPhotoBar(this);
        mEditPhotoBar.setCustomObjectListener(this);
        containerEditPhotoBar.addView(mEditPhotoBar);

        //установка фокуса на выбраном окошке
        setFocusOnLayout(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadLeftImage();
        loadRightImage();
        mCropperImageNumber = 0;
    }

    //загрузка верхнего изображения из GettingTwoImageActivity
    private void loadLeftImage() {
        final Intent intent = getIntent();
        Glide.with(this)
                .load(intent.getStringExtra(Constants.FIRST_IMAGE_URI))
                .asBitmap()
                .override(Constants.IMAGE_MEDIUM_WIGHT, Constants.IMAGE_MEDIUM_HIGHT)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        setTopImage(resource);
                    }
                });
    }

    //загрузка нижнего изображения из GettingTwoImageActivity
    private void loadRightImage() {
        final Intent intent = getIntent();
        Glide.with(this)
                .load(intent.getStringExtra(Constants.SECOND_IMAGE_URI))
                .asBitmap()
                .override(Constants.IMAGE_MEDIUM_WIGHT, Constants.IMAGE_MEDIUM_HIGHT)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        setBottomImage(resource);
                    }
                });
    }

    // работа с верхним изображением из GettingTwoImageActivity
    private void setTopImage(Bitmap resource) {
        mLeftImageBitmap = resource;
        mMiniLeftImageView.setImageBitmap(mLeftImageBitmap);
        mBigLeftImageView.setImageBitmap(mLeftImageBitmap);
        mBigLeftImageView.setImageAlpha(mLeftMiniImageProgressAlpha);
        mBigLeftImageView.setRotation(mLeftBigImageRotateAngle);
        mLeftCropperImageBitmap = mLeftImageBitmap;
    }

    // работа с нижним изображением из GettingTwoImageActivity
    private void setBottomImage(Bitmap resource) {
        mRightImageBitmap = resource;
        mMiniRightImageView.setImageBitmap(mRightImageBitmap);
        mBigRightImageView.setImageBitmap(mRightImageBitmap);
        mBigRightImageView.setImageAlpha(mRightMiniImageProgressAlpha);
        mBigRightImageView.setRotation(mRightBigImageRotateAngle);
        mRightCropperImageBitmap = mRightImageBitmap;
        GettingTwoImagesActivity.hideProgress();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //изменение поворота для выделеного изображения
        if (mEditPhotoBar.getCurrentMode() == Constants.ROTATE_MODE) {
            rotateChange(seekBar,progress);
        } else if (mEditPhotoBar.getCurrentMode() == Constants.TRANSPARENCY_MODE) {
           transparencyChange(seekBar,progress);
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
                    mCropperImageNumber = 2;
                }
                break;
        }
        setFocusOnLayout(true);
    }

    //изменение поворота для выделеного изображения
    private void rotateChange(SeekBar seekBar,int progress){
        seekBar.setMax(Constants.MAX_ROTATE_PROGRESS);
        switch (mFocusedImage) {
            case Constants.MINI_LEFT_IMAGE_FOCUS_NUMBER:
                mLeftBigImageRotateAngle = progress * Constants.STEP__ROTATE_PROGRESS;
                mBigLeftImageView.setRotation(mLeftBigImageRotateAngle);
                break;
            case Constants.MINI_RIGHT_IMAGE_FOCUS_NUMBER:
                mRightBigImageRotateAngle = progress * Constants.STEP__ROTATE_PROGRESS;
                mBigRightImageView.setRotation(mRightBigImageRotateAngle);
                break;
        }
    }

    //изменение прозрачности для выделеного изображения
    private void transparencyChange(SeekBar seekBar,int progress){
        seekBar.setMax(Constants.MAX_TRANSPARENCY_PROGRESS);
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

    //получение обрезаного левого или правого изображений
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

    // возвращает наложенное изображение
    private Bitmap createOverlayImageBitmap() {
        return mOverlayImageSingleton.createNewOverlayImage(mLeftImageBitmap,
                mRightImageBitmap, mLeftBigImageRotateAngle, mRightBigImageRotateAngle,
                mLeftMiniImageProgressAlpha, mRightMiniImageProgressAlpha);
    }

    //вставляем обрезанное изображение в Cropper для его подальшей обрезки
    public void overlayImageCropper() {
        Bitmap tempCropperBitmap = mOverlayImageSingleton.createNewOverlayImage(mLeftCropperImageBitmap,
                mRightCropperImageBitmap, mLeftBigImageRotateAngle, mRightBigImageRotateAngle,
                mLeftMiniImageProgressAlpha, mRightMiniImageProgressAlpha);
        mCropperImage.setImageBitmap(tempCropperBitmap);
    }

    //выделение выбраного окошка
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
        // Inflate the menu
        getMenuInflater().inflate(R.menu.share_menu, menu);
        //отображение кнопри перехода к предыдущему активити
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
                // сохранение полученного изображения в Instagram
                fileSaveHelper.createInstagramIntent();
                break;
            case R.id.menu_item_save:
                saveImage();
                break;
        }
        return true;
    }

    //сохранение изображения на SD
    private void saveImage() {
        //получение обрезаного или полного изображения
        if (mEditPhotoBar.getCurrentMode() == Constants.CROPPER_MODE) {
            mNewOverlayImageBitmap = mCropperImage.getCroppedImage();
        } else {
            mNewOverlayImageBitmap = createOverlayImageBitmap();
        }
        // сохранение полученного изображения на SD
        fileSaveHelper.saveImageOnSD(mNewOverlayImageBitmap);
    }

    //переключение между режимами обработки изображений
    @Override
    public void onSelectedMode(int currentMode) {
        setCropperImage();
        mCropperImageNumber = 0;
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
        setViewVisibility(mCropperImage, false);
    }

    private void transparencyImage() {
        setFocusOnLayout(true);
        setViewVisibility(mCropperImage, false);
    }

    private void cropperImage() {
        setFocusOnLayout(false);
        setViewVisibility(mCropperImage, true);
        //обрезка наложеного изображения
        overlayImageCropper();
    }

    private void setViewVisibility(View view, Boolean isVisible) {
        if (isVisible) {
            mCropperImage.setVisibility(View.INVISIBLE);
            mImageProgressSeekBar.setVisibility(View.INVISIBLE);
            mBigLeftImageView.setVisibility(View.INVISIBLE);
            mBigRightImageView.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
        } else {
            mCropperImage.setVisibility(View.VISIBLE);
            mImageProgressSeekBar.setVisibility(View.VISIBLE);
            mBigLeftImageView.setVisibility(View.VISIBLE);
            mBigRightImageView.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
        }
    }
}
