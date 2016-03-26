package ru.victor.androidmultiexpo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import ru.victor.androidmultiexpo.R;

public class GettingTwoImagesActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int LOADING_IMAGE_HIGHT = 200;
    private static final int LOADING_IMAGE_WIGHT = 200;
    private static final int TOP_IMAGE_ROTATE_ANGLE = -15;
    private static final int BOTTOM_IMAGE_ROTATE_ANGLE = 15;

    public static final int FIRST_PHOTO_GALLERY_REQUEST = 1;
    public static final int SECOND_PHOTO_GALLERY_REQUEST = 2;
    public static final int FIRST_PHOTO_INSTAGRAM_REQUEST = 3;
    public static final int SECOND_PHOTO_INSTAGRAM_REQUEST = 4;
    public static final String FIRST_IMAGE_URI = "firstURI";
    public static final String SECOND_IMAGE_URI = "secondURI";

    private static ProgressDialog sProgressDialog;
    private LinearLayout mTopWindowLinearLayout;
    private LinearLayout mBottomWindowLinearLayout;
    private ImageView mTopImageView;
    private ImageView mBottomImageView;
    private Intent mRedactorActivityIntent;
    private Intent mInstagramActivityIntent;
    private FloatingActionMenu mActionMenuFirstPhoto;
    private FloatingActionMenu mActionMenuSecondPhoto;
    private  ImageButton mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_two_images);

        mTopWindowLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutFrameFirst);
        mBottomWindowLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutFrameSecond);
        mTopImageView = (ImageView) findViewById(R.id.PhotoViewFirst);
        mBottomImageView = (ImageView) findViewById(R.id.PhotoViewSecond);
        mNextButton = (ImageButton) findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(this);

        // поворот окошек на указанные градусы
        rotateImages(TOP_IMAGE_ROTATE_ANGLE, BOTTOM_IMAGE_ROTATE_ANGLE);

        //создание всплывающих кнопок
        setFloatingButtonMenu();

        mInstagramActivityIntent = new Intent(this, InstagramImageActivity.class);
        mRedactorActivityIntent = new Intent(this, RedactorActivity.class);
    }

    //ProgressDialog с уведомлением загрузки
    public static void showProgress(String message, Context context) {
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sProgressDialog.setMessage(message);
        sProgressDialog.setCancelable(false);
        sProgressDialog.setCanceledOnTouchOutside(false);
        sProgressDialog.show();
    }

    //закрытие ProgressDialog
    public static void hideProgress() {
        sProgressDialog.dismiss();
    }

    //поворот окошек
    private void rotateImages(int firstFrame, int secondFrame) {
        mTopWindowLinearLayout.setRotation(firstFrame);
        mBottomWindowLinearLayout.setRotation(secondFrame);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // загрузка возвращаемого значения и помещение ссылки на него в mRedactorActivityIntent
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FIRST_PHOTO_GALLERY_REQUEST:
                    setBitmapIntoImageView(data.getDataString(), mTopImageView);
                    mRedactorActivityIntent.putExtra(FIRST_IMAGE_URI, data.getData().toString());
                    break;
                case FIRST_PHOTO_INSTAGRAM_REQUEST:
                    setBitmapIntoImageView(data.getStringExtra(InstagramImageActivity.INSTAGRAM_EXTRA), mTopImageView);
                    mRedactorActivityIntent.putExtra(FIRST_IMAGE_URI,
                            data.getStringExtra(InstagramImageActivity.INSTAGRAM_EXTRA));
                    break;
                case SECOND_PHOTO_GALLERY_REQUEST:
                    setBitmapIntoImageView(data.getDataString(), mBottomImageView);
                    mRedactorActivityIntent.putExtra(SECOND_IMAGE_URI, data.getData().toString());
                    break;
                case SECOND_PHOTO_INSTAGRAM_REQUEST:
                    setBitmapIntoImageView(data.getStringExtra(InstagramImageActivity.INSTAGRAM_EXTRA), mBottomImageView);
                    mRedactorActivityIntent.putExtra(SECOND_IMAGE_URI,
                            data.getStringExtra(InstagramImageActivity.INSTAGRAM_EXTRA));
                    break;

            }
        }
    }

    private void setBitmapIntoImageView(String uri, ImageView imageView) {
        Glide.with(this)
                .load(uri)
                .override(LOADING_IMAGE_WIGHT, LOADING_IMAGE_HIGHT)
                .into(imageView);
    }

    // создание меню с всплывающими кнопками
    protected void setFloatingButtonMenu() {
        final Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(90, 90));
        itemBuilder.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.oval_button));

        // создание первой кнопки меню и обработка ее нажатия
        SubActionButton topImageInstagramButton = createActionButton(itemBuilder,
                R.drawable.ic_instagram);
        topImageInstagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // GettingTwoImagesActivity.showProgress("connecting...", GettingTwoImagesActivity.this);
                startActivityForResult(mInstagramActivityIntent, FIRST_PHOTO_INSTAGRAM_REQUEST);
                mActionMenuFirstPhoto.close(true);
                if (mActionMenuSecondPhoto.isOpen()) mActionMenuSecondPhoto.close(true);
            }
        });

        // создание второй кнопки меню и обработка ее нажатия
        SubActionButton topImageGalleryButton = createActionButton(itemBuilder,
                R.drawable.ic_gallery);
        topImageGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(photoPickerIntent, FIRST_PHOTO_GALLERY_REQUEST);
                mActionMenuFirstPhoto.close(true);
                if (mActionMenuSecondPhoto.isOpen()) mActionMenuSecondPhoto.close(true);
            }
        });

        // создание третей кнопки меню и обработка ее нажатия
        SubActionButton bottomImageInstagramButton = createActionButton(itemBuilder,
                R.drawable.ic_instagram);
        bottomImageInstagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(mInstagramActivityIntent, SECOND_PHOTO_INSTAGRAM_REQUEST);
                mActionMenuSecondPhoto.close(true);
                if (mActionMenuFirstPhoto.isOpen()) mActionMenuFirstPhoto.close(true);
              //  GettingTwoImagesActivity.showProgress("connecting...", GettingTwoImagesActivity.this);
            }
        });

        // создание четвертой кнопки меню и обработка ее нажатия
        SubActionButton bottomImageGalleryButton = createActionButton(itemBuilder,
                R.drawable.ic_gallery);
        bottomImageGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(photoPickerIntent, SECOND_PHOTO_GALLERY_REQUEST);
                mActionMenuSecondPhoto.close(true);
                if (mActionMenuFirstPhoto.isOpen()) mActionMenuFirstPhoto.close(true);
            }
        });

        //создание всплывающего меню для первого окошка
        mActionMenuFirstPhoto = new FloatingActionMenu.Builder(this)
                .addSubActionView(topImageInstagramButton)
                .addSubActionView(topImageGalleryButton)
                .attachTo(mTopWindowLinearLayout)
                .setRadius(300)
                .setStartAngle(-35)
                .setEndAngle(5)
                .build();

        //создание всплывающего меню для второго окошка
        mActionMenuSecondPhoto = new FloatingActionMenu.Builder(this)
                .addSubActionView(bottomImageGalleryButton)
                .addSubActionView(bottomImageInstagramButton)
                .attachTo(mBottomWindowLinearLayout)
                .setRadius(300)
                .setStartAngle(160)
                .setEndAngle(200)
                .build();
    }

    private SubActionButton createActionButton(SubActionButton.Builder itemBuilder, int imageRes) {
        SubActionButton subActionButton;
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageResource(imageRes);
        subActionButton = itemBuilder.setContentView(itemIcon).build();
        return subActionButton;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextButton:
                buttonNext();
                break;
        }
    }

    //отображение и обработка нажатия кнопки перехода если оба окошка заполненны
    private void buttonNext(){
        if (mTopImageView.getDrawable() != null && mBottomImageView.getDrawable() != null) {
            startActivity(mRedactorActivityIntent);
            if (mActionMenuFirstPhoto.isOpen() || mActionMenuSecondPhoto.isOpen()) {
                mActionMenuFirstPhoto.close(true);
                mActionMenuSecondPhoto.close(true);
            }
        } else
            Toast.makeText(GettingTwoImagesActivity.this, "не выбрано изображение", Toast.LENGTH_LONG).show();
    }
}


