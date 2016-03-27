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
import ru.victor.androidmultiexpo.helper.Constants;

public class GettingTwoImagesActivity extends AppCompatActivity implements View.OnClickListener {

    private static ProgressDialog sProgressDialog;
    private LinearLayout mTopWindowLinearLayout;
    private LinearLayout mBottomWindowLinearLayout;
    private ImageView mTopImageView;
    private ImageView mBottomImageView;
    private Intent mRedactorActivityIntent;
    private Intent mInstagramActivityIntent;
    private FloatingActionMenu mActionMenuFirstPhoto;
    private FloatingActionMenu mActionMenuSecondPhoto;
    private ImageButton mNextButton;
    private Intent mPhotoPickerIntent;

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

        mPhotoPickerIntent = new Intent(Intent.ACTION_PICK);
        mPhotoPickerIntent.setType("image/*");
        mInstagramActivityIntent = new Intent(this, InstagramImageActivity.class);
        mRedactorActivityIntent = new Intent(this, RedactorActivity.class);

        // поворот окошек на указанные градусы
        rotateImages(Constants.TOP_IMAGE_ROTATE_ANGLE, Constants.BOTTOM_IMAGE_ROTATE_ANGLE);

        //создание всплывающих кнопок
        setFloatingButtonMenu();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // загрузка изображения и помещение ссылки на него для mRedactorActivityIntent
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.FIRST_PHOTO_GALLERY_REQUEST:
                    setBitmapIntoImageView(data.getDataString(), mTopImageView);
                    mRedactorActivityIntent.putExtra(Constants.FIRST_IMAGE_URI, data.getData().toString());
                    break;
                case Constants.FIRST_PHOTO_INSTAGRAM_REQUEST:
                    setBitmapIntoImageView(data.getStringExtra(Constants.INSTAGRAM_EXTRA), mTopImageView);
                    mRedactorActivityIntent.putExtra(Constants.FIRST_IMAGE_URI,
                            data.getStringExtra(Constants.INSTAGRAM_EXTRA));
                    break;
                case Constants.SECOND_PHOTO_GALLERY_REQUEST:
                    setBitmapIntoImageView(data.getDataString(), mBottomImageView);
                    mRedactorActivityIntent.putExtra(Constants.SECOND_IMAGE_URI, data.getData().toString());
                    break;
                case Constants.SECOND_PHOTO_INSTAGRAM_REQUEST:
                    setBitmapIntoImageView(data.getStringExtra(Constants.INSTAGRAM_EXTRA), mBottomImageView);
                    mRedactorActivityIntent.putExtra(Constants.SECOND_IMAGE_URI,
                            data.getStringExtra(Constants.INSTAGRAM_EXTRA));
                    break;
            }
        }
    }

    //загрузка изображений в указаный ImageView
    private void setBitmapIntoImageView(String uri, ImageView imageView) {
        Glide.with(this)
                .load(uri)
                .override(Constants.IMAGE_LOW_WIGHT, Constants.IMAGE_LOW_HIGHT)
                .into(imageView);
    }

    // создание меню с всплывающими кнопками
    protected void setFloatingButtonMenu() {
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(Constants.FLOATING_BUTTON_SIZE,
                Constants.FLOATING_BUTTON_SIZE));
        itemBuilder.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.oval_button));

        //создание первого меню
        createTopActionMenu(itemBuilder);
        //создание второго меню
        createBottomActionMenu(itemBuilder);
    }

    //создание первого всплывающего меню
    private void createTopActionMenu(SubActionButton.Builder itemBuilder) {
        //создание кнопки доступа к Instagram
        SubActionButton topImageInstagramButton = createActionButton(itemBuilder,
                R.drawable.ic_instagram, R.id.topInstagram);
        //создание кнопки доступа к Галереи
        SubActionButton topImageGalleryButton = createActionButton(itemBuilder,
                R.drawable.ic_gallery, R.id.topGallery);

        //создание меню
        mActionMenuFirstPhoto = new FloatingActionMenu.Builder(this)
                .addSubActionView(topImageInstagramButton)
                .addSubActionView(topImageGalleryButton)
                .attachTo(mTopWindowLinearLayout)
                .setRadius(Constants.FLOATING_ACTION_MENU_RADIUS)
                .setStartAngle(Constants.TOP_ACTION_MENU_START_ANGLE)
                .setEndAngle(Constants.TOP_ACTION_MENU_END_ANGLE)
                .build();
    }

    //создание второго всплывающего меню
    private void createBottomActionMenu(SubActionButton.Builder itemBuilder) {
        //создание кнопки доступа к Instagram
        SubActionButton bottomImageInstagramButton = createActionButton(itemBuilder,
                R.drawable.ic_instagram, R.id.bottomInstagram);
        //создание кнопки доступа к Галереи
        SubActionButton bottomImageGalleryButton = createActionButton(itemBuilder,
                R.drawable.ic_gallery, R.id.bottomGallery);

        //создание меню
        mActionMenuSecondPhoto = new FloatingActionMenu.Builder(this)
                .addSubActionView(bottomImageGalleryButton)
                .addSubActionView(bottomImageInstagramButton)
                .attachTo(mBottomWindowLinearLayout)
                .setRadius(Constants.FLOATING_ACTION_MENU_RADIUS)
                .setStartAngle(Constants.BOTTOM_ACTION_MENU_START_ANGLE)
                .setEndAngle(Constants.BOTTOM_ACTION_MENU_END_ANGLE)
                .build();
    }

    //создание всплывающей кнопеи для меню
    private SubActionButton createActionButton(SubActionButton.Builder itemBuilder,
                                               int imageRes, int id) {
        SubActionButton subActionButton;
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageResource(imageRes);
        subActionButton = itemBuilder.setContentView(itemIcon).build();
        subActionButton.setId(id);
        subActionButton.setOnClickListener(this);
        return subActionButton;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topInstagram:
                startActivityForResult(mInstagramActivityIntent, Constants.FIRST_PHOTO_INSTAGRAM_REQUEST);
                mActionMenuFirstPhoto.close(true);
                if (mActionMenuSecondPhoto.isOpen()) mActionMenuSecondPhoto.close(true);
                break;
            case R.id.topGallery:
                startActivityForResult(mPhotoPickerIntent, Constants.FIRST_PHOTO_GALLERY_REQUEST);
                mActionMenuFirstPhoto.close(true);
                if (mActionMenuSecondPhoto.isOpen()) mActionMenuSecondPhoto.close(true);
                break;
            case R.id.bottomInstagram:
                startActivityForResult(mInstagramActivityIntent, Constants.SECOND_PHOTO_INSTAGRAM_REQUEST);
                mActionMenuSecondPhoto.close(true);
                if (mActionMenuFirstPhoto.isOpen()) mActionMenuFirstPhoto.close(true);
                break;
            case R.id.bottomGallery:
                startActivityForResult(mPhotoPickerIntent,Constants.SECOND_PHOTO_GALLERY_REQUEST);
                mActionMenuSecondPhoto.close(true);
                if (mActionMenuFirstPhoto.isOpen()) mActionMenuFirstPhoto.close(true);
                break;
            case R.id.nextButton:
                buttonNext();
                break;
        }
    }

    //отображение и обработка нажатия кнопки перехода если оба окошка заполненны
    private void buttonNext() {
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


