package com.richydave.qoutes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomSupportMapFragment extends SupportMapFragment {

    private OnTouchListener mTouchListener;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, container, bundle);
        CustomFrameLayout frameLayout = new CustomFrameLayout(getActivity());
        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ((ViewGroup)view).addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    public interface OnTouchListener {
        void onTouch();
    }

    public void setOnTouchListener(OnTouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public class CustomFrameLayout extends FrameLayout {
        public CustomFrameLayout(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchListener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    mTouchListener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}

