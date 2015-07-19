package com.licheedev.blurviewproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.licheedev.blurviewproject.R;

/**
 * Created by John on 2015/7/19.
 */
public class ImageFragment extends Fragment {


    public ImageFragment() {
    }

    public static ImageFragment getInstance(int resId) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt("resid", resId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageView imageView = (ImageView) inflater.inflate(R.layout.fragment_image, container, false);
        int res = getArguments().getInt("resid");
        imageView.setImageResource(res);
        return imageView;
    }
}
