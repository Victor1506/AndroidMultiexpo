package ui.view;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import ru.victor.androidmultiexpo.R;
import ru.victor.androidmultiexpo.helper.Constants;


/**
 * Created by Виктор on 14.03.2016.
 */
public class EditPhotoBar extends RelativeLayout implements View.OnClickListener {

    public interface WorkModeListener {
        void onSelectedMode(int currentMode);
    }

    private WorkModeListener mModeListener;
    private ImageButton mRotateModeImageButton;
    private ImageButton mTransparencyModeImageButton;
    private ImageButton mCropperModeImageButton;
    private int mCurrentMode;

    public void setCustomObjectListener(WorkModeListener modeListener) {
        this.mModeListener = modeListener;
    }

    public EditPhotoBar(Context context) {
        super(context);
        this.mModeListener = null;
        initComponent();
        setTransparencyMode();
    }

    private void initComponent() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.edit_photo_bar, this);

        mRotateModeImageButton = (ImageButton) findViewById(R.id.rotateModeImageButton);
        mTransparencyModeImageButton = (ImageButton) findViewById(R.id.transparencyModeImageButton);
        mCropperModeImageButton = (ImageButton) findViewById(R.id.cropperModeImageButton);

        mRotateModeImageButton.setOnClickListener(this);
        mTransparencyModeImageButton.setOnClickListener(this);
        mCropperModeImageButton.setOnClickListener(this);
    }

    public int getCurrentMode() {
        return mCurrentMode;
    }

    private void setRotateMode() {
        mRotateModeImageButton.setBackground(ContextCompat.getDrawable(getContext(),
                R.drawable.oval_button));
        mCurrentMode = Constants.ROTATE_MODE;
        mModeListener.onSelectedMode(mCurrentMode);
    }

    private void setTransparencyMode() {
        mTransparencyModeImageButton.setBackground(ContextCompat.getDrawable(getContext(),
                R.drawable.oval_button));
        mCurrentMode = Constants.TRANSPARENCY_MODE;
        if (mModeListener != null) {
            mModeListener.onSelectedMode(mCurrentMode);
        }
    }

    private void setCropperMode() {
        mCropperModeImageButton.setBackground(ContextCompat.getDrawable(getContext(),
                R.drawable.oval_button));
        mCurrentMode = Constants.CROPPER_MODE;
        mModeListener.onSelectedMode(mCurrentMode);
    }

    //убираем фокус с кнопок
    private void clearButtonFocus() {
        mRotateModeImageButton.setBackground(ContextCompat.getDrawable(getContext(),
                R.color.colorGrey));
        mTransparencyModeImageButton.setBackground(ContextCompat.getDrawable(getContext(),
                R.color.colorGrey));
        mCropperModeImageButton.setBackground(ContextCompat.getDrawable(getContext(),
                R.color.colorGrey));
    }

    @Override
    public void onClick(View v) {
        clearButtonFocus();
        switch (v.getId()) {
            case R.id.rotateModeImageButton:
                setRotateMode();
                break;
            case R.id.transparencyModeImageButton:
                setTransparencyMode();
                break;
            case R.id.cropperModeImageButton:
                setCropperMode();
                break;
        }
    }
}