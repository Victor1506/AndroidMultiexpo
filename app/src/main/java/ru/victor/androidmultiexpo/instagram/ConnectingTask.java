package ru.victor.androidmultiexpo.instagram;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ru.victor.androidmultiexpo.activity.GettingTwoImagesActivity;

/**
 * Created by Виктор on 22.02.2016.
 */

// получение списока ссылок на изображения из Instagram
public class ConnectingTask extends AsyncTask<Void, Void, ArrayList<String>> {

    private static final String INSTAGRAM_IMAGE_SIZE = "standard_resolution";

    public interface OnImagesURlGettingListener {
        void onImagesURlGetting(ArrayList<String> arr);
    }

    private OnImagesURlGettingListener mOnImagesURlGettingListener;
    private String mAccessToken;

    public void setOnImagesURlGettingListener(OnImagesURlGettingListener onImagesURlGettingListener) {
        this.mOnImagesURlGettingListener = onImagesURlGettingListener;
    }

    public ConnectingTask(String accessToken) {
        mAccessToken = accessToken;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("tag", "begin connect");
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        // получаем список ссылок на изображения из instagram используя access token
        return getImageURL(mAccessToken);
    }

    @Override
    protected void onPostExecute(ArrayList<String> url) {
        super.onPostExecute(url);
        GettingTwoImagesActivity.hideProgress();
        Log.d("tag", "end connect ");
        mOnImagesURlGettingListener.onImagesURlGetting(url);
    }

    // получаем список ссылок на изображения из instagram
    private ArrayList<String> getImageURL(String token) {
        ArrayList<String> urlString = new ArrayList<String>();
        if (token != null) {
            try {
                Log.i("tag", "" + token);

                URL example = new URL("https://api.instagram.com/v1/users/self/media/recent?access_token="
                        + token);

                URLConnection tc = example.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        tc.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    JSONObject ob = new JSONObject(line);

                    JSONArray object = ob.getJSONArray("data");

                    for (int i = 0; i < object.length(); i++) {


                        JSONObject jo = (JSONObject) object.get(i);
                        JSONObject nja = (JSONObject) jo.getJSONObject("images");

                        JSONObject purl3 = (JSONObject) nja
                                .getJSONObject(INSTAGRAM_IMAGE_SIZE);
                        URL url = new URL(purl3.getString("url"));

                        urlString.add(purl3.getString("url"));
                        Log.i("tag", "" + purl3.getString("url"));
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return urlString;
    }
}
