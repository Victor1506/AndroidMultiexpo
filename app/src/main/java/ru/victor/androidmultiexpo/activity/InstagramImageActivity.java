package ru.victor.androidmultiexpo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ru.victor.androidmultiexpo.R;
import ru.victor.androidmultiexpo.fragments.InstagramConnectFragment;
import ru.victor.androidmultiexpo.helper.Constants;

public class InstagramImageActivity extends AppCompatActivity{

    private InstagramConnectFragment mInstagramConnectFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_image);

        GettingTwoImagesActivity.showProgress("connect...", this);

        mInstagramConnectFragment = new InstagramConnectFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mInstagramConnectFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(Constants.SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
            Log.d("tag", "activity result " + fragment.getClass());
        }
    }
}