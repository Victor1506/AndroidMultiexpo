package ru.victor.androidmultiexpo.helper;

import android.graphics.Color;

/**
 * Created by Виктор on 27.03.2016.
 */
public class Constants {
    //размер загружаемых изображений
    public static final int IMAGE_LOW_HIGHT = 200;
    public static final int IMAGE_LOW_WIGHT = 200;
    public static final int IMAGE_MEDIUM_HIGHT = 1024;
    public static final int IMAGE_MEDIUM_WIGHT = 720;
    //градус поворота окошек для GettingTwoImagesActivity
    public static final int TOP_IMAGE_ROTATE_ANGLE = -15;
    public static final int BOTTOM_IMAGE_ROTATE_ANGLE = 15;
    //параметры всплывающего меню для GettingTwoImagesActivity
    public static final int FLOATING_ACTION_MENU_RADIUS = 300;
    public static final int FLOATING_BUTTON_SIZE = 90;
    public static final int TOP_ACTION_MENU_START_ANGLE = -35;
    public static final int TOP_ACTION_MENU_END_ANGLE = 5;
    public static final int BOTTOM_ACTION_MENU_START_ANGLE = 160;
    public static final int BOTTOM_ACTION_MENU_END_ANGLE = 200;
    //request codes for startActivityForResult in GettingTwoImagesActivity
    public static final int FIRST_PHOTO_GALLERY_REQUEST = 1;
    public static final int SECOND_PHOTO_GALLERY_REQUEST = 2;
    public static final int FIRST_PHOTO_INSTAGRAM_REQUEST = 3;
    public static final int SECOND_PHOTO_INSTAGRAM_REQUEST = 4;
    //intent extra для верхнего и нижнего изображений
    public static final String FIRST_IMAGE_URI = "topImageURI";
    public static final String SECOND_IMAGE_URI = "bottomImageURI";

    //intent extra for GettingTwoImagesActivity
    public static final String INSTAGRAM_EXTRA = "link";
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    //размер загружаемых с Instagram изображений
    public static final String INSTAGRAM_IMAGE_SIZE = "standard_resolution";
    //размер изображений для GridView в InstagramActivity
    public static final int IMAGE_IN_GRID_SIZE = 350;

    //фокус на нажатом изображении
    public static final int MINI_LEFT_IMAGE_FOCUS_NUMBER = 1;
    public static final int MINI_RIGHT_IMAGE_FOCUS_NUMBER = 2;
    public static final int FOCUS_COLOR_GRAY = Color.parseColor("#BDBDBD");
    public static final int FOCUS_COLOR_WHITE = Color.parseColor("#ffffff");

    //максимальные значения для seekBar в разных режимах
    public static final int MAX_TRANSPARENCY_PROGRESS = 255;
    public static final int MAX_ROTATE_PROGRESS = 4;
    public static final int STEP__ROTATE_PROGRESS = 90;

    //режими работы с изображениями
    public static final int ROTATE_MODE = 1;
    public static final int TRANSPARENCY_MODE = 2;
    public static final int CROPPER_MODE = 3;
}
