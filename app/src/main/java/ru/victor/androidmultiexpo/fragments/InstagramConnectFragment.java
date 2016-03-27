package ru.victor.androidmultiexpo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.instagram.InstagramSocialNetwork;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ru.victor.androidmultiexpo.R;
import ru.victor.androidmultiexpo.activity.GettingTwoImagesActivity;
import ru.victor.androidmultiexpo.helper.Constants;
import ru.victor.androidmultiexpo.instagram.ConnectingTask;
import ru.victor.androidmultiexpo.instagram.ImageAdapter;


public class InstagramConnectFragment extends Fragment implements OnLoginCompleteListener,
        SocialNetworkManager.OnInitializationCompleteListener, ConnectingTask.OnImagesURlGettingListener,
        AdapterView.OnItemClickListener {

    private static final String INSTAGRAM_CLIENT_KEY = "20478ef48f764ac3b8c8a15399b5c52c";
    private static final String INSTAGRAM_CLIENT_SECRET = "274546c957e04b2bb4fe59c45e423890";
    private static final String REDIRECT_URL = "https://vk.com/id64987011";
    private static final String instagramScope = "basic+public_content+follower_list+comments+relationships+likes";

    private SocialNetworkManager mSocialNetworkManager;
    private ConnectingTask mConnectingTask;
    private SocialNetwork mSocialNetwork;
    private GridView mImagesGridView;
    private ImageAdapter mInstagramImageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_instagram_connect, container, false);

        mImagesGridView = (GridView) rootView.findViewById(R.id.instagraGridView);
        mImagesGridView.setOnItemClickListener(this);

        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(Constants.SOCIAL_NETWORK_TAG);
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            InstagramSocialNetwork instagramNetwork = new InstagramSocialNetwork(this,
                    INSTAGRAM_CLIENT_KEY, INSTAGRAM_CLIENT_SECRET, REDIRECT_URL, instagramScope);
            mSocialNetworkManager.addSocialNetwork(instagramNetwork);

            getFragmentManager().beginTransaction().add(mSocialNetworkManager, Constants.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);

            // loading social person
            mSocialNetwork = mSocialNetworkManager.getSocialNetwork(InstagramSocialNetwork.ID);
            mSocialNetwork.setOnLoginCompleteListener(this);
            if (!mSocialNetwork.isConnected()) {
                mSocialNetwork.requestLogin();
            } else {
                connectToInstagram();
            }
        }
        return rootView;
    }

    private void connectToInstagram() {
        String accessToken = mSocialNetwork.getAccessToken().token;
        //подключение к Instagram
        mConnectingTask = new ConnectingTask(accessToken);
        mConnectingTask.execute();
        mConnectingTask.setOnImagesURlGettingListener(this);
        Log.d("tag", mSocialNetwork.getAccessToken().token);
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
        GettingTwoImagesActivity.hideProgress();
        Log.d("tag", "success");
        connectToInstagram();
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        GettingTwoImagesActivity.hideProgress();
        Toast.makeText(getContext(), "не удалось подключится к Instagram",
                Toast.LENGTH_SHORT).show();
        Log.d("tag", "error");
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            Log.d("tag", "onSocialNetworkManagerInitialized");

        }
    }

    @Override
    public void onImagesURlGetting(ArrayList<String> arr) {
        Log.d("tag", "onImagesURlGetting");
        try {
            mInstagramImageAdapter = new ImageAdapter(getContext(), mConnectingTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        mImagesGridView.setAdapter(mInstagramImageAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // возвращаем ссылку на изображение из Instagram в GettingTwoImagesActivity
        Intent intent = new Intent();
        intent.putExtra(Constants.INSTAGRAM_EXTRA, mInstagramImageAdapter.getUrlString(position));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
