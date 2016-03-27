package ru.victor.androidmultiexpo.instagram;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ru.victor.androidmultiexpo.helper.Constants;

/**
 * Created by Виктор on 22.02.2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mUriStringArr = null;

    public ImageAdapter(Context context, ArrayList<String> url) {
        mContext = context;
        mUriStringArr = url;
    }

    public String getUrlString(int position){
        return mUriStringArr.get(position);
    }

    @Override
    public int getCount() {
        return mUriStringArr.size();
    }

    @Override
    public Object getItem(int position) {
        return mUriStringArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        Glide.with(mContext)
                .load(mUriStringArr.get(position))
                .into(imageView);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(Constants.IMAGE_IN_GRID_SIZE,
                Constants.IMAGE_IN_GRID_SIZE));
        return imageView;
    }

}
