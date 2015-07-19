package com.licheedev.blurviewproject.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.licheedev.blurviewproject.R;
import com.licheedev.blurviewproject.listener.ToBlurListener;

/**
 * Created by John on 2015/7/19.
 */
public class ScrollFragment extends Fragment {

    private ToBlurListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (ToBlurListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scroll, container, false);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        scrollView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                mListener.toBlur();
                return false;
            }
        });

        return view;
    }
}
